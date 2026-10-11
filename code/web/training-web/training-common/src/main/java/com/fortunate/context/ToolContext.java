package com.fortunate.context;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * AI 工具上下文：以会话 memoryId（sessionId）为作用域隔离，
 * 避免并发 AI 会话互相覆盖 USER_ID / REQUEST_ID 造成跨用户串扰。
 */
public class ToolContext {

    private static final Map<String, Map<String, Object>> CONTEXT = new ConcurrentHashMap<>();

    public static final String USER_ID = "userId";
    public static final String REQUEST_ID = "requestId";

    private ToolContext() {
    }

    /**
     * 在指定会话作用域内写入上下文
     * @param memoryId 会话标识（LangChain4j memoryId，即客服 sessionId）
     * @param key      上下文键
     * @param value    上下文值
     */
    public static void put(String memoryId, String key, Object value) {
        CONTEXT.computeIfAbsent(memoryId, k -> new ConcurrentHashMap<>()).put(key, value);
    }

    /**
     * 在指定会话作用域内读取上下文
     */
    public static Object get(String memoryId, String key) {
        Map<String, Object> scope = CONTEXT.get(memoryId);
        return scope == null ? null : scope.get(key);
    }

    /**
     * 清理指定会话作用域，避免静态 Map 条目泄漏
     */
    public static void clear(String memoryId) {
        if (memoryId != null) {
            CONTEXT.remove(memoryId);
        }
    }
}
