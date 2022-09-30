package com.wy.panda.jdbc.memory.enhance;

import com.wy.panda.jdbc.entity.TableEntity;

public interface DomainEnhancer {

	/** enhance类的后缀名 */
	static final String ENHANCED_CLASS_NAME_SUFFIX = "$EnhanceByPanda";

	public <V> Class<V> enhance(Class<V> clazz, TableEntity tableEntity);
	
}
