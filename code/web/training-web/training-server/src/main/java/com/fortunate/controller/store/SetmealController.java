package com.fortunate.controller.store;


import com.fortunate.constant.CachePrefixConstant;
import com.fortunate.dto.SetmealDTO;
import com.fortunate.query.SetmealPageQueryDTO;
import com.fortunate.result.PageResult;
import com.fortunate.result.Result;
import com.fortunate.service.SetmealService;
import com.fortunate.utils.CacheDoubleDeleteUtil;
import com.fortunate.vo.SetmealVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @description 套餐管理Controller
 */
@Slf4j
@Api(tags = "套餐相关接口")
@RestController
@RequestMapping("/store/setmeal")
@RequiredArgsConstructor
public class SetmealController {
  
  private final SetmealService setmealService;
  private final CacheDoubleDeleteUtil clearCacheUtils;
  private final String DISH_CACHE = CachePrefixConstant.DISH_KEY + "*";
  /**
   * 新增套餐
   * @param setmealDTO 套餐DTO
   */
  @PostMapping
  @ApiOperation("新增套餐")
  // 通过动态获取`setmealDTO.categoryId`中的分类id精确清除缓存
  @CacheEvict(cacheNames = CachePrefixConstant.SETMEAL_KEY, key = "#setmealDTO.categoryId")
  public Result<?> save(@RequestBody SetmealDTO setmealDTO) {
    // 保存套餐及这个套餐下的关联的菜品
    setmealService.saveWithDish(setmealDTO);
    clearCacheUtils.doubleDelete(DISH_CACHE, 3000);
    clearCacheUtils.doubleDelete(CachePrefixConstant.RECOMMEND_DISH_KEY, 3000);
    return Result.success();
  }
  
  /**
   * 分页查询套餐
   * @param setmealPageQueryDTO 套餐分页查询DTO
   * @return 分页结果
   */
  @GetMapping("/page")
  @ApiOperation("分页查询套餐")
  public Result<PageResult> page(SetmealPageQueryDTO setmealPageQueryDTO) {
    PageResult pageResult = setmealService.pageQuery(setmealPageQueryDTO);
    return Result.success(pageResult);
  }
  
  /**
   * 批量删除套餐
   * @param ids 套餐ID，多个ID用逗号分隔
   */
  @DeleteMapping
  @ApiOperation("批量删除套餐")
  @CacheEvict(cacheNames = CachePrefixConstant.SETMEAL_KEY, allEntries = true) // 清除所有缓存
  public Result<?> delete(@RequestParam List<Long> ids) {
    setmealService.deleteBatch(ids);
    clearCacheUtils.doubleDelete(DISH_CACHE, 3000);
    clearCacheUtils.doubleDelete(CachePrefixConstant.RECOMMEND_DISH_KEY, 3000);
    return Result.success();
  }
  
  /**
   * 根据id查询套餐，用于修改页面回显数据
   * @param id
   * @return
   */
  @GetMapping
  @ApiOperation("根据id查询套餐")
  public Result<SetmealVO> getById(@RequestParam Long id) {
    // 根据套餐id查询套餐以及这个套餐包含的菜品
    SetmealVO setmealVO = setmealService.getSetmealByIdWithDish(id);
    return Result.success(setmealVO);
  }
  
  /**
   * 更新套餐
   * @param setmealDTO 套餐数据传输对象
   */
  @PutMapping
  @ApiOperation("更新套餐")
  @CacheEvict(cacheNames = CachePrefixConstant.SETMEAL_KEY, allEntries = true) // 清除所有缓存
  public Result<?> update(@RequestBody SetmealDTO setmealDTO) {
    setmealService.update(setmealDTO);
    clearCacheUtils.doubleDelete(DISH_CACHE, 3000);
    clearCacheUtils.doubleDelete(CachePrefixConstant.RECOMMEND_DISH_KEY, 3000);
    return Result.success();
  }
  
  /**
   * 启动或停止套餐
   * @param id 套餐ID
   * @param status 状态，1：起售，0：停售
   */
  @PostMapping("/status/{status}")
  @ApiOperation("套餐起售停售")
  @CacheEvict(cacheNames = CachePrefixConstant.SETMEAL_KEY, allEntries = true) // 清除所有缓存
  public Result<?> startOrStop(@PathVariable("status") Integer status, Long id) {
    setmealService.startOrStop(id, status);
    return Result.success();
  }
}
