package com.wy.panda.mvc;

import com.wy.panda.common.ScanUtil;
import com.wy.panda.exception.IllegalParametersException;
import com.wy.panda.log.Logger;
import com.wy.panda.log.LoggerFactory;
import com.wy.panda.mvc.annotation.Action;
import com.wy.panda.mvc.annotation.CommandMarker;
import com.wy.panda.mvc.annotation.RpcCommandMarker;
import com.wy.panda.mvc.config.DispatchServletConfig;
import com.wy.panda.mvc.domain.Request;
import com.wy.panda.mvc.domain.Response;
import com.wy.panda.mvc.intercept.Interceptor;
import com.wy.panda.mvc.result.ByteResult;
import com.wy.panda.mvc.result.NoActionResult;
import com.wy.panda.mvc.result.Result;
import com.wy.panda.rpc.RpcRequest;
import com.wy.panda.rpc.RpcResponse;
import com.wy.panda.spring.ObjectFactory;
import org.apache.commons.lang3.StringUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

public class DispatchServlet {

	protected static Logger log = LoggerFactory.getLogger(DispatchServlet.class);
	
	/** 配置 */
	private DispatchServletConfig servletConfig;
	
	/** 执行环境 */
	private ServletContext servletContext;
	
	private Map<String, Invoker> actionMap = new HashMap<>();
	private Map<Integer, Invoker> codeMap = new HashMap<>();
	private Map<Integer, RpcInvoker> rpcInvokerMap = new HashMap<>();

	/** 默认的返回 */
	private ByteResult defaultResult = new ByteResult(null);
	
	/** 拦截器 */
	private List<Interceptor> interceptors = new ArrayList<>();
	
	public DispatchServlet(DispatchServletConfig servletConfig, ServletContext servletContext) {
		this.servletConfig = servletConfig;
		this.servletContext = servletContext;
	}
	
	public void addInterceptors(List<Interceptor> interceptorList) {
		this.interceptors.addAll(interceptorList);
	}
	
	public void init() throws Throwable {
		initAction();
		initInterceptors();
	}

	private void initAction() throws Throwable {
		String[] pathArray = servletConfig.getScanPath().split(",");
		Set<Class<?>> classes = ScanUtil.scan(pathArray);
		for(Class<?> clazz : classes){
			try {
				initHandlers(clazz);
			} catch (Throwable e) {
				e.printStackTrace();
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
			obj = parseCommand(clazz, method, obj);
			obj = parseRpcCommand(clazz, method, obj);
		}
	}

	private Object parseCommand(Class<?> clazz, Method method, Object obj) throws Exception {
		Annotation[] annotations = method.getDeclaredAnnotations();
		for (Annotation annotation : annotations) {
			Class<? extends Annotation> annotationType = annotation.annotationType();
			CommandMarker commandMarkerAnnotation = annotationType.getAnnotation(CommandMarker.class);
			if (commandMarkerAnnotation == null) {
				continue;
			}

			Method value = annotationType.getDeclaredMethod("value");
			Object result = value.invoke(annotation); // cmd

			Field codeField = result.getClass().getDeclaredField(commandMarkerAnnotation.code());
			Field actionField = result.getClass().getDeclaredField(commandMarkerAnnotation.action());
			codeField.setAccessible(true);
			actionField.setAccessible(true);

			int code = (int)codeField.get(result);
			String commandName = (String)actionField.get(result);

			if (StringUtils.isBlank(commandName)) {
				String msg = String.format("command cannot be blank for %s.%s()", clazz.getName(), method.getName());
				throw new IllegalParametersException(msg);
			}
			if (actionMap.containsKey(commandName)) {
				String msg = String.format("command cannot be dumplicated for %s in %s.%s()", commandName, clazz.getName(), method.getName());
				throw new IllegalParametersException(msg);
			}
			if (codeMap.containsKey(code)) {
				String msg = String.format("command cannot be dumplicated for %s in %s.%s()", result.getClass().getSimpleName(), clazz.getName(), method.getName());
				throw new IllegalParametersException(msg);
			}

			// 创建action实例
			if (obj == null) {
				obj = ObjectFactory.getObject(clazz, null);
			}

			Invoker invoker = new Invoker(obj, method);
			invoker.init();
			actionMap.put(commandName, invoker);
			codeMap.put(code, invoker);

			log.info("init command:{}, handler:{}#{}", commandName, clazz.getSimpleName(), method.getName());

			break;
		}

		return obj;
	}

	private Object parseRpcCommand(Class<?> clazz, Method method, Object obj) throws Exception {
		Annotation[] annotations = method.getDeclaredAnnotations();
		for (Annotation annotation : annotations) {
			Class<? extends Annotation> annotationType = annotation.annotationType();
			RpcCommandMarker commandMarkerAnnotation = annotationType.getAnnotation(RpcCommandMarker.class);
			if (commandMarkerAnnotation == null) {
				continue;
			}

			Method value = annotationType.getDeclaredMethod("value");
			Object result = value.invoke(annotation); // cmd

			Field codeField = result.getClass().getDeclaredField(commandMarkerAnnotation.code());
			Field actionField = result.getClass().getDeclaredField(commandMarkerAnnotation.action());
			codeField.setAccessible(true);
			actionField.setAccessible(true);

			int code = (int)codeField.get(result);
			String commandName = (String)actionField.get(result);

			if (rpcInvokerMap.containsKey(code)) {
				String msg = String.format("command cannot be dumplicated for %s in %s.%s()", result.getClass().getSimpleName(), clazz.getName(), method.getName());
				throw new IllegalParametersException(msg);
			}

			// 创建action实例
			if (obj == null) {
				obj = ObjectFactory.getObject(clazz, null);
			}

			RpcInvoker invoker = new RpcInvoker(obj, method);
			invoker.init();
			rpcInvokerMap.put(code, invoker);

			log.info("init command:{}, handler:{}#{}", commandName, clazz.getSimpleName(), method.getName());

			break;
		}

		return obj;
	}

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

	public void dispatch(Request request, Response response) {
		Invoker invoker = null;
		if (request.getCode() > 0) {
			invoker = codeMap.get(request.getCode());
		} else {
			invoker = actionMap.get(request.getCommand());
		}

		Result result = null;
		if (invoker != null) {
			// 设置调用系统环境，让command执行过程中，可以获得applicationContext
			request.setServletContext(this.servletContext);
			// 执行command
			result = invoke(invoker, request, response);
		} else {
			result = new NoActionResult(request.getCommand());
		}
		
		// 结果处理
		if (result != null) {
			result.render(request, response);
		} else {
			result = new NoActionResult(request.getCommand());
			result.render(request, response);
		}
	}

	public void dispatch(RpcRequest request, RpcResponse response) {
		RpcInvoker invoker = rpcInvokerMap.get(request.getCommand());
		if (invoker == null) {
			return;
		}

		Object result = null;
		try {
			result = invoker.invoke(request);
			response.setResult(result);
		} catch (Throwable e) {
			response.setCause(e);
			log.error("invoke rpc error", e);
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
