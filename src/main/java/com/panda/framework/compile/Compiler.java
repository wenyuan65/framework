package com.panda.framework.compile;

public interface Compiler {

    Class<?> compile(String name, String sourceCode) throws Throwable;

}
