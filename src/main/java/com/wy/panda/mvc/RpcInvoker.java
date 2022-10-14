package com.wy.panda.mvc;

import com.wy.panda.exception.IllegalParametersException;
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
    /** 参数格式化 */
    private Adaptor adaptor;

    public RpcInvoker(Object instance, Method method) {
        this.invokeInstance = instance;
        this.method = method;
        this.clazz = this.method.getDeclaringClass();
        this.returnType = method.getReturnType();
    }

    public void init() throws IllegalParametersException {
        // 检查rpc方法是否合法
        checkRpcMethodDefine();

        adaptor = new Adaptor(method);
        adaptor.init();
    }

    public void initBindSource(String[] bindSources) {
        adaptor.initBindSourceInjectors(bindSources);
    }

    private void checkRpcMethodDefine() {

    }

    public Object invoke(RpcRequest request) throws InvocationTargetException, IllegalAccessException {
        return method.invoke(invokeInstance, request.getParam().getArgs());
    }

    public Adaptor getAdaptor() {
        return adaptor;
    }
}
