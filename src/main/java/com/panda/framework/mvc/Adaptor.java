package com.panda.framework.mvc;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;

import com.panda.framework.common.ReflactUtil;
import com.panda.framework.exception.IllegalParametersException;
import com.panda.framework.mvc.annotation.RequestParam;
import com.panda.framework.mvc.domain.Request;
import com.panda.framework.mvc.domain.Response;
import com.panda.framework.log.Logger;
import com.panda.framework.log.LoggerFactory;
import com.panda.framework.mvc.inject.BooleanInjector;
import com.panda.framework.mvc.inject.ByteInjector;
import com.panda.framework.mvc.inject.CharInjector;
import com.panda.framework.mvc.inject.DoubleInjector;
import com.panda.framework.mvc.inject.FloatInjector;
import com.panda.framework.mvc.inject.Injector;
import com.panda.framework.mvc.inject.IntInjector;
import com.panda.framework.mvc.inject.LongInjector;
import com.panda.framework.mvc.inject.ObjectInjector;
import com.panda.framework.mvc.inject.PlayerIdInjector;
import com.panda.framework.mvc.inject.RequestInjector;
import com.panda.framework.mvc.inject.ResponseInjector;
import com.panda.framework.mvc.inject.ShortInjector;
import com.panda.framework.mvc.inject.StringInjector;

public class Adaptor {

	private static final Logger log = LoggerFactory.getLogger(Adaptor.class);

	/** 内部方法 */
	private Method method;
	/** 参数名 */
	private String[] paramNameList;
	/** 参数注入方法 */
	private Injector[] injectors;
	/** 绑定线程的参数，Injector引用列表 */
	private Integer[] bindSourceInjectorRefs;

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
				throw new IllegalParametersException("Parameter name duplicated");
			}
			paramNameFilter.add(paramName);
			
			injectors[i] = parseInjector(p.getType(), paramName);
		}
	}

	public void initBindSourceInjectors(String[] bindSource) {
		if (bindSource == null || bindSource.length == 0) {
			return;
		}

		bindSourceInjectorRefs = new Integer[bindSource.length];
		for (int k = 0; k < bindSource.length; k++) {
			boolean found = false;
			for (int i = 0; i < paramNameList.length; i++) {
				if (!paramNameList[i].equals(bindSource[k])) {
					continue;
				}
				found = true;
				bindSourceInjectorRefs[k] = i;
			}
			if (!found) {
				// 默认的playerId找不到，或者指定的参数不存在
				bindSourceInjectorRefs[k] = -1;
				log.warn("cannot found bind source:{} in method {}", bindSource[k], method);
			}
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

	public Object[] adaptBindSources(Object[] params) {
		if (bindSourceInjectorRefs == null || bindSourceInjectorRefs.length == 0) {
			return null;
		}

		Object[] bindValues = new Object[bindSourceInjectorRefs.length];
		for (int i = 0; i < bindSourceInjectorRefs.length; i++) {
			Integer ref = bindSourceInjectorRefs[i];
			if (ref.intValue() >= 0) {
				bindValues[i] = params[ref];
			}
		}
		return bindValues;
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
