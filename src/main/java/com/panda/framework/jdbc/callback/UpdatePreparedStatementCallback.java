package com.panda.framework.jdbc.callback;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.PreparedStatementCallback;

import com.panda.framework.jdbc.param.Params;

public class UpdatePreparedStatementCallback implements PreparedStatementCallback<Integer> {

	private Params params;

	public UpdatePreparedStatementCallback(Params params) {
		this.params = params;
	}
	
	@Override
	public Integer doInPreparedStatement(PreparedStatement ps) throws SQLException, DataAccessException {
		params.appendParams(ps);
		return ps.executeUpdate();
	}

}
