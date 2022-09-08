package com.wy.panda.reflect;

import java.lang.reflect.Field;

public class ReflectTest {

    public static void main(String[] args) throws Exception {
        Field[] fields1 = CatData.class.getDeclaredFields();
        Field[] fields2 = CatData.class.getDeclaredFields();
        fields1[0].setAccessible(true);

        System.out.println(fields1[0]);
        System.out.println(fields2[0]);

    }
}
