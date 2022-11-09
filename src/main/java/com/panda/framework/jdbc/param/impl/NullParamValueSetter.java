package com.panda.framework.jdbc.param.impl;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.panda.framework.jdbc.param.ColumnValueHandler;

public class NullParamValueSetter implements ColumnValueHandler {

	private int sqlType;

	public NullParamValueSetter(int sqlType) {
		this.sqlType = sqlType;
	}
	
	@Override
	public void setParamValue(PreparedStatement ps, int index, Object value) throws SQLException {
		ps.setNull(index, sqlType);
	}

	@Override
	public Object getResultValue(ResultSet rs, int index) throws SQLException {
		return rs.getObject(index);
	}

}
