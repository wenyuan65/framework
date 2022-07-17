package com.wy.panda.mvc;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.wy.panda.mvc.annotation.HttpCommand;
import com.wy.panda.mvc.common.ProtocolType;
import org.apache.commons.lang3.StringUtils;

import com.wy.panda.common.ScanUtil;
import com.wy.panda.exception.IllegalParametersException;
import com.wy.panda.log.Logger;
import com.wy.panda.log.LoggerFactory;
import com.wy.panda.mvc.annotation.Action;
import com.wy.panda.mvc.annotation.Command;
import com.wy.panda.mvc.config.DispatchServletConfig;
import com.wy.panda.mvc.domain.Request;
import com.wy.panda.mvc.domain.Response;
import com.wy.panda.mvc.intercept.Interceptor;
import com.wy.panda.mvc.result.ByteResult;
import com.wy.panda.mvc.result.NoActionResult;
import com.wy.panda.mvc.result.Result;
import com.wy.panda.spring.ObjectFactory;

public class DispatchServlet {

	protected static Logger log = LoggerFactory.getLogger(DispatchServlet.class);
	
	/** 配置 */
	private DispatchServletConfig servletConfig;
	
	/** 执行环境 */
	private ServletContext servletContext;
	
	/** 命令存储 */
	private Map<Integer, Invoker> actionMap = new HashMap<>();
	/** 命令存储 */
	private Map<String, Invoker> httpActionMap = new HashMap<>();

	/** 默认的返回 */
	private ByteResult defaultResult = new ByteResult(null);
	
	/** 拦截器 */
	private List<Interceptor> interceptors = new ArrayList<>();
	
	public DispatchServlet(DispatchServletConfig servletConfig, ServletContext servletContext) {
		this.servletConfig = servletConfig;
		this.servletContext = servletContext;
	}
	
	/**
	 * 添加拦截器
	 * @param interceptorList
	 */
	public void addInterceptors(List<Interceptor> interceptorList) {
		this.interceptors.addAll(interceptorList);
	}
	
	/**
	 * 初始化命令
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws IllegalParametersException
	 */
	public void init() throws Throwable {
		initAction();
		initInterceptors();
	}

	/**
	 * 初始化
	 * @throws IllegalParametersException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	private void initAction() throws Throwable {
		String[] pathArray = servletConfig.getScanPath().split(",");
		for (String path : pathArray) {
			Set<Class<?>> classes = ScanUtil.scan(path);
			for(Class<?> clazz : classes){
				try {
					initHandlers(clazz);
				} catch (Throwable e) {
					e.printStackTrace();
				}
			}
		}
	}

	private void initHandlers(Class<?> clazz) throws Throwable {
		if (Modifier.isAbstract(clazz.getModifiers()) || Modifier.isInterface(clazz.getModifiers())) {
			return;
		}
		
		Action action = clazz.getAnnotation(Action.class);
		if (action == null) {
			Class<?> superclass = clazz.getSuperclass();
			if (superclass == null) {
				return;
			}
			action = clazz.getSuperclass().getAnnotation(Action.class);
			if (action == null) {
				return;
			}
		}
		
		// action实例
		Object obj = null;
		Method[] methods = clazz.getDeclaredMethods();
		for (Method method : methods) {
			Command command = method.getAnnotation(Command.class);
			if(command == null){
				continue;
			}
			
			Integer commandId = command.value();
			if (commandId == 0) {
				String msg = String.format("command cannot be blank for %s.%s()", clazz.getName(), method.getName());
				throw new IllegalParametersException(msg);
			}
			if (actionMap.containsKey(commandId)) {
				String msg = String.format("command cannot be dumplicated for %s in %s.%s()", commandId, clazz.getName(), method.getName());
				throw new IllegalParametersException(msg);
			}
			
			// 创建action实例
			if (obj == null) {
				obj = ObjectFactory.getObject(clazz, null);
			}
			
			Invoker invoker = new Invoker(obj, method);
			invoker.init();
			actionMap.put(commandId, invoker);
			
			log.info("init command:{}, handler:{}#{}", commandId, clazz.getSimpleName(), method.getName());

			// http
			HttpCommand httpCommand = method.getAnnotation(HttpCommand.class);
			if(httpCommand == null){
				continue;
			}

			String commandName = httpCommand.value();
			if (StringUtils.isBlank(commandName)) {
				String msg = String.format("command cannot be blank for %s.%s()", clazz.getName(), method.getName());
				throw new IllegalParametersException(msg);
			}
			if (httpActionMap.containsKey(commandName)) {
				String msg = String.format("command cannot be dumplicated for %s in %s.%s()", commandName, clazz.getName(), method.getName());
				throw new IllegalParametersException(msg);
			}

			invoker = new Invoker(obj, method);
			invoker.init();
			httpActionMap.put(commandName, invoker);
		}
	}
	
	/**
	 * 设置拦截器顺序
	 */
	private void initInterceptors() {
		if (interceptors.size() <= 1) {
			return;
		}
		
		for (int i = 0; i < interceptors.size() - 1; i++) {
			Interceptor interceptor = interceptors.get(i);
			Interceptor next = interceptors.get(i + 1);
			interceptor.setNextInterceptor(next);
		}
	}

	/**
	 * 执行command
	 * @param request
	 * @param response
	 */
	public void dispatch(Request request, Response response) {
		Result result = null;
		Invoker invoker = null;
		if (request.getProtocol() == ProtocolType.TCP) {
			invoker = actionMap.get(request.getCommand());
		} else {
			invoker = httpActionMap.get(request.getHttpCommand());
		}

		if (invoker != null) {
			// 设置调用系统环境，让command执行过程中，可以获得applicationContext
			request.setServletContext(this.servletContext);
			// 执行command
			result = invoke(invoker, request, response);
		} else {
			result = new NoActionResult(String.valueOf(request.getCommand()));
		}
		
		// 结果处理
		if (result != null) {
			result.render(request, response);
		} else {
			result = new NoActionResult(String.valueOf(request.getCommand()));
		}
	}
	
	public Result invoke(Invoker invoker, Request request, Response response) {
		Result result = null;
		if (interceptors.size() > 0) {
			Interceptor interceptor = interceptors.get(0);
			result = (Result) interceptor.invoke(invoker, request, response);
		}
		
		return result;
	}
	
}
