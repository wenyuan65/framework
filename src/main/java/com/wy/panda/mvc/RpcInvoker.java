package com.wy.panda.mvc;

import com.wy.panda.rpc.RpcRequest;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class RpcInvoker {

    /** 实例 */
    private Object invokeInstance;
    /** 执行器的类 */
    private Class<?> clazz;
    /** 内部方法 */
    private Method method;
    /** 内部方法 */
    private Class<?> returnType;

    public RpcInvoker(Object instance, Method method) {
        this.invokeInstance = instance;
        this.method = method;
        this.clazz = this.method.getDeclaringClass();
        this.returnType = method.getReturnType();
    }

    public void init() {

    }

    public Object invoke(RpcRequest request) throws InvocationTargetException, IllegalAccessException {
        return method.invoke(invokeInstance, request.getParam().getArgs());
    }

}
