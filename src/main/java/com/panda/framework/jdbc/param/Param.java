package com.panda.framework.jdbc.param;

public class Param {

	private Object value;
	
	private int sqlType;

	public Param(Object value, int sqlType) {
		this.value = value;
		this.sqlType = sqlType;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public int getSqlType() {
		return sqlType;
	}

	public void setSqlType(int sqlType) {
		this.sqlType = sqlType;
	}
	
}
