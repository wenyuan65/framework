package com.panda.framework.jdbc.memory.enhance;

import com.panda.framework.jdbc.entity.TableEntity;

public interface DomainEnhancer {

	/** enhance类的后缀名 */
	static final String ENHANCED_CLASS_NAME_SUFFIX = "$EnhanceByPanda";

	public <V> Class<V> enhance(Class<V> clazz, TableEntity tableEntity);
	
}
