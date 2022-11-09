package com.panda.framework.netty.spring.namespace.parser;

import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

import com.panda.framework.netty.NettyServerBootstrap;

public class NettyBootstrapBeanDefinitionParser extends AbstractBeanDefinitionParser {

	@Override
	protected AbstractBeanDefinition parseInternal(Element element, ParserContext parserContext) {
		AbstractBeanDefinition nettyConfigBeanDefinition = NettyBootstrapBeanDefinition.getBeanDefinitionByElement(element, parserContext);
		
		BeanDefinitionBuilder factory = BeanDefinitionBuilder.rootBeanDefinition(NettyServerBootstrap.class);
		factory.addConstructorArgValue(nettyConfigBeanDefinition);
		return factory.getBeanDefinition();
	}

}
