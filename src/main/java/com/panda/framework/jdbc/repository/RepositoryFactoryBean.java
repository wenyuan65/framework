package com.panda.framework.jdbc.repository;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

public class RepositoryFactoryBean<T> extends JdbcDaoSupport implements FactoryBean<T> {

	private Class<T> repositoryInterface;
	
	private boolean addToConfig = true;
	
	private volatile RepositoryProxyFactory<T> repositoryProxyFactory;
	
	public RepositoryFactoryBean() {
	}
	
	public RepositoryFactoryBean(Class<T> repositoryInterface) {
		super();
		this.repositoryInterface = repositoryInterface;
	}

	@Override
	public T getObject() throws Exception {
		if (repositoryProxyFactory == null) {
			synchronized (this) {
				if (repositoryProxyFactory == null) {
					repositoryProxyFactory = new RepositoryProxyFactory<>(repositoryInterface);
				}
			}
		}
		
		return repositoryProxyFactory.newInstance(getDataSource());
	}

	@Override
	public Class<?> getObjectType() {
		return repositoryInterface;
	}

	public boolean isAddToConfig() {
		return addToConfig;
	}

	public void setAddToConfig(boolean addToConfig) {
		this.addToConfig = addToConfig;
	}

}
