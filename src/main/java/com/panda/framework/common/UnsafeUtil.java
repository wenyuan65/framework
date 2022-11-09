package com.panda.framework.common;

import sun.misc.Unsafe;

import java.lang.reflect.Array;
import java.lang.reflect.Field;

public class UnsafeUtil {

    private static final Unsafe unsafe;

    static {
        Unsafe tempUnsafe = null;
        try {
            Field field = Unsafe.class.getDeclaredField("theUnsafe");
            field.setAccessible(true);
            tempUnsafe = (Unsafe)field.get((Object)null);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }

        unsafe = tempUnsafe;
    }

    public static Unsafe getUnsafe() {
        return unsafe;
    }

    public static Object[] convert(Object arrayObj) {
        if (!arrayObj.getClass().isArray()) {
            throw new RuntimeException("illegal parameter type");
        }

        Class<?> componentType = arrayObj.getClass().getComponentType();
        long baseOffset = unsafe.arrayBaseOffset(arrayObj.getClass());
        long indexScale = unsafe.arrayIndexScale(arrayObj.getClass());


        int length = Array.getLength(arrayObj);
        Object[] array = new Object[length];
        for (int i = 0; i < length; i++) {
            long address = baseOffset + indexScale * i;
            Object obj = unsafe.getObject(arrayObj, address);

            array[i] = componentType.cast(obj);
        }

        return array;
    }

}
