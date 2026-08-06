package com.fortunate.service.impl;

import com.fortunate.constant.MessageConstant;
import com.fortunate.constant.StatusConstant;
import com.fortunate.dto.CategoryDTO;
import com.fortunate.entity.Category;
import com.fortunate.exception.DeletionNotAllowedException;
import com.fortunate.mapper.CategoryMapper;
import com.fortunate.mapper.DishMapper;
import com.fortunate.query.CategoryPageQueryDTO;
import com.fortunate.result.PageResult;
import com.fortunate.service.CategoryService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @description
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
  
  private final CategoryMapper categoryMapper;
  private final DishMapper dishMapper;

  /**
   * 新增分类
   * @param categoryDTO 分类传输对象
   */
  @Override
  public void save(CategoryDTO categoryDTO) {
    Category category = new Category();
    BeanUtils.copyProperties(categoryDTO, category);
    category.setStatus(StatusConstant.DISABLE);
    categoryMapper.insert(category);
  }

  /**
   * 分类分页查询
   * @param categoryPageQueryDTO 分类分页查询传输对象
   * @return
   */
  @Override
  public PageResult pageQuery(CategoryPageQueryDTO categoryPageQueryDTO) {
    int page = categoryPageQueryDTO.getPage();
    int pageSize = categoryPageQueryDTO.getPageSize();
    
    PageHelper.startPage(page, pageSize);
    Page<Category> categories = categoryMapper.pageQuery(categoryPageQueryDTO);
    
    return new PageResult(categories.getTotal(), categories.getResult());
  }

  /**
   * 删除分类
   * @param id 分类id
   */
  @Override
  public void deleteById(Long id) {
    // 查询当前分类是否关联了菜品，如果关联了就抛出业务异常
    Integer count = dishMapper.countByCategoryId(id);
    if (count > 0) {
      // 当前分类下有菜品，不能删除
      throw new DeletionNotAllowedException(MessageConstant.CATEGORY_BE_RELATED_BY_DISH);
    }
    
    // 删除分类数据
    categoryMapper.deleteById(id);
  }

  /**
   * 修改分类
   * @param categoryDTO 分类传输对象
   */
  @Override
  public void update(CategoryDTO categoryDTO) {
    Category category = new Category();
    BeanUtils.copyProperties(categoryDTO, category);
    categoryMapper.update(category);
  }

  /**
   * 启用或禁用分类
   * @param status 分类状态，1：启用，0：禁用
   * @param id 分类id
   */
  @Override
  public void startOrStop(Integer status, Long id) {
    Category category = Category.builder()
            .id(id)
            .status(status)
            .build();
    categoryMapper.update(category);
  }

  @Override
  public List<Category> list(Integer type) {
    return categoryMapper.list(type);
  }
}
