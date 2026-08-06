package com.fortunate.context;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ToolContext {
    private static final Map<String, Object> CONTEXT = new ConcurrentHashMap<>();

    public static final String USER_ID = "userId";
    public static final String REQUEST_ID = "requestId";


    public static void put(String key, Object value) {
        // 在CONTEXT.get()返回的Map上执行put操作
        CONTEXT.put(key, value);
    }

    public static Object get(String key) {
        // 在CONTEXT.get()返回的Map上执行get操作
        return CONTEXT.get(key);
    }

    // 添加清理方法避免内存泄漏
    public static void clear() {
        CONTEXT.clear();
    }
}