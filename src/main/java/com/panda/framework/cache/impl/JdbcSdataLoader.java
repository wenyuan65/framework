package com.panda.framework.cache.impl;

import java.util.List;

import com.panda.framework.cache.SdataLoader;
import com.panda.framework.jdbc.TableFactory;

public class JdbcSdataLoader implements SdataLoader {
	
	private TableFactory tableFactory;
	
	public JdbcSdataLoader() {}

	@Override
	public <T> List<T> getTable(Class<T> clazz) {
		return tableFactory.selectAll(clazz);
	}

	public TableFactory getTableFactory() {
		return tableFactory;
	}

	public void setTableFactory(TableFactory tableFactory) {
		this.tableFactory = tableFactory;
	}
	
}
