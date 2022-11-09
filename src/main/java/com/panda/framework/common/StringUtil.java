package com.panda.framework.common;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

public class StringUtil {

    private static final String default_key_value_spliterator = ":";
    private static final String default_entry_spliterator = ",";

    /**
     *
     * @param str
     * @return
     */
    public static boolean isNumber(String str) {
        if (str == null || str.trim().equals("")) {
            return false;
        }

        int length = str.length();
        for (int i = 0; i < length; i++) {
            char c = str.charAt(i);
            if (!('0' <= c && c <= '9')) {
                return false;
            }
        }

        return true;
    }

    public static String map2Str(Map<Integer, Integer> map) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<Integer, Integer> entry : map.entrySet()) {
            if (sb.length() > 0) {
                sb.append(default_entry_spliterator);
            }
            sb.append(entry.getKey()).append(default_key_value_spliterator).append(entry.getValue());
        }

        return sb.toString();
    }

    public static Map<Integer, Integer> string2Map(String content) {
        return string2Map(content, HashMap::new, Integer::parseInt, Integer::parseInt, default_entry_spliterator, default_key_value_spliterator);
    }

    public static Map<Integer, Integer> string2Map(String content, String entrySpliterator, String keyValueSpliterator) {
        return string2Map(content, HashMap::new, Integer::parseInt, Integer::parseInt, entrySpliterator, keyValueSpliterator);
    }

    public static <K, V> Map<K, V> string2Map(String content, Supplier<Map<K, V>> supplier, Function<String, K> keyExtractor, Function<String, V> valueExtractor) {
        return string2Map(content, supplier, keyExtractor, valueExtractor, default_entry_spliterator, default_key_value_spliterator);
    }

    public static <K, V> Map<K, V> string2Map(String content, Supplier<Map<K, V>> supplier,
                                              Function<String, K> keyExtractor, Function<String, V> valueExtractor,
                                              String entrySpliterator, String keyValueSpliterator) {
        Map<K, V> map = supplier.get();
        String[] array = content.split(entrySpliterator);
        for (int i = 0; i < array.length; i++) {
            String keyValue = array[i];
            String[] args = keyValue.split(keyValueSpliterator);
            if (args.length != 2) {
                throw new RuntimeException("key/value解析异常:" + keyValue);
            }

            K key = keyExtractor.apply(args[0]);
            V value = valueExtractor.apply(args[1]);

            map.put(key, value);
        }

        return map;
    }

}
