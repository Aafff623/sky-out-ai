package com.fortunate.websocket;

import com.alibaba.fastjson.JSON;
import com.fortunate.constant.CustomerServiceConstant;
import com.fortunate.dto.CustomerServiceMessageDTO;
import com.fortunate.entity.CustomerServiceMessage;
import com.fortunate.service.CustomerServiceService;
//import jakarta.websocket.Session;
import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.ContextLoader;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 客服WebSocket服务器
 */
@Slf4j
@Component
@ServerEndpoint("/ws/customer-service/{sessionId}/{userType}")
public class CustomerServiceWebSocketServer {

    // 存放会话对象 key: sessionId, value: Session
    private static final Map<String, Session> sessionMap = new ConcurrentHashMap<>();
    
    // 存放用户类型 key: sessionId, value: userType (user/staff)
    private static final Map<String, String> userTypeMap = new ConcurrentHashMap<>();

    private static CustomerServiceService customerServiceService;

    @Autowired
    public void setCustomerServiceService(CustomerServiceService customerServiceService) {
        CustomerServiceWebSocketServer.customerServiceService = customerServiceService;
    }

    /**
     * 获取CustomerServiceService实例
     */
    private CustomerServiceService getCustomerServiceService() {
        if (customerServiceService == null) {
            customerServiceService = ContextLoader.getCurrentWebApplicationContext()
                    .getBean(CustomerServiceService.class);
        }
        return customerServiceService;
    }

    /**
     * 连接建立成功回调方法
     * @param session WebSocket会话
     * @param sessionId 客服会话ID
     * @param userType 用户类型 user/staff
     */
    @OnOpen
    public void onOpen(Session session, @PathParam("sessionId") String sessionId, @PathParam("userType") String userType) {
        log.info("客服WebSocket连接建立，会话ID：{}，用户类型：{}", sessionId, userType);
        sessionMap.put(sessionId + "_" + userType, session);
        userTypeMap.put(sessionId + "_" + userType, userType);
        
        // 发送连接成功消息
        Map<String, Object> connectMessage = new HashMap<>();
        connectMessage.put("type", "system");
        connectMessage.put("message", "连接成功");
        connectMessage.put("timestamp", System.currentTimeMillis());
        sendMessage(session, JSON.toJSONString(connectMessage));
    }

    /**
     * 收到客户端消息后回调方法
     * @param message 客户端发送的消息
     * @param sessionId 客服会话ID
     * @param userType 用户类型
     */
    @OnMessage
    public void onMessage(String message, @PathParam("sessionId") String sessionId, @PathParam("userType") String userType) {
        log.info("收到客服消息，会话ID：{}，用户类型：{}，消息：{}", sessionId, userType, message);
        
        try {
            // 解析消息
            Map<String, Object> messageMap = JSON.parseObject(message, Map.class);
            String content = (String) messageMap.get("content");
            Integer messageType = (Integer) messageMap.getOrDefault("messageType", CustomerServiceConstant.MESSAGE_TYPE_TEXT);
            
            // 构建消息DTO
            CustomerServiceMessageDTO messageDTO = new CustomerServiceMessageDTO();
            messageDTO.setSessionId(sessionId);
            messageDTO.setContent(content);
            messageDTO.setMessageType(messageType);

            // 保存消息到数据库
            CustomerServiceMessage savedMessage;
            CustomerServiceService service = getCustomerServiceService();
            if ("user".equals(userType)) {
                // 用户发送消息
                savedMessage = service.sendMessage(messageDTO);
            } else {
                // TODO 客服发送消息，这里使用默认客服ID，实际应该从JWT中获取
                savedMessage = service.sendStaffMessage(messageDTO, 1L);
            }
            
            // 构建转发消息
            Map<String, Object> forwardMessage = new HashMap<>();
            forwardMessage.put("type", "message");
            forwardMessage.put("sessionId", sessionId);
            forwardMessage.put("senderType", savedMessage.getSenderType());
            forwardMessage.put("senderId", savedMessage.getSenderId());
            forwardMessage.put("content", content);
            forwardMessage.put("messageType", messageType);
            forwardMessage.put("timestamp", System.currentTimeMillis());
            
            String forwardMessageJson = JSON.toJSONString(forwardMessage);
            
            // 转发消息给对方
            if ("user".equals(userType)) {
                // 用户发送的消息，转发给客服
                sendToStaff(sessionId, forwardMessageJson);
            } else if ("staff".equals(userType)) {
                // 客服发送的消息，转发给用户
                sendToUser(sessionId, forwardMessageJson);
            }
            
        } catch (Exception e) {
            log.error("处理客服消息失败", e);
            sendErrorMessage(sessionId + "_" + userType, "消息处理失败");
        }
    }

    /**
     * 连接关闭回调方法
     * @param sessionId 客服会话ID
     * @param userType 用户类型
     */
    @OnClose
    public void onClose(@PathParam("sessionId") String sessionId, @PathParam("userType") String userType) {
        String key = sessionId + "_" + userType;
        log.info("客服WebSocket连接关闭，会话ID：{}，用户类型：{}", sessionId, userType);
        sessionMap.remove(key);
        userTypeMap.remove(key);
    }

    /**
     * 连接发生错误时回调方法
     * @param session WebSocket会话
     * @param error 错误信息
     */
    @OnError
    public void onError(Session session, Throwable error) {
        log.error("客服WebSocket发生错误", error);
    }

    /**
     * 向用户发送消息
     * @param sessionId 会话ID
     * @param message 消息内容
     */
    public void sendToUser(String sessionId, String message) {
        String userKey = sessionId + "_user";
        Session session = sessionMap.get(userKey);
        if (session != null) {
            sendMessage(session, message);
        } else {
            log.warn("用户WebSocket连接不存在，会话ID：{}", sessionId);
        }
    }

    /**
     * 向客服发送消息
     * @param sessionId 会话ID
     * @param message 消息内容
     */
    public void sendToStaff(String sessionId, String message) {
        String staffKey = sessionId + "_staff";
        Session session = sessionMap.get(staffKey);
        if (session != null) {
            sendMessage(session, message);
        } else {
            log.warn("客服WebSocket连接不存在，会话ID：{}", sessionId);
        }
    }

    /**
     * 发送错误消息
     * @param key 连接key
     * @param errorMessage 错误消息
     */
    private void sendErrorMessage(String key, String errorMessage) {
        Session session = sessionMap.get(key);
        if (session != null) {
            Map<String, Object> error = new HashMap<>();
            error.put("type", "error");
            error.put("message", errorMessage);
            error.put("timestamp", System.currentTimeMillis());
            sendMessage(session, JSON.toJSONString(error));
        }
    }

    /**
     * 发送消息
     * @param session WebSocket会话
     * @param message 消息内容
     */
    private void sendMessage(Session session, String message) {
        try {
            session.getBasicRemote().sendText(message);
        } catch (Exception e) {
            log.error("发送WebSocket消息失败", e);
        }
    }

    /**
     * 获取在线连接数
     * @return 在线连接数
     */
    public static int getOnlineCount() {
        return sessionMap.size();
    }
}
