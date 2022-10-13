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

import com.wy.panda.common.JdbcUtils;
import com.wy.panda.jdbc.memory.dynamic.DynamicUpdate;

import com.wy.panda.concurrent.ScheduledThread;
import com.wy.panda.jdbc.common.SQLOption;
import com.wy.panda.jdbc.entity.TableEntity;
import com.wy.panda.log.Logger;
import com.wy.panda.log.LoggerFactory;

public class AsyncSQLManager {
	
	private static final Logger log = LoggerFactory.getLogger("async");
	
	private static final AsyncSQLManager instance = new AsyncSQLManager();
	
	public static AsyncSQLManager getInstance() {
		return instance;
	}
	
	private AtomicInteger state = new AtomicInteger(0);
	/** 数据源 */
	private DataSource dataSource;
	/** 阻塞队列 */
	private BlockingQueue<SQLEntity> queue = new LinkedBlockingQueue<>();
	
	/** SQL接收线程 */
	private ScheduledThread thread = null;
	/** SQL执行线程 */
	private ScheduledThread[] workers = null;
	
	/** 一个批处理的上限 */
	private static final int BATCH_SIZE_THRESHOLD = 64;
	
	public void init(DataSource dataSource) {
		if (isStarted()) {
			return;
		}
		
		this.dataSource = dataSource;

		int workerNum = 4;
		FlushTask[] workerTasks = new FlushTask[workerNum];
		for (int i = 0; i < workerTasks.length; i++) {
			workerTasks[i] = new FlushTask();
		}
		
		TaskDispatcher dispatcher = new TaskDispatcher(workerTasks);
		thread = new ScheduledThread("AsyncSQLDispatcher", dispatcher, 100);
		thread.start();
		
		int interval = 200;
		workers = new ScheduledThread[workerTasks.length];
		for (int i = 0; i < workers.length; i++) {
			workers[i]  = new ScheduledThread("AsyncSQLWorker_" + i, workerTasks[i], interval);
		}
		for (int i = 0; i < workers.length; i++) {
			workers[i].start();
		}
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void addSQLEntity(SQLOption op, TableEntity tableEntity, int count, Object... args) {
		String tableName = tableEntity.getTableName();
		long currentThreadId = Thread.currentThread().getId();

		try {
			switch (op) {
			case INSERT: {
				String insertSQL = tableEntity.fillInsertSQLParams(args[0]);
				log.info("#{}#{}#{}#{}", tableName, count, insertSQL, 1);
				queue.add(new SQLEntity(op, insertSQL, count, tableName, currentThreadId));
			}
				break;
			case DELETE: {
				String deleteSQL = tableEntity.fillDeleteSQLParams(args[0]);
				log.info("#{}#{}#{}#{}", tableName, count, deleteSQL, 1);
				queue.add(new SQLEntity(op, deleteSQL, count, tableName, currentThreadId));
			}
				break;
			case UPDATE: {
				DynamicUpdate dynamicUpdateObject = (DynamicUpdate) args[1];
				String dynamicUpdateSQL = dynamicUpdateObject.getDynamicUpdateSQL(args[0], args[1], tableEntity);
				if (dynamicUpdateSQL == null || dynamicUpdateSQL.isEmpty()) {
					log.info("#{}#{}#{}#{}", tableName, count, dynamicUpdateSQL, 0);
				} else {
					log.info("#{}#{}#{}#{}", tableName, count, dynamicUpdateSQL, 1);
					queue.add(new SQLEntity(op, dynamicUpdateSQL, count, tableName, currentThreadId));
				}
			}
				break;
			default:
				throw new RuntimeException("unknown sql op type: " + op.name());
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

		public TaskDispatcher(FlushTask[] workers) {
			this.workers = workers;
		}

		@Override
		public void run() {
			// 每次循环时，将queue中的当前存入的数据全部派发出去
			while (queue.size() > 0) {
				SQLEntity entry = queue.poll();
				if (entry == null) {
					break;
				}

				FlushTask worker = workers[Math.abs((int) (entry.getHashCode() % workers.length))];
				worker.dispatch(entry);
			}
		}
		
	}
	
	class FlushTask implements Runnable {
		
		/** sql缓存池 */
		private LinkedBlockingQueue<SQLEntity> pendingQueue = new LinkedBlockingQueue<>();
		
		public void dispatch(SQLEntity sqlEntity) {
			pendingQueue.add(sqlEntity);
		}
		
		@Override
		public void run() {
			List<SQLEntity> list = new ArrayList<>();
			while (list.size() < BATCH_SIZE_THRESHOLD) {
				SQLEntity sqlEntity = pendingQueue.poll();
				if (sqlEntity == null) {
					break;
				}

				list.add(sqlEntity);
			}
			if (list.size() == 0) {
				return;
			}

			Connection connection = null;
			Statement stmt = null;
			try {
				connection = dataSource.getConnection();
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
				JdbcUtils.close(connection, stmt);
			}
		}
		
	}
	
}
