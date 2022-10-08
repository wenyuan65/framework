package com.wy.panda.util;

import java.util.concurrent.ConcurrentHashMap;

public class ConcurrentHashMapTest {

    public static void main(String[] args) {
        ConcurrentHashMap<Integer, String> map = new ConcurrentHashMap<>();
        System.out.println(map.put(1, "a"));
        System.out.println(map.put(1, "a"));
        System.out.println(map.putIfAbsent(2, "a"));
        System.out.println(map.putIfAbsent(2, "a"));
    }

}
