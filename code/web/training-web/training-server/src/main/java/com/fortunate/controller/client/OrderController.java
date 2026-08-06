package com.fortunate.controller.client;

import com.fortunate.dto.OrderCancelDTO;
import com.fortunate.dto.OrdersPaymentDTO;
import com.fortunate.dto.OrdersSubmitDTO;
import com.fortunate.result.PageResult;
import com.fortunate.result.Result;
import com.fortunate.service.OrderService;
import com.fortunate.vo.OrderSubmitVO;
import com.fortunate.vo.OrderVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * @description
 */
@Slf4j
@Api(tags = "C端-订单相关接口")
@RestController("clientOrderController")
@RequestMapping("/client/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    /**
     * 用户下单
     *
     * @param ordersSubmitDTO 订单提交DTO
     */
    @PostMapping("/submit")
    @ApiOperation("用户下单")
    public Result<OrderSubmitVO> submit(@RequestBody OrdersSubmitDTO ordersSubmitDTO) {
        log.info("用户下单，参数==> {}", ordersSubmitDTO);
        OrderSubmitVO orderSubmitVO = orderService.submitOrder(ordersSubmitDTO);
        return Result.success(orderSubmitVO);
    }

    /**
     * 订单支付
     *
     * @param ordersPaymentDTO 订单支付参数
     */
    @PutMapping("/payment")
    @ApiOperation("订单支付")
    public Result<?> payment(@RequestBody OrdersPaymentDTO ordersPaymentDTO) {
        /* 模拟支付成功 */
        orderService.paySuccess(ordersPaymentDTO.getOrderNumber());
        log.info("模拟交易成功==> {}", ordersPaymentDTO.getOrderNumber());
        return Result.success();
    }

    /**
     * 客户催单
     */
    @GetMapping("/reminder")
    @ApiOperation("客户催单")
    public Result<?> reminder(@RequestParam Long id) {
        orderService.reminder(id);
        return Result.success();
    }

    /**
     * 更新支付方式
     */
    @PutMapping("/updatePayStatus")
    @ApiOperation("更新支付方式")
    public Result<?> updatePayStatus(@RequestBody OrdersPaymentDTO ordersPaymentDTO) {
        orderService.updatePayStatus(ordersPaymentDTO);
        return Result.success();
    }

    /**
     * 历史订单查询
     *
     * @param page     页码
     * @param pageSize 每页数量
     * @param status   订单状态 1待付款 2待接单 3已接单 4派送中 5已完成 6已取消
     */
    @GetMapping("/historyOrders")
    @ApiOperation("历史订单查询")
    public Result<PageResult> page(@RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "5") int pageSize, Integer status) {
        PageResult pageResult = orderService.pageQueryUser(page, pageSize, status);
        return Result.success(pageResult);
    }

    /**
     * 根据id查询订单详情
     *
     * @param id 订单id
     */
    @GetMapping("/orderDetail")
    @ApiOperation("根据id查询订单详情")
    public Result<OrderVO> details(@RequestParam Long id) {
        OrderVO orderVO = orderService.details(id);
        return Result.success(orderVO);
    }

    /**
     * 用户取消订单
     *
     * @param orderCancelDTO 订单取消dto
     */
    @PutMapping("/cancel")
    @ApiOperation("用户取消订单")
    public Result<?> cancel(@RequestBody OrderCancelDTO orderCancelDTO) throws Exception {
        orderService.userCancelById(orderCancelDTO);
        return Result.success();
    }

    /**
     * 再来一单
     *
     * @param id 订单id
     */
    @PutMapping("/repetition/{id}")
    @ApiOperation("再来一单")
    public Result<?> repetition(@PathVariable Long id) {
        orderService.repetition(id);
        return Result.success();
    }
}
