package com.wy.panda.jdbc.memory;

import com.wy.panda.jdbc.common.SQLOption;

public class SQLEntity {

	/** sql类型 */
	private SQLOption option;
	
	private String sql;
	
	private int count;
	
	private String tableName;

	private long hashCode;
	
	public SQLEntity(SQLOption option, String sql, int count, String tableName, long hashCode) {
		super();
		this.option = option;
		this.sql = sql;
		this.count = count;
		this.tableName = tableName;
	}

	public SQLOption getOption() {
		return option;
	}

	public void setOption(SQLOption option) {
		this.option = option;
	}

	public String getSql() {
		return sql;
	}

	public void setSql(String sql) {
		this.sql = sql;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public long getHashCode() {
		return hashCode;
	}

	public void setHashCode(long hashCode) {
		this.hashCode = hashCode;
	}
}
