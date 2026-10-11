package com.fortunate.websocket;

import com.alibaba.fastjson.JSON;
import com.fortunate.constant.CustomerServiceConstant;
import com.fortunate.constant.JwtClaimsConstant;
import com.fortunate.dto.CustomerServiceMessageDTO;
import com.fortunate.entity.CustomerServiceMessage;
import com.fortunate.properties.JwtProperties;
import com.fortunate.service.CustomerServiceService;
import com.fortunate.utils.JwtUtil;
import com.fortunate.vo.CustomerServiceSessionVO;
//import jakarta.websocket.Session;
import io.jsonwebtoken.Claims;
import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.ContextLoader;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 客服WebSocket服务器
 * 握手鉴权：客户端需在 URL 上携带 ?token=（商家端 token / 用户端 authentication），
 * 未通过鉴权的连接在 onOpen 阶段直接关闭，不进入会话表。
 */
@Slf4j
@Component
@ServerEndpoint("/ws/customer-service/{sessionId}/{userType}")
public class CustomerServiceWebSocketServer {

    // 存放会话对象 key: sessionId, value: Session
    private static final Map<String, Session> sessionMap = new ConcurrentHashMap<>();

    // 存放用户类型 key: sessionId, value: userType (user/staff)
    private static final Map<String, String> userTypeMap = new ConcurrentHashMap<>();

    // 存放客服身份 key: sessionId_userType, value: empId（来自 JWT，非客户端自报）
    private static final Map<String, Long> staffIdMap = new ConcurrentHashMap<>();

    private static CustomerServiceService customerServiceService;
    private static JwtProperties jwtProperties;

    @Autowired
    public void setCustomerServiceService(CustomerServiceService customerServiceService) {
        CustomerServiceWebSocketServer.customerServiceService = customerServiceService;
    }

    @Autowired
    public void setJwtProperties(JwtProperties jwtProperties) {
        CustomerServiceWebSocketServer.jwtProperties = jwtProperties;
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
        if (!authenticate(session, sessionId, userType)) {
            log.warn("客服WebSocket鉴权失败，拒绝连接。会话ID：{}，用户类型：{}", sessionId, userType);
            try {
                session.close(new CloseReason(CloseReason.CloseCodes.VIOLATED_POLICY, "unauthorized"));
            } catch (IOException e) {
                log.error("关闭未授权客服WebSocket连接失败", e);
            }
            return;
        }
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
     * 握手鉴权：校验 token 有效性与会话归属
     * @param session   WebSocket会话
     * @param sessionId 客服会话ID
     * @param userType  user/staff
     * @return 是否通过
     */
    private boolean authenticate(Session session, String sessionId, String userType) {
        try {
            List<String> tokens = session.getRequestParameterMap().get("token");
            if (tokens == null || tokens.isEmpty() || jwtProperties == null) {
                return false;
            }
            String token = tokens.get(0);
            CustomerServiceSessionVO sessionVO = getCustomerServiceService().getSessionDetail(sessionId);
            if (sessionVO == null) {
                return false;
            }
            if ("staff".equals(userType)) {
                Claims claims = JwtUtil.parseJWT(jwtProperties.getStoreSecretKey(), token);
                Long empId = Long.valueOf(claims.get(JwtClaimsConstant.EMP_ID).toString());
                // 会话未分配客服时允许接入（随后通过 HTTP 分配），已分配则仅限本人
                if (sessionVO.getStaffId() != null && !empId.equals(sessionVO.getStaffId())) {
                    return false;
                }
                staffIdMap.put(sessionId + "_" + userType, empId);
                return true;
            }
            if ("user".equals(userType)) {
                Claims claims = JwtUtil.parseJWT(jwtProperties.getClientSecretKey(), token);
                Long userId = Long.valueOf(claims.get(JwtClaimsConstant.USER_ID).toString());
                // 用户只能进入自己的客服会话
                return userId.equals(sessionVO.getUserId());
            }
            return false;
        } catch (Exception e) {
            log.warn("客服WebSocket鉴权异常。会话ID：{}，用户类型：{}", sessionId, userType, e);
            return false;
        }
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
                // 客服发送消息，身份取自握手时校验的 JWT（empId）
                Long empId = staffIdMap.get(sessionId + "_" + userType);
                savedMessage = service.sendStaffMessage(messageDTO, empId);
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
        staffIdMap.remove(key);
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
