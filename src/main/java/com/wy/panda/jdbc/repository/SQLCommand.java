package com.wy.panda.jdbc.repository;

import java.lang.reflect.Method;

import com.wy.panda.jdbc.common.SQLOption;
import com.wy.panda.jdbc.repository.annotation.Delete;
import com.wy.panda.jdbc.repository.annotation.Insert;
import com.wy.panda.jdbc.repository.annotation.Select;
import com.wy.panda.jdbc.repository.annotation.Update;

public class SQLCommand {
	
	private Class<?> repositoryInterface;
	private Method method;
	private SQLOption sqlType;
	private String sql;
	
	public SQLCommand(Class<?> repositoryInterface, Method method) {
		this.repositoryInterface = repositoryInterface;
		this.method = method;
		
		parse();
	}

	private void parse() {
		Select select = method.getAnnotation(Select.class);
		if (select != null) {
			sqlType = SQLOption.SELECT;
			sql = select.value();
			return;
		}
		
		Insert insert = method.getAnnotation(Insert.class);
		if (insert != null) {
			sqlType = SQLOption.INSERT;
			sql = insert.value();
			return;
		}
		
		Delete delete = method.getAnnotation(Delete.class);
		if (delete != null) {
			sqlType = SQLOption.DELETE;
			sql = delete.value();
			return;
		}
		
		Update update = method.getAnnotation(Update.class);
		if (update != null) {
			sqlType = SQLOption.UPDATE;
			sql = update.value();
			return;
		}
		
		throw new RuntimeException("unkown SQL type:" + repositoryInterface.getName() + "." + method.getName());
	}

	public SQLOption getSqlType() {
		return sqlType;
	}

	public void setSqlType(SQLOption sqlType) {
		this.sqlType = sqlType;
	}

	public String getSql() {
		return sql;
	}

	public void setSql(String sql) {
		this.sql = sql;
	}
	
}
