package com.wy.panda.mvc;

import java.util.HashMap;
import java.util.Map;

public class ServletContext {

	/** Spring context的存储key */
	public static final String ATTRIBUTE_SPRING_APPLICATION_CONTEXT = "attribute.spring.application.context";
	
	/** 属性map */
	private Map<String, Object> attributes = new HashMap<>();
	
	public ServletContext() {
	}
	
	public void addAttribute(String attributeName, Object value) {
		attributes.put(attributeName, value);
	}
	
	public Object getAttribute(String attributeName) {
		return attributes.get(attributeName);
	}
}
