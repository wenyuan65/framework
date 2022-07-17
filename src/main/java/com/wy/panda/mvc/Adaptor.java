package com.wy.panda.mvc;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.wy.panda.common.ReflactUtil;
import com.wy.panda.exception.IllegalParametersException;
import com.wy.panda.mvc.annotation.RequestParam;
import com.wy.panda.mvc.domain.Request;
import com.wy.panda.mvc.domain.Response;
import com.wy.panda.mvc.inject.BooleanInjector;
import com.wy.panda.mvc.inject.ByteInjector;
import com.wy.panda.mvc.inject.CharInjector;
import com.wy.panda.mvc.inject.DoubleInjector;
import com.wy.panda.mvc.inject.FloatInjector;
import com.wy.panda.mvc.inject.Injector;
import com.wy.panda.mvc.inject.IntInjector;
import com.wy.panda.mvc.inject.LongInjector;
import com.wy.panda.mvc.inject.ObjectInjector;
import com.wy.panda.mvc.inject.PlayerIdInjector;
import com.wy.panda.mvc.inject.RequestInjector;
import com.wy.panda.mvc.inject.ResponseInjector;
import com.wy.panda.mvc.inject.ShortInjector;
import com.wy.panda.mvc.inject.StringInjector;

public class Adaptor {

	/** 内部方法 */
	private Method method;
	/** 参数名 */
	private String[] paramNameList;
	/** 参数注入方法 */
	private Injector[] injectors;
	
	public Adaptor(Method method) {
		this.method = method;
	}
	
	public void init() throws IllegalParametersException {
		// 获取参数名
		Set<String> paramNameFilter = new HashSet<>();
		Parameter[] parameters = method.getParameters();
		paramNameList = new String[parameters.length];
		injectors = new Injector[parameters.length];
		for (int i = 0; i < parameters.length; i++) {
			Parameter p = parameters[i];
			RequestParam requestParam = p.getDeclaredAnnotation(RequestParam.class);
			String paramName = requestParam != null ? requestParam.value() : p.getName();
			paramNameList[i] = paramName;
			// 具有相同的参数名出现
			if (paramNameFilter.contains(paramName)) {
				throw new IllegalParametersException("Parameter name dumplicate");
			}
			paramNameFilter.add(paramName);
			
			injectors[i] = parseInjector(p.getType(), paramName);
		}
	}

	public Object[] adapt(Request request, Response response) {
		// 填充参数
		Map<String, String> paramMap = request.getParamMap();
		Object[] param = new Object[paramNameList.length];
		for (int i = 0; i < param.length; i++) {
			String paramName = paramNameList[i];
			String paramValue = paramMap.get(paramName);
			param[i] = injectors[i].inject(request, response, paramValue);
		}
		return param;
	}
	
	private Injector parseInjector(Class<?> paramType, String paramName) {
		if (paramType == Request.class) {
			return new RequestInjector();
		} else if (paramType == Response.class) {
			return new ResponseInjector();
		} else if ("playerId".equals(paramName)) {
			return new PlayerIdInjector();
		} else if (paramType == String.class) {
			return new StringInjector();
		} else if (ReflactUtil.isInt(paramType)) {
			return new IntInjector();
		} else if (ReflactUtil.isBoolean(paramType)) {
			return new BooleanInjector();
		} else if (ReflactUtil.isLong(paramType)) {
			return new LongInjector();
		} else if (ReflactUtil.isFloat(paramType)) {
			return new FloatInjector();
		} else if (ReflactUtil.isDouble(paramType)) {
			return new DoubleInjector();
		} else if (ReflactUtil.isShort(paramType)) {
			return new ShortInjector();
		} else if (ReflactUtil.isByte(paramType)) {
			return new ByteInjector();
		} else if (ReflactUtil.isChar(paramType)) {
			return new CharInjector();
		} else {
			// TODO:
			return new ObjectInjector();
		}
	}
	
}
