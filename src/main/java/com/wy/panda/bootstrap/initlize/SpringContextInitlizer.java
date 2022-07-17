package com.wy.panda.bootstrap.initlize;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.wy.panda.bootstrap.ServerConfig;
import com.wy.panda.mvc.ServletContext;
import com.wy.panda.spring.ObjectFactory;

public class SpringContextInitlizer implements ContextInitlizer {
	
	private ApplicationContext applicationContext;

	@Override
	public void initContext(ServletContext servletContext, ServerConfig config) throws Exception {
		initSpringContext(config);
		
		// 在ServletContext中设置spring引用
		servletContext.addAttribute(ServletContext.ATTRIBUTE_SPRING_APPLICATION_CONTEXT, applicationContext);
		
		// 设置spring环境
		ObjectFactory.setApplicationContext(applicationContext);
	}

	private void initSpringContext(ServerConfig config) {
		// 初始化spring
		this.applicationContext = new ClassPathXmlApplicationContext(config.getApplicationContextPath());
	}
	
	

}
