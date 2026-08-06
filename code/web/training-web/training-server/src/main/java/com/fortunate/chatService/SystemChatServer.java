package com.fortunate.chatService;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.spring.AiService;
import dev.langchain4j.service.spring.AiServiceWiringMode;

@AiService(
        wiringMode = AiServiceWiringMode.EXPLICIT,
        chatModel = "openAiChatModel"
)
public interface SystemChatServer {
    @SystemMessage(fromResource = "system-chat-message.txt")
     String chat(@UserMessage String message);
}
