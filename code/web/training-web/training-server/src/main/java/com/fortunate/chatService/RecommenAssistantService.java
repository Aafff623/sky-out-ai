package com.fortunate.chatService;

import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.spring.AiService;
import dev.langchain4j.service.spring.AiServiceWiringMode;

//推荐ai
@AiService(
        wiringMode = AiServiceWiringMode.EXPLICIT,
        chatModel = "openAiChatModel"
)
public interface RecommenAssistantService {

    @SystemMessage(fromResource = "RecommentAssistant.txt")
     String chat(@UserMessage String message);
}
