package com.wy.panda.cache;

import java.util.List;

public interface SdataLoader {

	public <T> List<T> getTable(Class<T> clazz);
	
}
