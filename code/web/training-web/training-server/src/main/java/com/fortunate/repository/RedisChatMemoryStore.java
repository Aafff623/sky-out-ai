package com.fortunate.repository;

import cn.hutool.core.collection.CollStreamUtil;
import com.alibaba.fastjson.JSON;
import com.mongodb.internal.connection.MessageSettings;
import dev.langchain4j.data.message.*;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import dev.langchain4j.store.memory.chat.ChatMemoryStore;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class RedisChatMemoryStore implements ChatMemoryStore {

    private final StringRedisTemplate redisTemplate;

    @Override
    public List<ChatMessage> getMessages(Object memoryId) {
        //获取会话消息
        String json = redisTemplate.opsForValue().get(memoryId.toString());
        List<RedisMessage> redisMessages = JSON.parseArray(json, RedisMessage.class);
        //将会话消息转换成List<ChatMessage>
        List<ChatMessage> list = CollStreamUtil.toList(redisMessages, MessageUtil::toMessage);
//        System.out.println(list);
        return list;
    }

    @Override
    public void updateMessages(Object memoryId, List<ChatMessage> messages) {
        //更新会话消息
        List<String> list = new ArrayList<>();
        messages.forEach(message -> {
            String json = MessageUtil.toJson(message);
            list.add(json);
        });
        redisTemplate.opsForValue().set(memoryId.toString(), list.toString(), Duration.ofHours(6));
    }

    @Override
    public void deleteMessages(Object memoryId) {
        redisTemplate.delete(memoryId.toString());
    }
}
