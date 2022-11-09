package com.panda.framework.jdbc.param.impl;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.panda.framework.jdbc.param.ColumnValueHandler;

public class StringParamValueSetter implements ColumnValueHandler{

	@Override
	public void setParamValue(PreparedStatement ps, int index, Object value) throws SQLException {
		ps.setString(index, (String) value);
	}

	@Override
	public Object getResultValue(ResultSet rs, int index) throws SQLException {
		return rs.getString(index);
	}

}
