package com.wy.panda.jdbc.repository;

import java.lang.reflect.Proxy;

import javax.sql.DataSource;

public class RepositoryProxyFactory<T> {
	
	private final Class<T> repositoryInterface;

	public RepositoryProxyFactory(Class<T> repositoryInterface) {
		this.repositoryInterface = repositoryInterface;
	}

	public Class<T> getRepositoryInterface() {
		return repositoryInterface;
	}

	@SuppressWarnings("unchecked")
	protected T newInstance(RepositoryProxy<T> repositoryProxy) {
		return (T) Proxy.newProxyInstance(repositoryInterface.getClassLoader(), new Class[] { repositoryInterface },
				repositoryProxy);
	}

	public T newInstance(DataSource dataSource) {
		final RepositoryProxy<T> repositoryProxy = new RepositoryProxy<T>(dataSource, repositoryInterface);
		return newInstance(repositoryProxy);
	}

}
