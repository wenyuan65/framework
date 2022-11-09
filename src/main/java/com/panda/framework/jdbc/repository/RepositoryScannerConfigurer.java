package com.panda.framework.jdbc.repository;

import java.lang.annotation.Annotation;
import java.util.Map;
import java.util.Objects;

import org.springframework.beans.BeansException;
import org.springframework.beans.PropertyValue;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.PropertyResourceConfigurer;
import org.springframework.beans.factory.config.TypedStringValue;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.util.StringUtils;

public class RepositoryScannerConfigurer implements BeanDefinitionRegistryPostProcessor, InitializingBean, ApplicationContextAware, BeanNameAware {

	private String basePackage;
	private String beanName;
	private ApplicationContext applicationContext;
	private boolean processPropertyPlaceHolders;
	private boolean addToConfig = true;
	private Class<? extends Annotation> annotationClass;
	private Class<?> markerInterface;
	private BeanNameGenerator nameGenerator;

	@Override
	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
	}

	@Override
	public void setBeanName(String name) {
		this.beanName = name;
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		Objects.requireNonNull(this.basePackage, "Property 'basePackage' is required");
	}

	@Override
	public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
		if (this.processPropertyPlaceHolders) {
			processPropertyPlaceHolders();
		}

		ClassPathRepositoryScanner scanner = new ClassPathRepositoryScanner(registry);
		scanner.setAddToConfig(this.addToConfig);
		scanner.setAnnotationClass(this.annotationClass);
		scanner.setMarkerInterface(this.markerInterface);
		scanner.setResourceLoader(this.applicationContext);
		scanner.setBeanNameGenerator(this.nameGenerator);
		scanner.registerFilters();
		scanner.scan(StringUtils.tokenizeToStringArray(this.basePackage, ConfigurableApplicationContext.CONFIG_LOCATION_DELIMITERS));
	}

	private void processPropertyPlaceHolders() {
		Map<String, PropertyResourceConfigurer> prcs = applicationContext
				.getBeansOfType(PropertyResourceConfigurer.class);

		if (!prcs.isEmpty() && applicationContext instanceof ConfigurableApplicationContext) {
			BeanDefinition repositoryScannerBean = ((ConfigurableApplicationContext) applicationContext)
					.getBeanFactory().getBeanDefinition(beanName);

			DefaultListableBeanFactory factory = new DefaultListableBeanFactory();
			factory.registerBeanDefinition(beanName, repositoryScannerBean);

			for (PropertyResourceConfigurer prc : prcs.values()) {
				prc.postProcessBeanFactory(factory);
			}

			PropertyValues values = repositoryScannerBean.getPropertyValues();

			this.basePackage = updatePropertyValue("basePackage", values);
		}
	}

	private String updatePropertyValue(String propertyName, PropertyValues values) {
		PropertyValue property = values.getPropertyValue(propertyName);

		if (property == null) {
			return null;
		}

		Object value = property.getValue();

		if (value == null) {
			return null;
		} else if (value instanceof String) {
			return value.toString();
		} else if (value instanceof TypedStringValue) {
			return ((TypedStringValue) value).getValue();
		} else {
			return null;
		}
	}

	public String getBasePackage() {
		return basePackage;
	}

	public void setBasePackage(String basePackage) {
		this.basePackage = basePackage;
	}

	public boolean isProcessPropertyPlaceHolders() {
		return processPropertyPlaceHolders;
	}

	public void setProcessPropertyPlaceHolders(boolean processPropertyPlaceHolders) {
		this.processPropertyPlaceHolders = processPropertyPlaceHolders;
	}

	public boolean isAddToConfig() {
		return addToConfig;
	}

	public void setAddToConfig(boolean addToConfig) {
		this.addToConfig = addToConfig;
	}

	public Class<? extends Annotation> getAnnotationClass() {
		return annotationClass;
	}

	public void setAnnotationClass(Class<? extends Annotation> annotationClass) {
		this.annotationClass = annotationClass;
	}

	public Class<?> getMarkerInterface() {
		return markerInterface;
	}

	public void setMarkerInterface(Class<?> markerInterface) {
		this.markerInterface = markerInterface;
	}

	public BeanNameGenerator getNameGenerator() {
		return nameGenerator;
	}

	public void setNameGenerator(BeanNameGenerator nameGenerator) {
		this.nameGenerator = nameGenerator;
	}

	public String getBeanName() {
		return beanName;
	}

	public ApplicationContext getApplicationContext() {
		return applicationContext;
	}

}
