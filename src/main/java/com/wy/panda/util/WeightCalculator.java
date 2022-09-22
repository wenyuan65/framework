package com.wy.panda.util;

@FunctionalInterface
public interface WeightCalculator<T> {

    int getWeight(T t);

}
