package com.wy.panda.compile;

public interface Compiler {

    Class<?> compile(String name, String sourceCode) throws Throwable;

}
