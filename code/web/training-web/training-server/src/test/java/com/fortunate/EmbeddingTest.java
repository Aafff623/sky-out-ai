package com.fortunate;

import dev.langchain4j.community.store.embedding.redis.RedisEmbeddingStore;
import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.document.Metadata;
import dev.langchain4j.data.document.loader.ClassPathDocumentLoader;
import dev.langchain4j.data.document.parser.apache.tika.ApacheTikaDocumentParser;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.embedding.MyEmbeddingStoreIngestor;
import dev.langchain4j.store.embedding.MyIngestionResult;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

@SpringBootTest
public class EmbeddingTest {
    @Autowired
    private EmbeddingModel embeddingModel;
    @Autowired
    private RedisEmbeddingStore redisEmbeddingStore;

//    @Test
    void testEmbedding() {
//        Document document = ClassPathDocumentLoader.loadDocument("content/天坑专业top10.pdf", new ApachePdfBoxDocumentParser());
        Document document = ClassPathDocumentLoader.loadDocument("content/1.md", new ApacheTikaDocumentParser());
//        Document document = ClassPathDocumentLoader.loadDocument("content/测试.txt", new TextDocumentParser());
        String text = document.text();
        System.out.println(text);
        Metadata metadata = document.metadata();
        System.out.println(metadata);
        String encodedFileName = document.metadata().getString(Document.FILE_NAME);
        String decodedFileName = URLDecoder.decode(encodedFileName, StandardCharsets.UTF_8);
        document.metadata().put("file_name", decodedFileName);
        //构建通用文本分割器
        DocumentSplitter recursive = DocumentSplitters.recursive(300, 100);

        //构建文本向量存储器
        MyEmbeddingStoreIngestor ingestor = MyEmbeddingStoreIngestor.builder()
                .embeddingModel(embeddingModel)
                .embeddingStore(redisEmbeddingStore)
                .documentSplitter(recursive)
                .build();

        MyIngestionResult ingest = ingestor.ingest(document);
        System.out.println(ingest);
    }
}
