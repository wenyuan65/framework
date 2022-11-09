package com.panda.framework.netty.spring.namespace.parser;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.panda.framework.netty.config.NettyConfig;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.xml.DomUtils;
import org.w3c.dom.Element;

public class NettyBootstrapBeanDefinition {

	public static AbstractBeanDefinition getBeanDefinitionByElement(final Element element, ParserContext parserContext) {
		BeanDefinitionBuilder bdb = BeanDefinitionBuilder.rootBeanDefinition(NettyConfig.class);
		bdb.addPropertyValue("bossEventLoopNum", element.getAttribute(NettyConfig.BOSS_EVENTLOOP_NUM));
		bdb.addPropertyValue("workerEventLoopNum", element.getAttribute(NettyConfig.WORKER_EVENTLOOP_NUM));
		
		Element channelClassNameElement = DomUtils.getChildElementByTagName(element, NettyConfig.CHANNEL_CLASS);
		if (channelClassNameElement == null) {
			throw new RuntimeException("sad");
		}
		bdb.addPropertyValue("channelClassName", channelClassNameElement.getAttribute(NettyConfig.CLASS));
		
		Element options = DomUtils.getChildElementByTagName(element, NettyConfig.OPTIONS);
		if (options != null) {
			bdb.addPropertyValue("options", parseOptions(options));
		}
		
		Element childOptions = DomUtils.getChildElementByTagName(element, NettyConfig.CHILD_OPTIONS);
		if (childOptions != null) {
			bdb.addPropertyValue("childOptions", parseOptions(childOptions));
		}
		
		Element portElement = DomUtils.getChildElementByTagName(element, NettyConfig.PORT);
		bdb.addPropertyValue("port", portElement.getAttribute(NettyConfig.BIND));
		bdb.addPropertyValue("childHandlers", parseChildHandlerList(element, parserContext));
		
		return bdb.getBeanDefinition();
	}
	
	private static List<String> parseChildHandlerList(Element element, ParserContext parserContext) {
		Element childHandlersElement = DomUtils.getChildElementByTagName(element, NettyConfig.CHILD_HANDLERS);
		List<Element> childHandlerNameElementList = DomUtils.getChildElementsByTagName(childHandlersElement, NettyConfig.CHILD_HANDLER);
		
		List<String> channelHandlerBeanNameList = new ArrayList<>();
//		List<BeanDefinition> channelHandlerBeanDefinitionList = new ManagedList<>(childHandlerNameElementList.size());
		for (Element childHandlerElement : childHandlerNameElementList) {
			String beanName = childHandlerElement.getAttribute(NettyConfig.REF);
//			channelHandlerBeanDefinitionList.add(parserContext.getRegistry().getBeanDefinition(beanName));
			channelHandlerBeanNameList.add(beanName);
		}
		
		return channelHandlerBeanNameList;
//		return channelHandlerBeanDefinitionList;
	}

	public static Map<String, Object> parseOptions(final Element element) {
		
		return null;
	}
	
}
