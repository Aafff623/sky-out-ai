package com.fortunate.config;

import com.fortunate.chatService.ConsultantService;
import com.fortunate.repository.RedisChatMemoryStore;
import com.fortunate.tools.DishRecommendTool;
import com.fortunate.tools.OrderTool;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.memory.chat.ChatMemoryProvider;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiStreamingChatModel;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.store.embedding.EmbeddingStore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.fortunate.constant.CustomerServiceConstant.REDIS_KEY_SESSION_MESSAGES;


@Configuration
@RequiredArgsConstructor
@Slf4j
public class LangChain4jConfiguration {

    /** do=RedisEmbeddingStore；local=InMemoryEmbeddingStore */
    private final EmbeddingStore<TextSegment> embeddingStore;

    private final EmbeddingModel embeddingModel;

    //创建会话提供者
    @Bean
    public ChatMemoryProvider chatMemoryProvider(RedisChatMemoryStore store) {
        return memoryId -> MessageWindowChatMemory.builder()
                .id(REDIS_KEY_SESSION_MESSAGES + memoryId)
                .maxMessages(20)
                .chatMemoryStore(store)
                .build();
    }

    @Bean
    public ConsultantService consultantService(OpenAiChatModel openAiChatModel,
                                               OpenAiStreamingChatModel openAiStreamingChatModel,
                                               ChatMemoryProvider chatMemoryProvider,
                                               DishRecommendTool dishRecommendTool,
                                               OrderTool orderTool,
                                               ContentRetriever contentRetriever) {
        return AiServices.builder(ConsultantService.class)
                //聊天模型
                .chatModel(openAiChatModel)
                //流式聊天模型
                .streamingChatModel(openAiStreamingChatModel)
                //会话提供者
                .chatMemoryProvider(chatMemoryProvider)
                //检索增强
                .contentRetriever(contentRetriever)
                //工具
                .tools(dishRecommendTool, orderTool)
                .build();
    }


    //构建向量数据库检索对象
    @Bean
    public ContentRetriever contentRetriever() {
        log.info("构建向量数据库检索对象");
        return EmbeddingStoreContentRetriever.builder()
                .embeddingStore(embeddingStore)
                .embeddingModel(embeddingModel)
                .maxResults(2)
                .minScore(0.6)
                .build();
    }
}
