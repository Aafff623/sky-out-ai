package com.fortunate.repository;

import dev.langchain4j.agent.tool.ToolExecutionRequest;
import dev.langchain4j.data.message.AiMessage;

import java.util.List;
import java.util.Map;

public class MyAiMessage extends AiMessage {
    private Map<String, Object> params = Map.of();


    /**
     * Create a new {@link AiMessage} with the given text.
     *
     * @param text the text of the message.
     */
    public MyAiMessage(String text, Map<String, Object> params) {
        super(text);
        this.params = params;
    }

    /**
     * Create a new {@link AiMessage} with the given tool execution requests.
     *
     * @param toolExecutionRequests the tool execution requests of the message.
     */
    public MyAiMessage(List<ToolExecutionRequest> toolExecutionRequests) {
        super(toolExecutionRequests);
    }

    /**
     * Create a new {@link AiMessage} with the given text and tool execution requests.
     *
     * @param text                  the text of the message.
     * @param toolExecutionRequests the tool execution requests of the message.
     */
    public MyAiMessage(String text, List<ToolExecutionRequest> toolExecutionRequests, Map<String, Object> params) {
        super(text, toolExecutionRequests);
        this.params = params;
    }

    public Map<String, Object> getParams() {
        return params;
    }

    public void setParams(Map<String, Object> params) {
        this.params = params;
    }
}
