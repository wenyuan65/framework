package com.wy.panda.jdbc.param;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public interface ColumnValueHandler {

	public void setParamValue(PreparedStatement ps, int index, Object value) throws SQLException;
	
	public Object getResultValue(ResultSet rs, int index) throws SQLException;
	
}
