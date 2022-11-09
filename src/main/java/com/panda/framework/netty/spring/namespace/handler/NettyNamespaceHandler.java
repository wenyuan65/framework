package com.panda.framework.netty.spring.namespace.handler;

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

import com.panda.framework.netty.spring.namespace.parser.NettyBootstrapBeanDefinitionParser;

public class NettyNamespaceHandler extends NamespaceHandlerSupport {

	public void init() {
		registerBeanDefinitionParser("bootstrap", new NettyBootstrapBeanDefinitionParser());
	}

}
