package com.panda.framework.jdbc.param.impl;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;

import com.panda.framework.jdbc.param.ColumnValueHandler;

public class TimeParamValueSetter implements ColumnValueHandler{

	@Override
	public void setParamValue(PreparedStatement ps, int index, Object value) throws SQLException {
		java.util.Date date = (java.util.Date)value;
		java.sql.Time sqlTime = new java.sql.Time(date.getTime());
		ps.setTime(index, sqlTime);
	}

	@Override
	public Object getResultValue(ResultSet rs, int index) throws SQLException {
		Time time = rs.getTime(index);
		return new java.util.Date(time.getTime());
	}

}
