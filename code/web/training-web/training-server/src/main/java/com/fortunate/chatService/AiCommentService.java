package com.fortunate.chatService;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;
import dev.langchain4j.service.spring.AiService;
import dev.langchain4j.service.spring.AiServiceWiringMode;

@AiService(
        wiringMode = AiServiceWiringMode.EXPLICIT,
        chatModel = "openAiChatModel"
)
public interface AiCommentService {
    @SystemMessage(fromResource = "comment-chat-message.txt")
     String chat(@UserMessage String message, @V("type") Integer type, @V("dishOrSetmeal") String dishOrSetmeal);
}
