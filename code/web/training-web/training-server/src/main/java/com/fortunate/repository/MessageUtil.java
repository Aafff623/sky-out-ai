package com.fortunate.repository;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import com.fortunate.context.ToolContext;
import com.fortunate.tools.result.ToolResultHolder;
import dev.langchain4j.agent.tool.ToolExecutionRequest;
import dev.langchain4j.data.message.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;


/**
 * 消息转换工具类，提供消息对象与JSON字符串之间的转换功能，主要用于Redis存储格式转换
 */
public class MessageUtil {
    private static final String ID = "id";
    private static final String NAME = "name";
    private static final String TOOL_NAME = "tool_name";
    private static final String ARGUMENTS = "arguments";


    /**
     * 将Message对象转换为Redis存储格式的JSON字符串
     *
     * @param message 需要转换的原始消息对象
     * @return 符合Redis存储规范的JSON字符串
     */
    public static String toJson(ChatMessage message) {
        ChatMessageType type = message.type();
        RedisMessage redisMessage = new RedisMessage();
        //设置用户消息
        if (type == ChatMessageType.USER) {
            UserMessage userMessage = (UserMessage) message;
            //设置消息内容
            String s = userMessage.singleText();
            redisMessage.setTextContent(s);
            redisMessage.setType(String.valueOf(ChatMessageType.USER));
        }
        //设置AI消息
        else if (type == ChatMessageType.AI) {
            AiMessage aiMessage = (AiMessage) message;
            String text = aiMessage.text();
            redisMessage.setType(String.valueOf(ChatMessageType.AI));
            redisMessage.setTextContent(text);
            //修复数据更新bug 判断文本是否为空串
            if ("".equals(text)) {
                //为空串说明是AI调用工具
                // 强转为自己的ai消息
                MyAiMessage myAiMessage = (MyAiMessage) aiMessage;
                Map<String, Object> params = myAiMessage.getParams();
                //在判断参数是否不为空
                if (ObjectUtil.isNotEmpty(params)) {
                    //把原来的参数设置到redisMessage中
                    redisMessage.setParams(params);
                }
            }
            List<ToolExecutionRequest> toolExecutionRequests = aiMessage.toolExecutionRequests();
            if (toolExecutionRequests != null && !toolExecutionRequests.isEmpty()) {
                //在onToolExecuted时k设置了本次的请求id key：messageId field："requestId" value：requestId
                String messageId = toolExecutionRequests.get(0).id();
                //在调用推荐菜品工具时根据请求id设置了数据
                var requestId = Convert.toStr(ToolResultHolder.get(messageId, ToolContext.REQUEST_ID));
                //根据请求id获取参数数据
                Map<String, Object> params = ToolResultHolder.get(requestId);
                String name = toolExecutionRequests.get(0).name();
                String arguments = toolExecutionRequests.get(0).arguments();
                // 确保这里使用的键与TOOL_EXECUTION_RESULT中一致
                redisMessage.setMetadata(Map.of(ID, messageId, NAME, name, ARGUMENTS, arguments));
                //设置了但是在封装MyAiMessage获取不到只能通过metadata获取
              /*  MyToolExecutionRequest myToolExecutionRequest = MyToolExecutionRequest.builder()
                        .id(messageId)
                        .name(name)
                        .arguments(arguments).
                        build();
                List<MyToolExecutionRequest> myToolExecutionRequests = convertToMyToolExecutionRequests(toolExecutionRequests);
                List<MyToolExecutionRequest> myToolExecutionRequests2 = List.of(myToolExecutionRequest);
                System.out.println(myToolExecutionRequests2);
                redisMessage.setMyToolExecutionRequests(myToolExecutionRequests);*/
                //判断参数是否为空
                if (ObjectUtil.isNotEmpty(params)) {
                    redisMessage.setParams(params);
                }
                // 最后，删除 messageId 对应的数据
                ToolResultHolder.remove(messageId);
            }
        }
        //设置工具执行结果消息
        else if (type == ChatMessageType.TOOL_EXECUTION_RESULT) {
            ToolExecutionResultMessage toolExecutionResultMessage = (ToolExecutionResultMessage) message;
            String toolCallId = toolExecutionResultMessage.id();
            String toolName = toolExecutionResultMessage.toolName();
            String text = toolExecutionResultMessage.text();
            redisMessage.setTextContent(text);
            redisMessage.setMetadata(Map.of(ID, toolCallId, TOOL_NAME, toolName));
            redisMessage.setType(String.valueOf(ChatMessageType.TOOL_EXECUTION_RESULT));
        }
        //设置系统消息
        else if (type == ChatMessageType.SYSTEM) {
            SystemMessage systemMessage = (SystemMessage) message;
            redisMessage.setTextContent(systemMessage.text());
            redisMessage.setType(String.valueOf(ChatMessageType.SYSTEM));
        }
        return JSONUtil.toJsonStr(redisMessage);
    }

    /**
     * 将Redis存储的JSON字符串反序列化为对应的Message对象
     *
     * @param redisMessage Redis存储的JSON格式消息数据 已转换为RedisMessage对象
     * @return 对应类型的Message对象
     * @throws RuntimeException 当无法识别的消息类型时抛出异常
     */

    public static ChatMessage toMessage(RedisMessage redisMessage) {
        ChatMessageType messageType = ChatMessageType.valueOf(redisMessage.getType());
        switch (messageType) {
            case SYSTEM -> {
                String textContent = redisMessage.getTextContent();
                return new SystemMessage(textContent);
            }
            case USER -> {
                String textContent = redisMessage.getTextContent();
                return new UserMessage(textContent);
            }
            case AI -> {
                Map<String, Object> metadata = redisMessage.getMetadata();
                if (metadata.isEmpty()) {
                    return new MyAiMessage(redisMessage.getTextContent(), redisMessage.getParams());
                }
                //设置ai调用工具的一些参数
                String id = metadata.get(ID).toString();
                String name = metadata.get(NAME).toString();
                String arguments = metadata.get(ARGUMENTS).toString();
                ToolExecutionRequest toolExecutionRequest = ToolExecutionRequest.builder()
                        .id(id)
                        .name(name)
                        .arguments(arguments)
                        .build();
                List<ToolExecutionRequest> toolExecutionRequests = List.of(toolExecutionRequest);
                return new MyAiMessage(redisMessage.getTextContent(), toolExecutionRequests, redisMessage.getParams());
            }
            case TOOL_EXECUTION_RESULT -> {
                String toolCallId = redisMessage.getMetadata().get(ID).toString();
                String toolName = redisMessage.getMetadata().get(TOOL_NAME).toString();
                String textContent = redisMessage.getTextContent();
                return new ToolExecutionResultMessage(toolCallId, toolName, textContent);
            }
        }
        throw new RuntimeException("Message data conversion failed.");
    }

    /**
     * 将 ToolExecutionRequest 列表转换为 MyToolExecutionRequest 列表
     *
     * @param toolExecutionRequests 原始工具执行请求列表
     * @return 转换后的 MyToolExecutionRequest 列表
     */
    private static List<MyToolExecutionRequest> convertToMyToolExecutionRequests(List<ToolExecutionRequest> toolExecutionRequests) {
        if (toolExecutionRequests == null || toolExecutionRequests.isEmpty()) {
            return new ArrayList<>();
        }

        return toolExecutionRequests.stream()
                .map(request -> new MyToolExecutionRequest(
                        request.id(),
                        request.name(),
                        request.arguments()
                ))
                .collect(Collectors.toList());
    }

    private static List<ToolExecutionRequest> convertToToolExecutionRequests(List<MyToolExecutionRequest> myToolExecutionRequests) {
        if (myToolExecutionRequests == null
                || myToolExecutionRequests.isEmpty()
                || myToolExecutionRequests.stream().allMatch(Objects::isNull)) {
            return new ArrayList<>();
        }

        return myToolExecutionRequests.stream()
                .map(request -> ToolExecutionRequest.builder()
                        .id(request.id())
                        .name(request.name())
                        .arguments(request.arguments())
                        .build()
                ).collect(Collectors.toList());
    }
}
