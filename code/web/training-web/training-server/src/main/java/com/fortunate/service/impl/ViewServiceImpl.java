package com.fortunate.service.impl;

import com.fortunate.context.BaseContext;
import com.fortunate.entity.Dish;
import com.fortunate.entity.ItemView;
import com.fortunate.mapper.DishMapper;
import com.fortunate.mapper.ItemViewMapper;
import com.fortunate.service.ViewService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.fortunate.constant.Constants.DISH;
import static com.fortunate.constant.MessageConstant.INVALID_PARAM;

@Service
@RequiredArgsConstructor
public class ViewServiceImpl implements ViewService {
    private final ItemViewMapper itemViewMapper;

    /**
     * 菜品后套餐浏览数加1
     *
     * @param dishOrSetmealId
     */
    @Override
    public void incrementViewCount(Long dishOrSetmealId, String type, Long userId) {
        //1.参数校验
        if (dishOrSetmealId == null) {
            throw new RuntimeException(INVALID_PARAM);
        }
        //根据商品id和用户id和商品类型查询
        ItemView itemView = itemViewMapper.selectByItemIdAndUserIdAndType(dishOrSetmealId, userId, type);
        //说明用户没有浏览过这个菜品或套餐
        if (itemView == null) {
            //进行新增操作
            itemView = new ItemView();
            itemView.setItemId(dishOrSetmealId);
            itemView.setItemType(type);
            itemView.setUserId(userId);
            itemView.setViewCount(1);
            itemViewMapper.insert(itemView);
            return;
        }
        //执行到这里说明已经浏览过
        //直接进行更新操作
        itemViewMapper.updateViewCount(itemView);
    }
}
