package com.fortunate.service.impl;

import com.alibaba.fastjson.JSON;
import com.fortunate.constant.CustomerServiceConstant;
import com.fortunate.context.BaseContext;
import com.fortunate.dto.CustomerServiceMessageDTO;
import com.fortunate.entity.CustomerServiceMessage;
import com.fortunate.entity.CustomerServiceSession;
import com.fortunate.entity.Dish;
import com.fortunate.entity.DishFlavor;
import com.fortunate.mapper.CategoryMapper;
import com.fortunate.mapper.CustomerServiceSessionMapper;
import com.fortunate.mapper.DishFlavorMapper;
import com.fortunate.repository.MyAiMessage;
import com.fortunate.repository.RedisChatMemoryStore;
import com.fortunate.service.CustomerServiceService;
import com.fortunate.service.DishService;
import com.fortunate.vo.CustomerServiceSessionVO;
import com.fortunate.vo.DishVO;
import dev.langchain4j.data.message.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.fortunate.constant.CustomerServiceConstant.REDIS_KEY_SESSION_MESSAGES;
import static com.fortunate.constant.CustomerServiceConstant.SERVICE_TYPE_HUMAN;

/**
 * 客服服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CustomerServiceServiceImpl implements CustomerServiceService {

    private final CustomerServiceSessionMapper sessionMapper;
    private final StringRedisTemplate redisTemplate;
    private final RedisChatMemoryStore redisChatMemoryStore;

    @Override
    @Transactional
    public CustomerServiceSession createOrGetSession(Long userId, Integer serviceType) {
        // 查询是否存在进行中的会话
        CustomerServiceSession existingSession = sessionMapper.getActiveSessionByUserIdAndType(userId, serviceType);

        if (existingSession != null) {
            log.info("找到现有会话，用户ID：{}，服务类型：{}，会话ID：{}", userId, serviceType, existingSession.getSessionId());
            // 检查现有会话的sessionId是否为空
            if (existingSession.getSessionId() == null || existingSession.getSessionId().isEmpty()) {
                log.warn("现有会话的sessionId为空，删除并创建新会话");
                // 如果sessionId为空，删除这个无效会话并创建新的
                sessionMapper.updateStatus(existingSession.getSessionId(), CustomerServiceConstant.SESSION_STATUS_ENDED, LocalDateTime.now());
            } else {
                return existingSession;
            }
        }

        // 创建新会话
        String newSessionId = UUID.randomUUID().toString().replace("-", "");
        CustomerServiceSession session = CustomerServiceSession.builder()
                .userId(userId)
                .sessionId(newSessionId)
                .serviceType(serviceType)
                .status(CustomerServiceConstant.SESSION_STATUS_ACTIVE)
                .createTime(LocalDateTime.now())
                .build();

        sessionMapper.insert(session);
        log.info("创建新的客服会话，用户ID：{}，服务类型：{}，会话ID：{}", userId, serviceType, session.getSessionId());

        return session;
    }

    /**
     * 保存用户向客户提问消息
     *
     * @param messageDTO 消息DTO
     * @return
     */
    @Override
    @Transactional
    public CustomerServiceMessage sendMessage(CustomerServiceMessageDTO messageDTO) {
        // 获取当前用户ID（从上下文中获取，如果为空则使用默认值）
        Long currentUserId = BaseContext.getUserId();
        if (currentUserId == null) {
            // 临时使用默认用户ID，实际应该从JWT中获取
            currentUserId = 1L;
        }

        // 构建消息对象
        CustomerServiceMessage message = CustomerServiceMessage.builder()
                .sessionId(messageDTO.getSessionId())
                // 默认为用户发送
                .senderType(CustomerServiceConstant.SENDER_TYPE_USER)
                .senderId(currentUserId)
                .messageType(messageDTO.getMessageType())
                .content(messageDTO.getContent())
                .createTime(LocalDateTime.now())
                .build();

        redisMethods(messageDTO, message);

        return message;
    }

    /**
     * redis存储数据统一方法
     *
     * @param messageDTO
     * @param message
     */
    private void redisMethods(CustomerServiceMessageDTO messageDTO, CustomerServiceMessage message) {
        //从redis中查询原始记录
        String json = redisTemplate.opsForValue().get(REDIS_KEY_SESSION_MESSAGES + messageDTO.getSessionId());
        List<CustomerServiceMessage> customerServiceMessages;
        if (json != null && !json.isEmpty()) {
            customerServiceMessages = JSON.parseArray(json, CustomerServiceMessage.class);
        } else {
            // 如果Redis中没有记录，创建一个新的空列表
            customerServiceMessages = new ArrayList<>();
        }
        //添加新纪录
        customerServiceMessages.add(message);
        //保存到redis
        redisTemplate.opsForValue().set(REDIS_KEY_SESSION_MESSAGES + messageDTO.getSessionId(),
                JSON.toJSONString(customerServiceMessages), Duration.ofDays(1));
        log.info("用户发送消息，会话ID：{}，内容：{}", messageDTO.getSessionId(), messageDTO.getContent());
    }

    /**
     * 客服发送消息
     *
     * @param messageDTO 消息DTO
     * @param staffId    客服人员ID
     * @return
     */
    @Override
    @Transactional
    public CustomerServiceMessage sendStaffMessage(CustomerServiceMessageDTO messageDTO, Long staffId) {
        // 构建消息对象
        CustomerServiceMessage message = CustomerServiceMessage.builder()
                .sessionId(messageDTO.getSessionId())
                .senderType(CustomerServiceConstant.SENDER_TYPE_STAFF) // 客服发送
                .senderId(staffId)
                .messageType(messageDTO.getMessageType())
                .content(messageDTO.getContent())
                .createTime(LocalDateTime.now())
                .build();

        //从redis中查询原始记录
        redisMethods(messageDTO, message);
        log.info("客服发送消息，会话ID：{}，客服ID：{}，内容：{}", messageDTO.getSessionId(), staffId, messageDTO.getContent());

        return message;
    }

    /**
     * 获取聊天记录
     *
     * @param sessionId 会话ID
     * @return
     */
    @Override
    public List<CustomerServiceMessage> getSessionMessages(String sessionId, Integer type) {
        //获取redis中的数据
        String redisKey = REDIS_KEY_SESSION_MESSAGES + sessionId;
        if (type.equals(SERVICE_TYPE_HUMAN)) {
            String json = redisTemplate.opsForValue().get(redisKey);
            List<CustomerServiceMessage> customerServiceMessages = JSON.parseArray(json, CustomerServiceMessage.class);
            return customerServiceMessages;
        }

        List<ChatMessage> messages = redisChatMemoryStore.getMessages(redisKey);
        //创建集合存储内容
        List<CustomerServiceMessage> customerServiceMessages = new ArrayList<>();
        // 用于暂存上一次AI工具调用的params
        Map<String, Object> pendingParams = null;
        for (ChatMessage message : messages) {
            CustomerServiceMessage customerServiceMessage = new CustomerServiceMessage();
            if (message.type() == ChatMessageType.USER) {
                UserMessage userMessage = (UserMessage) message;
                //设置会话id
                customerServiceMessage.setSessionId(sessionId);
                //设置发送者类型
                customerServiceMessage.setSenderType(CustomerServiceConstant.SENDER_TYPE_USER);
                //设置发送者ID
                customerServiceMessage.setSenderId(BaseContext.getUserId());
                //设置消息类型
                customerServiceMessage.setMessageType(CustomerServiceConstant.MESSAGE_TYPE_TEXT);
                //设置用户内容
                customerServiceMessage.setContent(userMessage.singleText());
                //添加到集合
                customerServiceMessages.add(customerServiceMessage);
            } else if (message.type() == ChatMessageType.AI) {
                //非用户
                MyAiMessage aiMessage = (MyAiMessage) message;
                //工具调用也会存储到redis中但是text是空的需要跳过
                if (aiMessage.text() == null || aiMessage.text().trim().isEmpty()) {
                    // 这是工具调用，只包含params，不输出文本
                    Map<String, Object> params = aiMessage.getParams();
                    if (params != null && !params.isEmpty()) {
                        // 暂存params，等待下一个AI回复
                        pendingParams = params;
                    }
                    // 不添加空消息到列表
                    continue;
                } else {
                    // 这是真正的AI回复
                    customerServiceMessage.setSessionId(sessionId);
                    customerServiceMessage.setSenderType(CustomerServiceConstant.SENDER_TYPE_AI);
                    customerServiceMessage.setContent(aiMessage.text());
                    customerServiceMessage.setMessageType(CustomerServiceConstant.MESSAGE_TYPE_TEXT);
                    // 如果有之前暂存的params，就附加到当前消息
                    if (pendingParams != null) {
                        customerServiceMessage.setParams(pendingParams);
                        // 清除缓存
                        pendingParams = null;
                    }
                    //添加到集合
                    customerServiceMessages.add(customerServiceMessage);
                }
            }
            log.info("获取AI助手聊天记录，内容：{}", customerServiceMessages);
        }
        return customerServiceMessages;
    }

    /**
     * 结束会话
     * @param sessionId 会话ID
     */
    @Override
    @Transactional
    public void endSession(String sessionId) {
        sessionMapper.updateStatus(sessionId, CustomerServiceConstant.SESSION_STATUS_ENDED, LocalDateTime.now());
        log.info("结束客服会话，会话ID：{}", sessionId);
    }

    @Override
    @Transactional
    public void assignStaff(String sessionId, Long staffId) {
        sessionMapper.assignStaff(sessionId, staffId);
        log.info("分配客服人员，会话ID：{}，客服ID：{}", sessionId, staffId);
    }

    /**
     * 获取客服人员会话列表
     *
     * @param staffId 客服人员ID
     * @return
     */
    @Override
    public List<CustomerServiceSessionVO> getStaffSessions(Long staffId) {
        return sessionMapper.getSessionsByStaffId(staffId);
    }

    /**
     * 获取所有进行中的人工会话
     *
     * @return
     */
    @Override
    public List<CustomerServiceSessionVO> getActiveHumanSessions() {
        //获取所有进行中的会话
        List<CustomerServiceSessionVO> activeHumanSessions = sessionMapper.getActiveHumanSessions();
        //遍历进行中的会话
        for (CustomerServiceSessionVO activeHumanSession : activeHumanSessions) {
            String redisKey = REDIS_KEY_SESSION_MESSAGES + activeHumanSession.getSessionId();
            String json = redisTemplate.opsForValue().get(redisKey);
            List<CustomerServiceMessage> customerServiceMessages = JSON.parseArray(json, CustomerServiceMessage.class);
            //获取最后一条消息
            CustomerServiceMessage lastMessage = null;
            if (customerServiceMessages != null) {
                lastMessage = customerServiceMessages.get(customerServiceMessages.size() - 1);
                //封装最后一条消息
                activeHumanSession.setLastMessage(lastMessage.getContent());
            }
        }
        return activeHumanSessions;
    }

    @Override
    public CustomerServiceSessionVO getSessionDetail(String sessionId) {
        CustomerServiceSession session = sessionMapper.getBySessionId(sessionId);
        if (session == null) {
            return null;
        }
        // 构建VO对象
        return CustomerServiceSessionVO.builder()
                .id(session.getId())
                .userId(session.getUserId())
                .sessionId(session.getSessionId())
                .serviceType(session.getServiceType())
                .status(session.getStatus())
                .staffId(session.getStaffId())
                .createTime(session.getCreateTime())
                .endTime(session.getEndTime())
                .build();
    }
}
