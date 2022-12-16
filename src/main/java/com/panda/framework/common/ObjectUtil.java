package com.panda.framework.common;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class ObjectUtil {
    private static final Map<String, Long> clazzSizeMap = new HashMap<>();
    private static final Set<Object> history = new HashSet<>(256);
    static {
        clazzSizeMap.put(byte.class.getName(), 1L);
        clazzSizeMap.put(short.class.getName(), 2L);
        clazzSizeMap.put(char.class.getName(), 2L);
        clazzSizeMap.put(int.class.getName(), 4L);
        clazzSizeMap.put(float.class.getName(), 4L);
        clazzSizeMap.put(long.class.getName(), 8L);
        clazzSizeMap.put(double.class.getName(), 8L);
        clazzSizeMap.put(boolean.class.getName(), 4L);

        clazzSizeMap.put(Byte.class.getName(), 1L);
        clazzSizeMap.put(Short.class.getName(), 2L);
        clazzSizeMap.put(Character.class.getName(), 2L);
        clazzSizeMap.put(Integer.class.getName(), 4L);
        clazzSizeMap.put(Float.class.getName(), 4L);
        clazzSizeMap.put(Long.class.getName(), 8L);
        clazzSizeMap.put(Double.class.getName(), 8L);
        clazzSizeMap.put(Boolean.class.getName(), 4L);

        clazzSizeMap.put(Object.class.getName(), 4L);
        clazzSizeMap.put(String.class.getName(), 4L);
        clazzSizeMap.put(Date.class.getName(), 32L);
        clazzSizeMap.put(AtomicInteger.class.getName(), 8L);
        clazzSizeMap.put(AtomicLong.class.getName(), 12L);
        clazzSizeMap.put("io.netty.channel.Channel", 4L);
        clazzSizeMap.put("sun.misc.Unsafe", 4L);
    }

    public static long calcInstanceSize(Object obj) {
        // System.out.println("begin================================================" + obj.getClass());
        long size = doCalcInstanceSize(obj, obj.getClass().getSimpleName());
        // System.out.println("end==================================================" + obj.getClass());
        history.clear();

        return size;
    }

    private static long doCalcInstanceSize(Object obj, String name) {
        if (obj == null) {
            return 0L;
        }
        if (history.contains(obj)) {
            return 4L;
        }
        history.add(obj);

        long size = 0L;
        Class<?> objClass = obj.getClass();
        List<Field> fields = ReflactUtil.getAllFields(objClass);
        for (Field field : fields) {
            field.setAccessible(true);
            Class<?> fieldType = field.getType();
            long fieldSize = calcFieldSizeFast(fieldType);
            size += fieldSize;

            if (fieldSize > 0 && fieldType != String.class) {
                // System.out.println(name + "." + field.getName() + ":" + fieldSize);
                continue;
            }

            Object value = null;
            try {
                value = field.get(obj);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
            size += calcFieldSize(value, name + "." + field.getName());
        }

        return size;
    }

    private static long calcFieldSize(Object obj, String name) {
        if (obj == null) {
            return 0L;
        }

        long size = 4L;
        Class<?> clazz = obj.getClass();
        if (clazz == String.class) {
            String s = (String) obj;
            size += s.length() * 2L;

            // System.out.println(name + ":" + s.length() * 2L);
        } else if (List.class.isAssignableFrom(clazz)) {
            List list = (List) obj;
            for (Object e : list) {
                size += calcFieldSize(e, name + ".list");
            }
        } else if (Map.class.isAssignableFrom(clazz)) {
            Map map = (Map) obj;
            for (Object e : map.keySet()) {
                size += calcFieldSize(e, name + ".key");
            }
            for (Object e : map.values()) {
                size += calcFieldSize(e, name + ".value");
            }
        } else if (Set.class.isAssignableFrom(clazz)) {
            Set set = (Set) obj;
            for (Object e : set) {
                size += calcFieldSize(e, name + ".set");
            }
        } else if (Queue.class.isAssignableFrom(clazz)) {
            Queue queue = (Queue) obj;
            for (Object e : queue) {
                size += calcFieldSize(e, name + ".queue");
            }
        } else if (clazz.isArray()) {
            int length = Array.getLength(obj);
            for (int i = 0; i < length; i++) {
                Object e = Array.get(obj, i);
                if (e != null) {
                    size += calcFieldSize(e, name + ".array");
                }
            }
        } else {
            long fieldSize = calcFieldSizeFast(clazz);
            if (fieldSize > 0) {
                size += fieldSize;
                // System.out.println(name + ":" + fieldSize);
            } else {
                long s = doCalcInstanceSize(obj, name);
                size += s;

                // System.out.println(name + ":" + s);
            }
        }

        return size;
    }

    private static long calcFieldSizeFast(Class<?> fieldType) {
        return clazzSizeMap.getOrDefault(fieldType.getName(), 0L);
    }

}
