package com.fortunate.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * AI聊天响应VO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiChatResponseVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * AI回复内容
     */
    private String reply;

    /**
     * 意图识别结果
     */
    private String intent;

    /**
     * 是否包含订单信息
     */
    private Boolean hasOrderInfo;

    /**
     * 订单信息列表（如果是订单查询）
     */
    private List<OrderInfoVO> orderList;

    /**
     * 是否包含菜品推荐
     */
    private Boolean hasDishRecommend;

    /**
     * 推荐菜品列表（如果是菜品推荐）
     */
    private List<DishRecommendVO> dishList;

    /**
     * 订单信息VO
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderInfoVO implements Serializable {
        private String orderNumber;
        private String status;
        private String statusText;
        private String orderTime;
        private Double amount;
        private String dishNames;
    }

    /**
     * 菜品推荐VO
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DishRecommendVO implements Serializable {
        private Long dishId;
        private String dishName;
        private String description;
        private Double price;
        private String image;
        private String category;
    }
}
