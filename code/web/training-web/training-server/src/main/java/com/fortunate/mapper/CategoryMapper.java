package com.fortunate.mapper;

import com.fortunate.annotation.AutoFill;
import com.fortunate.entity.Category;
import com.fortunate.enumeration.OperationType;
import com.fortunate.query.CategoryPageQueryDTO;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * @description
 */
@Mapper
public interface CategoryMapper {
  
  /**
   * 插入数据
   * @param category 分类实体
   */
  @Insert("insert into category(`type`, `name`, `sort`, `status`, `create_time`, `update_time`, `create_user`, " +
          "`update_user`) VALUES (#{type}, #{name}, #{sort}, #{status}, #{createTime}, #{updateTime}, " +
          "#{createUser}, #{updateUser})")
  @AutoFill(OperationType.INSERT)
  void insert(Category category);
  
  /**
   * 分页查询
   * @param categoryPageQueryDTO 分类分页查询DTO
   * @return 分页数据
   */
  Page<Category> pageQuery(CategoryPageQueryDTO categoryPageQueryDTO);
  
  /**
   * 根据id删除分类
   * @param id 分类id
   */
  @Delete("delete from category where `id` = #{categoryId}")
  void deleteById(@Param("categoryId") Long id);
  
  /**
   * 根据id修改分类
   * @param category 分类实体
   */
  @AutoFill(OperationType.UPDATE)
  void update(Category category);
  
  /**
   * 根据类型查询分类
   * @param type 分类类型
   * @return 分类列表
   */
  List<Category> list(@Param("categoryType") Integer type);
  
  /**
   * 根据分类id查询对应的分类名称
   * @param id 分类id
   * @return 分类名称
   */
  @Select("select `name` categoryName from `category` where `id` = #{categoryId}")
  String getCategoryNameById(@Param("categoryId") Long id);
}
