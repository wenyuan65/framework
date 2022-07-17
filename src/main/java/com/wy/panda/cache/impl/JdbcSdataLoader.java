package com.wy.panda.cache.impl;

import java.util.List;

import com.wy.panda.cache.SdataLoader;
import com.wy.panda.jdbc.TableFactory;

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
