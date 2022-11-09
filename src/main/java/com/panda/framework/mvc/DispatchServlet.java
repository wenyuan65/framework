package com.panda.framework.mvc;

import com.panda.framework.common.ScanUtil;
import com.panda.framework.concurrent.DefaultThreadFactory;
import com.panda.framework.exception.IllegalParametersException;
import com.panda.framework.log.LoggerFactory;
import com.panda.framework.mvc.annotation.Action;
import com.panda.framework.mvc.annotation.CommandMarker;
import com.panda.framework.mvc.annotation.RpcCommandMarker;
import com.panda.framework.mvc.config.DispatchServletConfig;
import com.panda.framework.mvc.domain.Request;
import com.panda.framework.mvc.domain.Response;
import com.panda.framework.mvc.intercept.Interceptor;
import com.panda.framework.mvc.result.NoActionResult;
import com.panda.framework.mvc.result.Result;
import com.panda.framework.rpc.RpcRequest;
import com.panda.framework.rpc.RpcResponse;
import com.panda.framework.spring.ObjectFactory;
import com.panda.framework.log.Logger;
import com.panda.framework.mvc.annotation.Bind;
import org.apache.commons.lang3.StringUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.concurrent.*;

public class DispatchServlet {

	protected static Logger log = LoggerFactory.getLogger(DispatchServlet.class);
	
	/** 配置 */
	private DispatchServletConfig servletConfig;
	
	/** 执行环境 */
	private ServletContext servletContext;
	
	private Map<String, Invoker> actionMap = new HashMap<>();
	private Map<Integer, Invoker> codeMap = new HashMap<>();
	private Map<Integer, RpcInvoker> rpcInvokerMap = new HashMap<>();

	/** 拦截器 */
	private List<Interceptor> interceptors = new ArrayList<>();

	/** 核心线程 */
	private ThreadPoolExecutor[] coreThreadPools = null;
	/** 异步线程 */
	private ThreadPoolExecutor asyncThreadPools = null;
	/** 掩码 */
	private int mark;
	/** 关闭 */
	private volatile boolean shutdown = false;

	public DispatchServlet(DispatchServletConfig servletConfig, ServletContext servletContext) {
		this.servletConfig = servletConfig;
		this.servletContext = servletContext;
	}
	
	public void addInterceptors(List<Interceptor> interceptorList) {
		this.interceptors.addAll(interceptorList);
	}
	
	public void init() throws Throwable {
		initContext();
		initAction();
		initInterceptors();
		initThreadPools();
	}

	private void initContext() {
		servletContext.setServlet(this);
	}

	private void initThreadPools() {
		int coreThreadPoolSize = servletConfig.getCoreThreadPoolSize();
		if ((coreThreadPoolSize & (coreThreadPoolSize - 1)) == 0) {
			int shift = 32 - Integer.numberOfLeadingZeros(coreThreadPoolSize - 1);
			coreThreadPoolSize = 1 << shift;
		}
		coreThreadPoolSize = Math.max(coreThreadPoolSize, 8);
		coreThreadPoolSize = Math.min(coreThreadPoolSize, 256);
		mark = coreThreadPoolSize - 1;

		coreThreadPools = new ThreadPoolExecutor[coreThreadPoolSize];
		DefaultThreadFactory coreThreadFactory = new DefaultThreadFactory("CoreThreadPools", coreThreadPoolSize);
		for (int i = 0; i < coreThreadPools.length; i++) {
			coreThreadPools[i] = new ThreadPoolExecutor(1, 1, 60, TimeUnit.SECONDS,
					new LinkedBlockingQueue<>(), coreThreadFactory);
		}
		log.info("init core Thread pool, size:{}", coreThreadPoolSize);

		int poolSize = servletConfig.getAsyncThreadPoolSize();
		asyncThreadPools = new ThreadPoolExecutor(poolSize, poolSize, 60, TimeUnit.SECONDS,
				new LinkedBlockingQueue<>(), new DefaultThreadFactory("AsyncThreadPools", poolSize));
		log.info("init async Thread pool, size:{}", poolSize);
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
			return;
		}
		
		// action实例
		Object obj = null;
		Method[] methods = clazz.getDeclaredMethods();
		for (Method method : methods) {
			String[] bindSources = getBindSources(method);

			obj = parseCommand(clazz, method, obj, bindSources);
			obj = parseRpcCommand(clazz, method, obj, bindSources);
		}
	}

	private Object parseCommand(Class<?> clazz, Method method, Object obj, String[] bindSources) throws Exception {
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
			invoker.initBindSource(bindSources);

			actionMap.put(commandName, invoker);
			codeMap.put(code, invoker);
			log.info("init command:{}, handler:{}#{}", commandName, clazz.getSimpleName(), method.getName());

			break;
		}

		return obj;
	}

	private String[] getBindSources(Method method) {
		Bind bind = method.getAnnotation(Bind.class);

		if (bind == null) {
			return new String[]{ "playerId" };
		} else {
			return bind.value();
		}
	}

	private Object parseRpcCommand(Class<?> clazz, Method method, Object obj, String[] bindSources) throws Exception {
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
			invoker.initBindSource(bindSources);
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
		if (shutdown) {
			Result result = new NoActionResult(request.getCommand());
			result.render(request, response);
			return;
		}

		final Invoker invoker;
		if (request.getCode() > 0) {
			invoker = codeMap.get(request.getCode());
		} else {
			invoker = actionMap.get(request.getCommand());
		}

		if (invoker != null) {
			Object[] param = invoker.getAdaptor().adapt(request, response);
			request.setParam(param);

			Object[] bindValues = invoker.getAdaptor().adaptBindSources(param);
			int hash = Objects.hash(bindValues);

			coreThreadPools[hash & mark].execute(() -> {
				// 设置调用系统环境，让command执行过程中，可以获得applicationContext
				request.setServletContext(this.servletContext);
				// 执行command
				Result result = invoke(invoker, request, response);

				// 结果处理
				if (result != null) {
					result.render(request, response);
				}
			});
		} else {
			log.error("not found command, code:{} or command:{}", request.getCode(), request.getCommand());

			Result result = new NoActionResult(request.getCommand());
			result.render(request, response);
		}
	}

	public void dispatch(RpcRequest request, RpcResponse response) {
		// rpc异常时返回null给客户端
		if (shutdown) {
			response.setCause(new RuntimeException("remote server shutting down"));
			response.getCtx().writeAndFlush(response);
			return;
		}

		RpcInvoker invoker = rpcInvokerMap.get(request.getCommand());
		if (invoker == null) {
			return;
		}

		Object[] bindValues = invoker.getAdaptor().adaptBindSources(request.getParam().getArgs());
		int hash = Objects.hash(bindValues);
		coreThreadPools[hash & mark].execute(() -> {
			try {
				Object result = invoker.invoke(request);
				response.setResult(result);

				if (response.getResult() != null) {
					response.getCtx().writeAndFlush(response);
				}
			} catch (Throwable e) {
				response.setCause(e);
				response.getCtx().writeAndFlush(response);
				log.error("invoke rpc error", e);
			}
		});
	}

	public Result invoke(Invoker invoker, Request request, Response response) {
		Result result = null;
		if (interceptors.size() > 0) {
			Interceptor interceptor = interceptors.get(0);
			result = (Result) interceptor.invoke(invoker, request, response);
		}
		
		return result;
	}

	public void run(Runnable runnable, Object... bindValues) {
		int hash = Objects.hash(bindValues);
		coreThreadPools[hash & mark].execute(runnable);
	}

	public void runAsync(Runnable runnable) {
		asyncThreadPools.execute(runnable);
	}

	public void shutdown() {
		this.shutdown = true;
	}

}
