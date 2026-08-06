package com.fortunate.service;

import com.fortunate.dto.CustomerServiceMessageDTO;
import com.fortunate.entity.CustomerServiceMessage;
import com.fortunate.entity.CustomerServiceSession;
import com.fortunate.vo.CustomerServiceSessionVO;

import java.util.List;

/**
 * 客服服务接口
 */
public interface CustomerServiceService {

    /**
     * 创建或获取客服会话
     * @param userId 用户ID
     * @param serviceType 服务类型 1人工客服 2AI助手
     * @return 会话信息
     */
    CustomerServiceSession createOrGetSession(Long userId, Integer serviceType);

    /**
     * 发送消息
     * @param messageDTO 消息DTO
     * @return 消息记录
     */
    CustomerServiceMessage sendMessage(CustomerServiceMessageDTO messageDTO);

    /**
     * 客服发送消息
     * @param messageDTO 消息DTO
     * @param staffId 客服人员ID
     * @return 消息记录
     */
    CustomerServiceMessage sendStaffMessage(CustomerServiceMessageDTO messageDTO, Long staffId);

    /**
     * 获取会话消息列表
     * @param sessionId 会话ID
     * @return 消息列表
     */
    List<CustomerServiceMessage> getSessionMessages(String sessionId,Integer type);

    /**
     * 结束会话
     * @param sessionId 会话ID
     */
    void endSession(String sessionId);

    /**
     * 分配客服人员
     * @param sessionId 会话ID
     * @param staffId 客服人员ID
     */
    void assignStaff(String sessionId, Long staffId);

    /**
     * 获取客服人员的会话列表
     * @param staffId 客服人员ID
     * @return 会话列表
     */
    List<CustomerServiceSessionVO> getStaffSessions(Long staffId);

    /**
     * 获取所有进行中的人工客服会话
     * @return 会话列表
     */
    List<CustomerServiceSessionVO> getActiveHumanSessions();

    /**
     * 根据会话ID获取会话详情
     * @param sessionId 会话ID
     * @return 会话详情
     */
    CustomerServiceSessionVO getSessionDetail(String sessionId);
}
