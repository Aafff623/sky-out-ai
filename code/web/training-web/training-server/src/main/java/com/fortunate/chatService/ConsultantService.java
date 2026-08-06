package com.fortunate.chatService;
import dev.langchain4j.service.*;
import dev.langchain4j.service.spring.AiService;
import dev.langchain4j.service.spring.AiServiceWiringMode;
import reactor.core.publisher.Flux;

import java.util.spi.ToolProvider;

//@AiService(
//        wiringMode = AiServiceWiringMode.EXPLICIT,
//        chatModel = "openAiChatModel",
//        streamingChatModel = "openAiStreamingChatModel",
//        chatMemoryProvider = "chatMemoryProvider",
//        tools = {"orderTool","dishRecommendTool"}
//)
public interface ConsultantService {

    //用于聊天的方法，message为用户输入的内容
    @SystemMessage(fromResource = "Consultant.txt")
    TokenStream chat(@MemoryId String memoryId, @UserMessage String message);
}
