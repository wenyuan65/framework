package com.wy.panda.jdbc.repository;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import com.wy.panda.jdbc.common.SQLOption;

public class RepositoryMethod<T> {
	
	private SQLCommand sqlCommand;
	private MethodSignature methodSignature;

	public RepositoryMethod(Class<T> repositoryInterface, Method method) {
		this.sqlCommand = new SQLCommand(repositoryInterface, method);
		this.methodSignature = new MethodSignature(repositoryInterface, method);
		
		if (!RepositoryMethodLegalValidating.isValidate(sqlCommand.getSql(), method)) {
			throw new RuntimeException("illegal SQL format:" + repositoryInterface.getName() + "." + method.getName());
		}
	}

	public Object execute(DataSource dataSource, Object[] args) {
		String sql = sqlCommand.getSql();
		String finalSql = methodSignature.setArgsToSqlCommandParam(sql, args);
		
		Object result = null;
		SQLOption sqlType = sqlCommand.getSqlType();
		if (sqlType == SQLOption.SELECT) {
			result = executeQuery(dataSource, finalSql);
		} else {
			result = executeUpdate(dataSource, finalSql);
		}

		return result;
	}
	
	private int executeUpdate(DataSource dataSource, String sql) {
		try {
			Connection connection = dataSource.getConnection();
			PreparedStatement ps = connection.prepareStatement(sql);
			return ps.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return -1;
	}

	private Object executeQuery(DataSource dataSource, String sql) {
		ResultSet rs = null;
		try {
			Connection connection = dataSource.getConnection();
			PreparedStatement ps = connection.prepareStatement(sql);
			rs = ps.executeQuery();
			
			return methodSignature.getResult(rs);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				rs.close();
			} catch (SQLException e) {
			}
		}
		
		return null;
	}

}
