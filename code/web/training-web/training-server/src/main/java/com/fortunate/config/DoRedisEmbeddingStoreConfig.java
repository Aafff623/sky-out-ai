package com.fortunate.config;

import dev.langchain4j.community.store.embedding.redis.RedisEmbeddingStore;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.store.embedding.EmbeddingStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * do 环境：跳过 RedisEmbeddingStore 启动时对 embedding 模型的维度探测
 *（业务空间未开通 text-embedding-v4 时会 403 导致进程起不来）。
 * text-embedding-v4 默认维度 1024；开通模型后知识库入库/RAG 才真正可用。
 */
@Configuration
@Profile("do")
public class DoRedisEmbeddingStoreConfig {

    @Bean
    public EmbeddingStore<TextSegment> embeddingStore(
            @Value("${training.redis.host}") String host,
            @Value("${training.redis.port}") int port,
            @Value("${training.redis.embedding-prefix}") String prefix,
            @Value("${langchain4j.community.redis.dimension:1024}") int dimension) {
        return RedisEmbeddingStore.builder()
                .host(host)
                .port(port)
                .prefix(prefix)
                .dimension(dimension)
                .build();
    }
}
