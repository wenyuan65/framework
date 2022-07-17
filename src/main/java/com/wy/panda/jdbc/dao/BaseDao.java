package com.wy.panda.jdbc.dao;

import java.lang.reflect.ParameterizedType;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.jdbc.core.RowMapperResultSetExtractor;

import com.wy.panda.jdbc.TableFactory;
import com.wy.panda.jdbc.callback.UpdatePreparedStatementCallback;
import com.wy.panda.jdbc.entity.TableEntity;
import com.wy.panda.jdbc.param.Params;
import com.wy.panda.log.Logger;
import com.wy.panda.log.LoggerFactory;

public abstract class BaseDao<K, V> implements Dao<K, V>, InitializingBean {

	private static Logger log = LoggerFactory.getLogger(Dao.class);
	
	@Autowired
	protected TableFactory tableFactory;
	protected DataSource dataSource;
	protected JdbcTemplate template;
	
	protected TableEntity tableEntity;
	
	@Override
	public void afterPropertiesSet() throws Exception {
		this.dataSource = tableFactory.getDataSource();
		this.template = tableFactory.getTemplate();
		
		ParameterizedType pt = (ParameterizedType)this.getClass().getGenericSuperclass();
		String parameterizedClazzName = pt.getActualTypeArguments()[1].getTypeName();
		this.tableEntity = tableFactory.getTableMap().get(parameterizedClazzName);
	}

	@Override
	public void create(V obj) {
		final V tmp = obj;
		template.execute(this.tableEntity.getInsertSQL(), new PreparedStatementCallback<Integer>() {

			@Override
			public Integer doInPreparedStatement(PreparedStatement ps) throws SQLException, DataAccessException {
				try {
					BaseDao.this.tableEntity.setInsertSQLParams(ps, tmp);
				} catch (IllegalArgumentException | IllegalAccessException e) {
					log.error("insert sql param error", e);
				}
				return ps.executeUpdate();
			}
		});
	}

	@Override
	public void delete(K key) {
		final K tmp = key;
		template.execute(this.tableEntity.getDeleteSQL(), new PreparedStatementCallback<Integer>() {

			@Override
			public Integer doInPreparedStatement(PreparedStatement ps) throws SQLException, DataAccessException {
				try {
					BaseDao.this.tableEntity.setDeleteSQLParams(ps, tmp);
				} catch (IllegalArgumentException | IllegalAccessException e) {
					log.error("insert sql param error", e);
				}
				return ps.executeUpdate();
			}
		});
	}

	@Override
	public void update(V obj) {
		final V tmp = obj;
		template.execute(this.tableEntity.getUpdateSQL(), new PreparedStatementCallback<Integer>() {

			@Override
			public Integer doInPreparedStatement(PreparedStatement ps) throws SQLException, DataAccessException {
				try {
					BaseDao.this.tableEntity.setUpdateSQLParams(ps, tmp);
				} catch (IllegalArgumentException | IllegalAccessException e) {
					log.error("insert sql param error", e);
				}
				return ps.executeUpdate();
			}
		});
	}

	@Override
	public V read(K id) {
		final K tmp = id;
		return template.execute(this.tableEntity.getSelectSQL(), new PreparedStatementCallback<V>() {
			@Override
			@SuppressWarnings("unchecked")
			public V doInPreparedStatement(PreparedStatement ps) throws SQLException, DataAccessException {
				try {
					BaseDao.this.tableEntity.setQuerySQLParams(ps, tmp);
				} catch (IllegalArgumentException | IllegalAccessException e) {
					log.error("insert sql param error", e);
				}
				
				ResultSet rs = ps.executeQuery();
				List<V> result = null;
				try {
					result = (List<V>)BaseDao.this.tableEntity.getResult(rs);
				} catch (InstantiationException | IllegalAccessException e) {
					log.error("insert sql param error", e);
				}
				
				return result.size() > 0 ? result.get(0) : null;
			}
			
		});
	}

	@Override
	public List<V> selectAll() {
		String realSQL = this.tableEntity.getSelectAllSQL();
		return select(realSQL, Params.EMPTY);
	}
	
	@Override
	public List<V> select(String sql, Params params) {
		String realSQL = getSQL(sql);
		return template.execute(realSQL, new PreparedStatementCallback<List<V>>() {

			@Override
			@SuppressWarnings("unchecked")
			public List<V> doInPreparedStatement(PreparedStatement ps)
					throws SQLException, DataAccessException {
				params.appendParams(ps);
				ResultSet rs = ps.executeQuery();
				
				List<V> result = null;
				try {
					 result = (List<V>)BaseDao.this.tableEntity.getResult(rs);
				} catch (InstantiationException | IllegalAccessException e) {
					log.error("extract result error", e);
				} finally {
					rs.close();
				}
				return result != null ? result : Collections.emptyList();
			}
		});
	}

	@Override
	public V selectOne(String sql, Params params) {
		List<V> list = select(sql, params);
		return list.size() > 0 ? list.get(0) : null;
	}

	@Override
	public int update(String sql, Params params) {
		String realSQL = getSQL(sql);
		return template.execute(realSQL, new UpdatePreparedStatementCallback(params));
	}

	@Override
	public Map<String, Object> selectForMap(String sql, Params params) {
		List<Map<String, Object>> selectForMapList = selectForMapList(sql, params);
		return selectForMapList.size() > 0 ? selectForMapList.get(0) : Collections.emptyMap();
	}

	@Override
	public List<Map<String, Object>> selectForMapList(String sql, Params params) {
		String realSQL = getSQL(sql);
		return template.execute(realSQL, new PreparedStatementCallback<List<Map<String, Object>>>() {

			@Override
			public List<Map<String, Object>> doInPreparedStatement(PreparedStatement ps)
					throws SQLException, DataAccessException {
				params.appendParams(ps);
				ResultSet rs = ps.executeQuery();
//				ColumnMapRowMapper mapper = new ColumnMapRowMapper();
				List<Map<String, Object>> resultMapList = new ArrayList<>();
				
				ColumnMapRowMapper columnMapRowMapper = new ColumnMapRowMapper();
				RowMapperResultSetExtractor<Map<String, Object>> resultSetExtractor = new RowMapperResultSetExtractor<>(columnMapRowMapper, 4);
				try {
					resultMapList = resultSetExtractor.extractData(rs);
//					ResultSetMetaData metaData = rs.getMetaData();
//					int columnCount = metaData.getColumnCount();
//					while (rs.next()) {
//						Map<String, Object> rowMap = new LinkedCaseInsensitiveMap<>(columnCount);
//						for (int i = 1; i <= columnCount; i++) {
//							String columnName = metaData.getColumnName(i);
//							Object columnValue = rs.getObject(i);
//							rowMap.put(columnName, columnValue);
//						}
//						resultMapList.add(rowMap);
//					}
				} finally {
					rs.close();
				}
				return resultMapList;
			}
		});
	}
	
	@Override
	public int count(String sql, Params params) {
		String realSQL = getSQL(sql);
		return template.execute(realSQL, new PreparedStatementCallback<Integer>() {

			@Override
			public Integer doInPreparedStatement(PreparedStatement ps) throws SQLException, DataAccessException {
				params.appendParams(ps);
				ResultSet rs = ps.executeQuery();
				try {
					rs.next();
					return rs.getInt(1);
				} catch(Exception e) {
					throw e;
				} finally {
					rs.close();
				}
			}
		});
	}
	
	private String getSQL(String sql) {
		return tableFactory.getSql(sql);
	}
	
}
