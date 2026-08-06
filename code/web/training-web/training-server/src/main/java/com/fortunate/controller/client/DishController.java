package com.fortunate.controller.client;

import com.fortunate.constant.CachePrefixConstant;
import com.fortunate.constant.StatusConstant;
import com.fortunate.entity.Dish;
import com.fortunate.result.Result;
import com.fortunate.service.DishService;
import com.fortunate.vo.DishVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @description
 */
@Slf4j
@Api(tags = "C端-菜品浏览接口")
@RestController("clientDishController")
@RequestMapping("/client/dish")
@RequiredArgsConstructor
public class DishController {

    private final DishService dishService;
    private final RedisTemplate<String, List<DishVO>> redisTemplate;

    /**
     * 根据分类id查询菜品
     *
     * @param categoryId 分类id
     * @param tabIndex   顶部标签 0 全部菜品 1热度菜品
     * @return 菜品列表
     */
    @GetMapping("/list")
    @ApiOperation("根据分类id查询菜品")
    public Result<List<DishVO>> list(@RequestParam(value = "categoryId", required = false) Long categoryId, @RequestParam("tabIndex") Integer tabIndex) {
        String key = CachePrefixConstant.DISH_KEY + categoryId + "_" + tabIndex;

        /* 查询redis中是否存在菜品数据，存在则直接返回 */
        List<DishVO> list = redisTemplate.opsForValue().get(key);
        if (list != null && !list.isEmpty()) {
            return Result.success(list);
        }

        Dish dish = new Dish();
        dish.setCategoryId(categoryId);
        dish.setStatus(StatusConstant.ENABLE);
        list = dishService.listWithFlavors(dish);


        //全部菜品直接返回
        if (tabIndex == 0) {
            /* 不存在则查询数据库并存入redis */
            redisTemplate.opsForValue().set(key, list);
            return Result.success(list);
        }

        //热度菜品 根据热度排序
        list.sort(Comparator.comparing(DishVO::getHotSpot, Comparator.nullsLast(Comparator.reverseOrder())));
        /* 不存在则查询数据库并存入redis */
        if (list.isEmpty()) {
            //说明查询了不存在的 数据 缓存空值 防止缓存穿透
            redisTemplate.opsForValue().set(key, list, 300, TimeUnit.SECONDS);
            return Result.success(list);
        } else {
            redisTemplate.opsForValue().set(key, list);
        }
        //返回
        return Result.success(list);
    }

    @GetMapping("/{id}")
    @ApiOperation("根据id查询菜品详情")
    public Result<DishVO> getById(@PathVariable Long id) {
        DishVO dishVO = dishService.getByIdWithFlavor(id);
        return Result.success(dishVO);
    }

    /**
     * 获取推荐菜品
     * @return
     */
    @GetMapping("/recommend")
    @ApiOperation("获取前四项作为推荐菜品")
    public Result<List<DishVO>> getDish() {
        String key = CachePrefixConstant.RECOMMEND_DISH_KEY;
        //从redis中查询是否有推荐菜品
        List<DishVO> dishVOS = redisTemplate.opsForValue().get(key);
        if (dishVOS != null && !dishVOS.isEmpty()) {
            return Result.success(dishVOS);
        }
        //没有缓存查询数据库
        Dish dish = new Dish();
        List<DishVO> list = dishService.listWithFlavors(dish);
        List<DishVO> fourDish = new ArrayList<>(list.subList(0, 4));
        //存入缓存
        redisTemplate.opsForValue().set(key, fourDish);
        return Result.success(fourDish);
    }
}
