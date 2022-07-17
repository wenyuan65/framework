package com.wy.panda.jdbc.repository;

import java.io.Serializable;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.ibatis.reflection.ExceptionUtil;

public class RepositoryProxy<T> implements InvocationHandler, Serializable {

	/**  */
	private static final long serialVersionUID = 1L;

	private DataSource dataSource;
	private Class<T> repositoryInterface;
	private Map<Method, RepositoryMethod<T>> methodCache = new HashMap<>();
	
	public RepositoryProxy(DataSource dataSource, Class<T> repositoryInterface) {
		this.dataSource = dataSource;
		this.repositoryInterface = repositoryInterface;
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		try {
			if (Object.class.equals(method.getDeclaringClass())) {
				return method.invoke(this, args);
			} else if (method.isDefault()) {
				return invokeDefaultMethod(proxy, method, args);
			}
		} catch (Throwable t) {
			throw ExceptionUtil.unwrapThrowable(t);
		}
		final RepositoryMethod<T> respositoryMethod = cachedRepositoryMethod(method);
		return respositoryMethod.execute(dataSource, args);
	}

	private RepositoryMethod<T> cachedRepositoryMethod(Method method) {
		RepositoryMethod<T> repositoryMethod = methodCache.get(method);
		if (repositoryMethod == null) {
			repositoryMethod = new RepositoryMethod<T>(repositoryInterface, method);
			methodCache.put(method, repositoryMethod);
		}
		return repositoryMethod;
	}

	private Object invokeDefaultMethod(Object proxy, Method method, Object[] args) throws Throwable {
		final Constructor<MethodHandles.Lookup> constructor = MethodHandles.Lookup.class
				.getDeclaredConstructor(Class.class, int.class);
		if (!constructor.isAccessible()) {
			constructor.setAccessible(true);
		}

		final Class<?> declaringClass = method.getDeclaringClass();
		return constructor.newInstance(declaringClass, MethodHandles.Lookup.PRIVATE | MethodHandles.Lookup.PROTECTED 
						| MethodHandles.Lookup.PACKAGE | MethodHandles.Lookup.PUBLIC)
				.unreflectSpecial(method, declaringClass).bindTo(proxy).invokeWithArguments(args);
	}

}
