package com.fortunate.controller.client;

import com.fortunate.constant.CachePrefixConstant;
import com.fortunate.constant.StatusConstant;
import com.fortunate.entity.Setmeal;
import com.fortunate.result.Result;
import com.fortunate.service.SetmealService;
import com.fortunate.vo.DishItemVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Comparator;
import java.util.List;

/**
 * @description C端-套餐浏览接口
 */
@Slf4j
@Api(tags = "C端-套餐浏览接口")
@RequiredArgsConstructor
@RestController("clientSetmealController")
@RequestMapping("/client/setmeal")
public class SetmealController {

    private final SetmealService setmealService;

    /**
     * 根据分类id查询套餐
     *
     * @param categoryId 分类id
     */
    @GetMapping("/list")
    @ApiOperation("根据分类id查询套餐")
    // 每次查询套餐都进行缓存，如果缓存中存在，则直接返回缓存，否则查询数据库，并将返回结果缓存到redis中
    @Cacheable(cacheNames = CachePrefixConstant.SETMEAL_KEY, key = "#categoryId+" + "_" + "+tabIndex")
    public Result<List<Setmeal>> list(@RequestParam(value = "categoryId", required = false) Long categoryId, @RequestParam("tabIndex") Integer tabIndex) {
        Setmeal setmeal = new Setmeal();
        setmeal.setCategoryId(categoryId);
        setmeal.setStatus(StatusConstant.ENABLE);
        List<Setmeal> list = setmealService.list(setmeal);

        if (tabIndex == 1) {
            //按照热度进行排序
            list.sort(Comparator.comparing(Setmeal::getHotSpot, Comparator.nullsLast(Comparator.reverseOrder())));
        }

        return Result.success(list);
    }

    /**
     * 根据套餐id查询包含的菜品列表
     *
     * @param id 套餐id
     * @return 包含的菜品列表
     */
    @GetMapping("/dish")
    @ApiOperation("根据套餐id查询包含的菜品列表")
    public Result<List<DishItemVO>> dishList(Long id) {
        List<DishItemVO> list = setmealService.getDishItemBySetmealId(id);
        return Result.success(list);
    }
}
