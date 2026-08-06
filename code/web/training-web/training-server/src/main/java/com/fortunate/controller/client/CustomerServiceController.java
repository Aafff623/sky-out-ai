package com.fortunate.controller.client;

import com.fortunate.constant.CustomerServiceConstant;
import com.fortunate.context.BaseContext;
import com.fortunate.entity.CustomerServiceMessage;
import com.fortunate.entity.CustomerServiceSession;
import com.fortunate.result.Result;
import com.fortunate.service.CustomerServiceService;
import com.fortunate.vo.CustomerServiceSessionVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 客服相关接口 - 用户端
 */
@Slf4j
@Api(tags = "客服相关接口")
@RestController("clientCustomerServiceController")
@RequestMapping("/client/customer-service")
@RequiredArgsConstructor
public class CustomerServiceController {

    private final CustomerServiceService customerServiceService;

    /**
     * 创建或获取人工客服会话
     */
    @PostMapping("/session/human")
    @ApiOperation("创建或获取人工客服会话")
    public Result<CustomerServiceSession> createHumanSession() {
        log.info("用户创建人工客服会话");
        // 这里需要从JWT中获取用户ID，暂时使用固定值
        Long userId = BaseContext.getUserId();

        CustomerServiceSession session = customerServiceService.createOrGetSession(userId, CustomerServiceConstant.SERVICE_TYPE_HUMAN);
        return Result.success(session);
    }

    /**
     * 创建或获取AI助手会话
     */
    @PostMapping("/session/ai")
    @ApiOperation("创建或获取AI助手会话")
    public Result<CustomerServiceSession> createAISession() {
        log.info("用户创建AI助手会话");
        // 这里需要从JWT中获取用户ID，暂时使用固定值
        Long userId = BaseContext.getUserId();

        CustomerServiceSession session = customerServiceService.createOrGetSession(userId, CustomerServiceConstant.SERVICE_TYPE_AI);
        return Result.success(session);
    }

    /**
     * 获取会话消息列表
     *
     * @param sessionId 会话ID
     */
    @GetMapping("/messages/{sessionId}")
    @ApiOperation("获取会话消息列表")
    public Result<List<CustomerServiceMessage>> getSessionMessages(@PathVariable String sessionId, Integer type) {
        log.info("获取会话消息列表，会话ID：{}", sessionId);
        List<CustomerServiceMessage> messages = customerServiceService.getSessionMessages(sessionId, type);
        return Result.success(messages);
    }

    /**
     * 获取会话详情
     *
     * @param sessionId 会话ID
     */
    @GetMapping("/session/{sessionId}")
    @ApiOperation("获取会话详情")
    public Result<CustomerServiceSessionVO> getSessionDetail(@PathVariable String sessionId, String type) {
        log.info("获取会话详情，会话ID：{}", sessionId);
        CustomerServiceSessionVO sessionVO = customerServiceService.getSessionDetail(sessionId);
        return Result.success(sessionVO);
    }

    /**
     * 结束会话
     *
     * @param sessionId 会话ID
     */
    @PutMapping("/session/{sessionId}/end")
    @ApiOperation("结束会话")
    public Result<?> endSession(@PathVariable String sessionId) {
        log.info("结束会话，会话ID：{}", sessionId);
        customerServiceService.endSession(sessionId);
        return Result.success();
    }
}
