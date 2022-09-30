package com.wy.panda.jdbc.entity.memory.enhance;

import com.wy.panda.common.SystemProperty;
import com.wy.panda.jdbc.entity.TableEntity;
import com.wy.panda.jdbc.entity.memory.dynamic.DynamicUpdate;

import java.util.HashMap;
import java.util.Map;

/**
 * @author wenyuan
 */
public class EnhanceUtil {

	private static final Map<String, DomainEnhancer> enhancer_Map = new HashMap<>();

	protected static boolean outputEnhancedClass = false;
	private static DomainEnhancer domainEnhancer = null;
	static {
		String printClass = System.getProperty(SystemProperty.ENHANCE_CLASS_OUTPUT, "false");
		outputEnhancedClass = "true".equalsIgnoreCase(printClass);

		enhancer_Map.put("asm", new CGLIBDomainEnhancer());
		enhancer_Map.put("cglib", new CGLIBDomainEnhancer());
		enhancer_Map.put("jdk", new CGLIBDomainEnhancer());

		String enhanceClassName = System.getProperty(SystemProperty.ENHANCE_CLASS_NAME, "asm");
		domainEnhancer = enhancer_Map.get(enhanceClassName);
	}
	
	@SuppressWarnings("unchecked")
	public static <V> Class<? extends V> enhance(Class<V> clazz, TableEntity tableEntity) throws Exception {
		if (DynamicUpdate.class.isAssignableFrom(clazz)) {
			return clazz;
		}

		return domainEnhancer.enhance(clazz, tableEntity);
	}

}
