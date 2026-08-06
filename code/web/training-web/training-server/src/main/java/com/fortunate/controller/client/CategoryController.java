package com.fortunate.controller.client;

import com.fortunate.entity.Category;
import com.fortunate.result.Result;
import com.fortunate.service.CategoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @description
 */
@Slf4j
@Api(tags = "C端-分类接口")
@RestController("clientCategoryController")
@RequestMapping("/client/category")
@RequiredArgsConstructor
public class CategoryController {
  
  private final CategoryService categoryService;
  
  /**
   * 查询分类
   * @param type 分类类型，1-菜品
   * @return 分类列表
   */
  @GetMapping("/list")
  @ApiOperation("查询分类")
  public Result<List<Category>> list(Integer type) {
    List<Category> categoryList = categoryService.list(type);
    return Result.success(categoryList);
  }
}
