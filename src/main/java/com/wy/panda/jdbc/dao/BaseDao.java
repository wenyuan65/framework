package com.wy.panda.jdbc.dao;

import com.wy.panda.common.JdbcUtils;
import com.wy.panda.jdbc.TableFactory;
import com.wy.panda.jdbc.entity.TableEntity;
import com.wy.panda.jdbc.param.Params;
import com.wy.panda.log.Logger;
import com.wy.panda.log.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

import javax.sql.DataSource;
import java.lang.reflect.ParameterizedType;
import java.sql.ResultSet;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public abstract class BaseDao<K, V> implements Dao<K, V>, InitializingBean {

	private static Logger log = LoggerFactory.getLogger(Dao.class);

	@Autowired
	protected TableFactory tableFactory;
	protected DataSource dataSource;

	protected TableEntity tableEntity;
	
	@Override
	public void afterPropertiesSet() throws Exception {
		this.dataSource = tableFactory.getDataSource();

		ParameterizedType pt = (ParameterizedType)this.getClass().getGenericSuperclass();
		String parameterizedClazzName = pt.getActualTypeArguments()[1].getTypeName();
		this.tableEntity = tableFactory.getTableMap().get(parameterizedClazzName);
	}

	@Override
	public void create(V obj) {
		JdbcUtils.execute(this.dataSource, this.tableEntity.getInsertSQL(), ps -> {
			this.tableEntity.setInsertSQLParams(ps, obj);
			return ps.executeUpdate();
		});
	}

	@Override
	public void delete(K key) {
		JdbcUtils.execute(this.dataSource, this.tableEntity.getDeleteSQL(), ps -> {
			this.tableEntity.setDeleteSQLParams(ps, key);
			return ps.executeUpdate();
		});
	}

	@Override
	public void update(V obj) {
		JdbcUtils.execute(this.dataSource, this.tableEntity.getUpdateSQL(), ps -> {
			this.tableEntity.setUpdateSQLParams(ps, obj);
			return ps.executeUpdate();
		});
	}

	@Override
	public V read(K id) {
		return JdbcUtils.execute(this.dataSource, this.tableEntity.getSelectSQL(), ps -> {
			this.tableEntity.setQuerySQLParams(ps, id);
			ResultSet rs = ps.executeQuery();

			List<V> result;
			try {
				result = this.tableEntity.getResult(rs);
			} finally {
				JdbcUtils.close(rs);
			}

			return result.size() > 0 ? result.get(0) : null;
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
		return JdbcUtils.execute(this.dataSource, realSQL, ps -> {
			params.appendParams(ps);
			ResultSet rs = ps.executeQuery();
			List<V> result;
			try {
				result = this.tableEntity.getResult(rs);
			} finally {
				JdbcUtils.close(rs);
			}
			return result != null ? result : Collections.emptyList();
		});
	}

	@Override
	public V selectOne(String sql, Params params) {
		List<V> list = select(sql, params);
		return list.size() > 0 ? list.get(0) : null;
	}

	@Override
	public int update(String sql, Params params) {
		if (params == null || sql == null) {
			throw new RuntimeException("参数异常");
		}

		String realSQL = getSQL(sql);
		Integer result = JdbcUtils.execute(this.dataSource, realSQL, ps -> {
			params.appendParams(ps);
			return ps.executeUpdate();
		});

		return result == null ? 0 : result.intValue();
	}

	@Override
	public Map<String, Object> selectForMap(String sql, Params params) {
		List<Map<String, Object>> selectForMapList = selectForMapList(sql, params);
		return selectForMapList.size() > 0 ? selectForMapList.get(0) : Collections.emptyMap();
	}

	@Override
	public List<Map<String, Object>> selectForMapList(String sql, Params params) {
		String realSQL = getSQL(sql);
		return JdbcUtils.execute(this.dataSource, realSQL, ps -> {
			params.appendParams(ps);
			ResultSet rs = ps.executeQuery();
			List<Map<String, Object>> resultMapList;
			try {
				resultMapList = this.tableEntity.getResultMap(rs);
			} finally {
				JdbcUtils.close(rs);
			}
			return resultMapList;
		});
	}
	
	@Override
	public int count(String sql, Params params) {
		String realSQL = getSQL(sql);
		Integer result = JdbcUtils.execute(this.dataSource, realSQL, ps -> {
			params.appendParams(ps);

			ResultSet rs = ps.executeQuery();
			int count = -1;
			try {
				rs.next();
				count = rs.getInt(1);
			} finally {
				JdbcUtils.close(rs);
			}

			return count;
		});

		return result == null ? 0 : result.intValue();
	}
	
	private String getSQL(String sql) {
		return tableFactory.getSql(sql);
	}

}
