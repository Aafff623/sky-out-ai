package com.fortunate.config;

import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * local 环境：本机 Scoop Redis 无 RediSearch（FT.*），改用内存向量库。
 * 进程重启后知识库向量需重新 embed；缓存/会话记忆仍走 Redis。
 */
@Configuration
@Profile("local")
public class LocalEmbeddingStoreConfig {

    @Bean
    public EmbeddingStore<TextSegment> embeddingStore() {
        return new InMemoryEmbeddingStore<>();
    }
}
