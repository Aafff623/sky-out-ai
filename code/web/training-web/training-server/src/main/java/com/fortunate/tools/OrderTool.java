package com.fortunate.tools;

import com.fortunate.context.BaseContext;
import com.fortunate.context.ToolContext;
import com.fortunate.dto.OrderCancelDTO;
import com.fortunate.entity.Orders;
import com.fortunate.exception.BaseException;
import com.fortunate.mapper.OrderMapper;
import com.fortunate.result.PageResult;
import com.fortunate.service.OrderService;
import com.fortunate.vo.OrderVO;
import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import dev.langchain4j.service.MemoryId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.fortunate.constant.MessageConstant.ORDER_CANCEL_FAILED;
import static com.fortunate.constant.MessageConstant.ORDER_CANCEL_SUCCESS;

@Component
@RequiredArgsConstructor
public class OrderTool {
    private final OrderMapper orderMapper;
    private final OrderService orderService;

    @Tool("查询用户最近的5个订单")
    public List<Orders> getOrdersByUserId(@MemoryId String memoryId) {
        Long userId = (Long) ToolContext.get(memoryId, ToolContext.USER_ID);
        return orderMapper.getOrdersByUserId(userId, 5);
    }

    @Tool("查询用户指定状态的5个订单")
    public List<OrderVO> getOrdersByUserIdAndStatus(@MemoryId String memoryId, @P(value = "订单状态") Integer status) {
        // 会话作用域里的用户身份透传到当前工具线程，供 Service 层归属校验读取
        Long userId = (Long) ToolContext.get(memoryId, ToolContext.USER_ID);
        BaseContext.setUserId(userId);
        try {
            PageResult pageResult = orderService.pageQueryUser(1, 5, status);
            return (List<OrderVO>) pageResult.getRecords();
        } finally {
            BaseContext.removeCurrentId();
        }
    }

    /**
     * 用户下单方法
     * @param orderId
     * @return
     */
//    @Tool("用户下单")
//    public String submitOrder(@P(value = "订单ID") Long orderId) {
//        Orders orders = orderMapper.getOrderById(orderId);
//        if (orders == null) {
//            throw new BaseException("订单不存在");
//        }
//        OrderVO orderVO = orderService.submitOrder(orders);
//        return "订单提交成功，订单号：" + orderVO.getNumber();
//    }

    @Tool("取消订单")
    public String cancelOrder(@MemoryId String memoryId, @P(value = "订单ID") Long orderId, @P(value = "取消原因") String reason) {
        Long userId = (Long) ToolContext.get(memoryId, ToolContext.USER_ID);
        BaseContext.setUserId(userId);
        try {
            OrderCancelDTO orderCancelDTO = new OrderCancelDTO(orderId, reason);
            orderService.userCancelById(orderCancelDTO);
            return ORDER_CANCEL_SUCCESS;
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(ORDER_CANCEL_FAILED);
        } finally {
            BaseContext.removeCurrentId();
        }
    }


}
