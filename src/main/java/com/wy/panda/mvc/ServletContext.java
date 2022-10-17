package com.wy.panda.mvc;

import org.springframework.context.ApplicationContext;

import java.util.HashMap;
import java.util.Map;

public class ServletContext {

//	/** Spring context的存储key */
//	public static final String ATTRIBUTE_SPRING_APPLICATION_CONTEXT = "attribute.spring.application.context";

	/** mokuai  */
	private DispatchServlet servlet;
	/** spring context */
	private ApplicationContext applicationContext;

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

	public DispatchServlet getServlet() {
		return servlet;
	}

	public void setServlet(DispatchServlet servlet) {
		this.servlet = servlet;
	}

	public ApplicationContext getApplicationContext() {
		return applicationContext;
	}

	public void setApplicationContext(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}
}
