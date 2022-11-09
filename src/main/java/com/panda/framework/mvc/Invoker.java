package com.panda.framework.mvc;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import com.panda.framework.exception.IllegalParametersException;
import com.panda.framework.mvc.domain.Request;
import com.panda.framework.mvc.domain.Response;
import com.panda.framework.mvc.result.Result;
import com.panda.framework.mvc.validate.ClearValidated;
import com.panda.framework.mvc.validate.Rule;
import com.panda.framework.mvc.validate.Validated;
import com.panda.framework.mvc.validate.Validation;

public class Invoker {

	/** 实例 */
	private Object invokeInstance;
	/** 执行器的类 */
	private Class<?> clazz;
	/** 内部方法 */
	private Method method;
	/** 类名 */
	private String clazzName;
	/** 方法名 */
	private String methodName;
	/** 参数格式化 */
	private Adaptor adaptor;
	/** 是否检查方法的调用条件 */
	private boolean validate;
	/** 条件检查 */
	private List<Validation> validations;

	public Invoker(Object instance, Method method) {
		this.invokeInstance = instance;
		this.method = method;
		this.clazz = this.method.getDeclaringClass();
		this.clazzName = this.clazz.getSimpleName();
		this.methodName = this.method.getName();
	}
	
	public void init() throws IllegalParametersException, InstantiationException, IllegalAccessException {
		// 参数适配器
		initAdaptor();
		// 返回结果
		initResultType();
		// 验证功能
		initValidations();
	}

	public void initBindSource(String[] bindSources) {
		adaptor.initBindSourceInjectors(bindSources);
	}
	
	/**
	 * 执行
	 * @param request
	 * @param response
	 * @return
	 */
	public Object invoke(Request request, Response response) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		if (validate) {
			for (Validation validation : validations) {
				byte[] result = validation.check();
				if (result != null) {
					return result;
				}
			}
		}

		Object[] param = request.getParam();
		if (request.getParam() == null) {
			param = adaptor.adapt(request, response);
		}
		
		return method.invoke(invokeInstance, param);
	}

	/**
	 * 参数适配器
	 * @throws IllegalParametersException
	 */
	private void initAdaptor() throws IllegalParametersException {
		adaptor = new Adaptor(this.method);
		adaptor.init();
	}

	/**
	 * 返回结果检查
	 * @throws IllegalParametersException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	private void initResultType() throws IllegalParametersException, InstantiationException, IllegalAccessException {
		Class<?> returnType = method.getReturnType();
		if (!Result.class.isAssignableFrom(returnType)) {
			String msg = String.format("return type of %s.%s() is not assignable from %s ", clazzName, methodName, Result.class.getName());
			throw new IllegalParametersException(msg);
		}
	}
	
	private void initValidations() throws InstantiationException, IllegalAccessException {
		validations = new ArrayList<>();
		Validated validated = this.clazz.getDeclaredAnnotation(Validated.class);
		if (validated != null) {
			parseValidation(validated);
		}
		
		ClearValidated clearValidated = this.method.getDeclaredAnnotation(ClearValidated.class);
		if (clearValidated != null) {
			validations.clear();
			// 支持多个Validation时，方法ClearValidated注解支持移除特定几个顺序的Validation
//			if (clearValidated.clearAll() || StringUtils.isBlank(clearValidated.clearOrder())) {
//				validations.clear();
//			} else {
//				String clearOrders = clearValidated.clearOrder();
//				int[] clearOrdersArray = TextUtil.getArray(clearOrders);
//				Arrays.sort(clearOrdersArray);
//				
//				int order = 0;
//				int clearOrdersIndex = 0;
//				Iterator<Validation> iterator = validations.iterator();
//				while (iterator.hasNext()) {
//					while (clearOrdersIndex < clearOrdersArray.length 
//							&& clearOrdersArray[clearOrdersIndex] < order) {
//						clearOrdersIndex ++;
//					}
//					
//					if (clearOrdersArray[clearOrdersIndex] == order) {
//						iterator.remove();
//						clearOrdersIndex ++;
//					}
//					order ++;
//				}
//			}
		}
		
		validated = this.method.getDeclaredAnnotation(Validated.class);
		if (validated != null) {
			parseValidation(validated);
		}
		
		this.validate = validations.size() > 0;
	}

	private void parseValidation(Validated validated) throws InstantiationException, IllegalAccessException {
		Class<? extends Rule> ruleClazz = validated.rule();
		String condition = validated.value();
		
		Rule rule = ruleClazz.newInstance();
		validations.add(new Validation(rule, condition));
	}

	public Class<?> getClazz() {
		return clazz;
	}

	public void setClazz(Class<?> clazz) {
		this.clazz = clazz;
	}

	public Method getMethod() {
		return method;
	}

	public void setMethod(Method method) {
		this.method = method;
	}

	public String getClazzName() {
		return clazzName;
	}

	public void setClazzName(String clazzName) {
		this.clazzName = clazzName;
	}

	public String getMethodName() {
		return methodName;
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	public Adaptor getAdaptor() {
		return adaptor;
	}

}
