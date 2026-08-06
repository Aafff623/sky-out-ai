package com.fortunate.tools;

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
    public List<Orders> getOrdersByUserId() {
        Long userId = (Long) ToolContext.get(ToolContext.USER_ID);
        List<Orders> ordersByUserId = orderMapper.getOrdersByUserId(userId, 5);
        return ordersByUserId;
    }

    @Tool("查询用户指定状态的5个订单")
    public List<OrderVO> getOrdersByUserIdAndStatus(@P(value = "订单状态") Integer status) {
        PageResult pageResult = orderService.pageQueryUser(1, 5, status);
        List<OrderVO> records = (List<OrderVO>) pageResult.getRecords();
        return records ;
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
    public String cancelOrder(@P(value = "订单ID") Long orderId, @P(value = "取消原因") String reason) {
        OrderCancelDTO orderCancelDTO = new OrderCancelDTO(orderId, reason);
        try {
            orderService.userCancelById(orderCancelDTO);
            return ORDER_CANCEL_SUCCESS;
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(ORDER_CANCEL_FAILED);
        }
    }


}
