package com.panda.framework.util;

@FunctionalInterface
public interface WeightCalculator<T> {

    int getWeight(T t);

}
