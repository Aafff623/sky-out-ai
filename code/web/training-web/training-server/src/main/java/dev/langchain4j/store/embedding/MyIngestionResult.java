package dev.langchain4j.store.embedding;

import dev.langchain4j.model.output.TokenUsage;
import lombok.Data;
import lombok.Getter;

import java.util.List;

/**
 * Represents the result of a {@link EmbeddingStoreIngestor} ingestion process.
 */
public class MyIngestionResult {

    /**
     * The token usage information.
     */
    private final TokenUsage tokenUsage;
    @Getter
    private List<String> ids;

    public MyIngestionResult(TokenUsage tokenUsage, List<String> ids) {
        this.tokenUsage = tokenUsage;
        this.ids = ids;
    }

    public TokenUsage tokenUsage() {
        return tokenUsage;
    }
}