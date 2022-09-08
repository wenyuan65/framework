package com.wy.panda.jdbc.entity.memory.enhance;

public interface DomainEnhancer {

	public <V> Class<V> enhance(Class<V> clazz);
	
}
