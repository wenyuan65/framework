package com.wy.panda.spring;

import java.lang.reflect.Field;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

public class ObjectFactory {
	
//	private static final Logger logger = LoggerFactory.getLogger(ObjectFactory.class);
	
	private static ApplicationContext applicationContext = null;
	
	public static void setApplicationContext(ApplicationContext ctx) {
		applicationContext = ctx;
	}

	/**
	 * 获取spring bean
	 * @param clazz
	 * @param name
	 * @return
	 * @throws Exception 
	 */
	@SuppressWarnings("unchecked")
	public static <T> T getObject(Class<?> clazz, String name) throws Exception {
		Object bean = null;
		if (applicationContext != null) {
			try {
				bean = applicationContext.getBean(clazz);
			} catch (Exception e) {
//				logger.error("get spring bean error", e);
			}
			if (bean == null && StringUtils.isNotBlank(name)) {
				try {
					bean = applicationContext.getBean(name, clazz);
				} catch (Exception e) {
				}
			}
		}
		
		if (bean == null) {
			try {
				bean = clazz.newInstance();
			} catch (Exception e) {
			}
		}
		
		if (bean != null) {
			Field[] fields = clazz.getDeclaredFields();
			for (Field field : fields) {
				Autowired autowired = field.getAnnotation(Autowired.class);
				if (autowired != null) {
					Object value = getObject(field.getType(), field.getName());
					if (value == null) {
						String msg = String.format("cannot found bean %s autowired in %s", field.getName(), clazz.getName());
						throw new Exception(msg);
					}
					
					field.setAccessible(true);
					field.set(bean, value);
				}
			}
		}
		
		return (T)bean;
	}
	
}
