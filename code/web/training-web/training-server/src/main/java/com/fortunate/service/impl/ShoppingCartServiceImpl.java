package com.fortunate.service.impl;

import com.fortunate.context.BaseContext;
import com.fortunate.dto.ShoppingCartDTO;
import com.fortunate.entity.Dish;
import com.fortunate.entity.Setmeal;
import com.fortunate.entity.ShoppingCart;
import com.fortunate.mapper.DishMapper;
import com.fortunate.mapper.SetmealMapper;
import com.fortunate.mapper.ShoppingCartMapper;
import com.fortunate.service.ShoppingCartService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @description
 */
@Service
@RequiredArgsConstructor
public class ShoppingCartServiceImpl implements ShoppingCartService {

    private final DishMapper dishMapper;
    private final SetmealMapper setmealMapper;
    private final ShoppingCartMapper shoppingCartMapper;

    /**
     * 添加购物车
     * @param shoppingCartDTO 购物车DTO
     */
    @Override
    public void addShoppingCart(ShoppingCartDTO shoppingCartDTO) {
        // 判断当前加入到购物车中的商品是否已经存在
        ShoppingCart shoppingCart = new ShoppingCart();
        BeanUtils.copyProperties(shoppingCartDTO, shoppingCart);
        // 设置用户id
        Long userId = BaseContext.getUserId();
        shoppingCart.setUserId(userId);

        // 根据前端传递过来的数据（菜品id或者套餐id）查询购物车是否存在该商品
        List<ShoppingCart> list = shoppingCartMapper.list(shoppingCart);

        /* 情况一：能查询出来商品，说明已经存在，只需要将该商品数量加1即可 */
        if (list != null && !list.isEmpty()) {
            ShoppingCart cart = list.get(0);
            // 该商品数量加1
            cart.setNumber(cart.getNumber() + 1);
            // 更新数据库里面该商品的数量
            shoppingCartMapper.updateNumberById(cart);
            return;
        }

        /* 情况二：不存在该商品，需要插入到购物车，插入到购物车前需要判断本次添加的是菜品还是套餐 */
        Long dishId = shoppingCartDTO.getDishId();
        // 本次添加的是菜品，因为菜品还是套餐，只能二选一
        if (dishId != null) {
            Dish dish = dishMapper.getDishById(dishId);
            shoppingCart.setName(dish.getName());
            shoppingCart.setImage(dish.getImage());
            shoppingCart.setAmount(dish.getPrice());
        } else { // 本次添加的是套餐，菜品id为空，则套餐id肯定不为空
            Setmeal setmeal = setmealMapper.getSetmealById(shoppingCartDTO.getSetmealId());
            shoppingCart.setName(setmeal.getName());
            shoppingCart.setImage(setmeal.getImage());
            shoppingCart.setAmount(setmeal.getPrice());
        }

        // 新插入到购物车中的商品，默认数量为1
        shoppingCart.setNumber(1);
        shoppingCart.setCreateTime(LocalDateTime.now());
        shoppingCartMapper.insert(shoppingCart);
    }

    /**
     * 查询购物车数据
     * @return
     */
    @Override
    public List<ShoppingCart> showShoppingCart() {
        Long userId = BaseContext.getUserId();
        ShoppingCart shoppingCart = ShoppingCart.builder()
                .userId(userId)
                .build();

        return shoppingCartMapper.list(shoppingCart);
    }

    /**
     * 清空购物车
     */
    @Override
    public void cleanShoppingCart() {
        Long userId = BaseContext.getUserId();
        userId = userId != null ? userId : 1L;
        shoppingCartMapper.deleteByUserId(userId);
    }

    /**
     * 减少购物车商品数量
     * @param shoppingCartDTO 购物车DTO
     */
    @Override
    public void subShoppingCart(ShoppingCartDTO shoppingCartDTO) {
        ShoppingCart shoppingCart = new ShoppingCart();
        BeanUtils.copyProperties(shoppingCartDTO, shoppingCart);
        shoppingCart.setUserId(BaseContext.getUserId());

        List<ShoppingCart> list = shoppingCartMapper.list(shoppingCart);
        if (list == null || list.isEmpty()) {
            // 购物车中不存在该商品，直接返回
            return;
        }

        /* 用户购物车不为空，分为两种情况，一种是减少数量，一种是删除商品 */
        shoppingCart = list.get(0);

        // 1、获取当前商品的数量，判断数量，如果等于1，则删除该商品
        Integer number = shoppingCart.getNumber();
        if (number == 1) {
            shoppingCartMapper.deleteById(shoppingCart.getId());
            return;
        }

        // 数量大于1，则商品数量减1
        shoppingCart.setNumber(shoppingCart.getNumber() - 1);
        shoppingCartMapper.updateNumberById(shoppingCart);
    }

    /**
     * 修改购物车商品数量
     * @param shoppingCartDTO
     */
    @Override
    public void updateShoppingCartNumber(ShoppingCartDTO shoppingCartDTO) {
        Long userId = BaseContext.getUserId();
        userId = userId != null ? userId : 1L;
        Long dishId = shoppingCartDTO.getDishId();
        Long setmealId = shoppingCartDTO.getSetmealId();
        int number = shoppingCartDTO.getNumber();

        // 参数校验：必须恰好有一个id存在，且number非负
        boolean hasDishId = dishId != null;
        boolean hasSetmealId = setmealId != null;
        if (hasDishId == hasSetmealId || number < 0) {
            throw new IllegalArgumentException("传递的参数不合法");
        }

        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setUserId(userId);
        shoppingCart.setDishId(dishId);
        shoppingCart.setSetmealId(setmealId);

        List<ShoppingCart> cartList = shoppingCartMapper.list(shoppingCart);

        // 如果购物车中存在该商品，则更新商品数量
        if (cartList != null && !cartList.isEmpty()) {
            ShoppingCart cartItem = cartList.get(0);

            if (number > 0) {
                cartItem.setNumber(number);
                shoppingCartMapper.updateNumberById(cartItem);
            } else {
                // 如果传入的数量为0，删除该商品
                shoppingCartMapper.deleteById(cartItem.getId());
            }
        } else {
            throw new RuntimeException("该商品不存在于购物车中，无法更新数量");
        }
    }
}
