package com.wy.panda.jdbc.memory;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wy.panda.concurrent.ScheduledThread;
import com.wy.panda.jdbc.common.SQLOption;
import com.wy.panda.jdbc.entity.TableEntity;
import com.wy.panda.jdbc.memory.dynamic.DynamicUpdate;

public class AsyncSQLManager {
	
	private static final Logger log = LoggerFactory.getLogger("async");
	
	private static final AsyncSQLManager instance = new AsyncSQLManager();
	
	public static AsyncSQLManager getInstance() {
		return instance;
	}
	
	/** 
	 * 工作状态，
	 * 0未初始化，
	 * 1已经初始化，
	 * 2已经启动，
	 * 3可接受，不可入库，例如数据库连接异常， 
	 * 4不可接受，可入库，例如进程停止，等待退出，
	 * 6已经退出 
	 **/
	private AtomicInteger state = new AtomicInteger(0);
	/** 数据源 */
	private DataSource dataSource;
	/** 阻塞队列 */
	private BlockingQueue<SQLEntity> queue = new LinkedBlockingQueue<>();
	
	/** 定时线程 */
	private ScheduledThread thread = null;
	
	/** 一个批处理的上限 */
	private static final int BATCH_SIZE_THRESHOLD = 2000;
	
	public void init(DataSource dataSource) {
		if (isStarted()) {
			return;
		}
		
		this.dataSource = dataSource;
		
		FlushTask[] workers = new FlushTask[4];
		for (int i = 0; i < workers.length; i++) {
			workers[i] = new FlushTask();
		}
		
		TaskDispatcher dispatcher = new TaskDispatcher(workers);
		thread = new ScheduledThread("AsyncSQLDispatcher", 100, dispatcher);
		thread.start();
		
		int intevel = 200;
		for (int i = 0; i < workers.length; i++) {
			ScheduledThread worker = new ScheduledThread("AsyncSQLFlusher_" + (i + 1), 
					intevel, workers[i]);
			worker.start();
		} 
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void addSQLEntity(SQLOption op, TableEntity tableEntity, int count, Object... args) {
		String tableName = tableEntity.getTableName();
		try {
			switch (op) {
			case INSERT: {
				String insertSQL = tableEntity.fillInsertSQLParams(args[0]);
				log.info("#{}#{}#{}#{}", tableName, count, insertSQL, 1);
				queue.add(new SQLEntity(op, insertSQL, count, tableName));
			}
				break;
			case DELETE: {
				String deleteSQL = tableEntity.fillDeleteSQLParams(args[0]);
				log.info("#{}#{}#{}#{}", tableName, count, deleteSQL, 1);
				queue.add(new SQLEntity(op, deleteSQL, count, tableName));
			}
				break;
			case UPDATE: {
				DynamicUpdate dynamicUpdateObject = (DynamicUpdate) args[1];
				String dynamicUpdateSQL = dynamicUpdateObject.getDynamicUpdateSQL(args[0], args[1], tableEntity);
				log.info("#{}#{}#{}#{}", tableName, count, dynamicUpdateSQL, 1);
				queue.add(new SQLEntity(op, dynamicUpdateSQL, count, tableName));
			}
				break;
			default:
				throw new RuntimeException("unkow sql op type: " + op.name());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void flush() {
		
	}
	
	public boolean isStarted() {
		return state.get() >= 1;
	}
	
	class TaskDispatcher implements Runnable {
		
		private FlushTask[] workers;
		private int count = 0;

		public TaskDispatcher(FlushTask[] workers) {
			this.workers = workers;
		}

		@Override
		public void run() {
			// FIXME：线程失败问题解决
			SQLEntity entry = null;
			try {
				while ((entry = queue.poll()) != null) {
					FlushTask worker = workers[count ++ % workers.length];
					worker.dispatch(entry);
				} 
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
	}
	
	class FlushTask implements Runnable {
		
		/** sql缓存池 */
//		private DoubleBufferQueue<SQLEntity> queue = new DoubleBufferQueue<>(1024);
//		private CopyOnWriteArrayList<SQLEntity> pendingQueue = new CopyOnWriteArrayList<>();
		private LinkedBlockingQueue<SQLEntity> pendingQueue = new LinkedBlockingQueue<>(1024);
		
		public void dispatch(SQLEntity sqlEntity) {
			pendingQueue.add(sqlEntity);
		}
		
		@Override
		public void run() {
			// TODO: SQL不是顺序的执行
			List<SQLEntity> list = new ArrayList<>();
			SQLEntity sqlEntity = null;
			int currSize = 0;
			while ((sqlEntity = pendingQueue.poll()) != null && currSize < BATCH_SIZE_THRESHOLD) {
				list.add(sqlEntity);
				currSize++;
			}
			if (currSize == 0) {
				return;
			}
			
			Statement stmt = null;
			try {
				Connection connection = dataSource.getConnection();
				stmt = connection.createStatement();
				for (SQLEntity entity : list) {
					stmt.addBatch(entity.getSql());
				}
				
				int[] result = stmt.executeBatch();
				for (int i = 0; i < result.length; i++) {
					SQLEntity entity = list.get(i);
					int s = result[i];
					if (s > 0) {
						log.info("#{}#{}#{}#", entity.getTableName(), entity.getCount(), 2);
					}
				}
			} catch (SQLException e) {
				log.error("FlushTask error", e);
			} finally {
				try {
					stmt.close();
				} catch (SQLException e) {
				}
			}
		}
		
	}
	
}
