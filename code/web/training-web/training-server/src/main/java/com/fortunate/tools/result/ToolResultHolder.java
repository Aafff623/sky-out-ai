package com.fortunate.tools.result;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 工具结果保持器，用来存储tools中得到的结果，请求id 作为key， value为键值对数据
 *
 *
 * @version 1.0
 */
public class ToolResultHolder {

    private static final Map<String, Map<String, Object>> HANDLER_MAP = new ConcurrentHashMap<>();


    private ToolResultHolder() {
    }

    public static void put(String key, String field, Object result) {
        if (null == key || null == field) {
            return;
        }
        //computeIfAbsent 如果key不存在则创建一个新的HshMap 然后再追加键值对
        HANDLER_MAP.computeIfAbsent(key, k -> new HashMap<>()).put(field, result);
    }

    public static Map<String, Object> get(String key) {
        //获取Key对应的value
        return key == null ? null : HANDLER_MAP.get(key);
    }

    public static Object get(String key, String field) {
        if (null == key || null == field) {
            return null;
        }
        //获取外层Key对应的value(也是一个map)，再获取里层key的value
        return Optional.ofNullable(HANDLER_MAP.get(key))
                .map(map -> map.get(field))
                .orElse(null);
    }

    public static void remove(String key) {
        if (null == key) {
            return;
        }
        HANDLER_MAP.remove(key);
    }

}