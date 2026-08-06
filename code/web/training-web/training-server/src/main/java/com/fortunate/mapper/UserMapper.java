package com.fortunate.mapper;

import com.fortunate.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * @description
 */
@Mapper
public interface UserMapper {
  
  /**
   * 根据openId获取用户信息
   * @param id 微信小程序openId
   * @return 用户信息
   */
  @Select("select * from `user` where `openid` = #{openId}")
  User getUserByOpenId(@Param("openId") String id);
  
  /**
   * 新增微信用户
   * @param user 用户信息
   */
  void insert(User user);
  
  /**
   * 根据用户id获取用户信息
   * @param userId 用户id
   */
  @Select("select * from `user` where `id` = #{userId}")
  User getUserById(Long userId);
  
  /**
   * 更新用户信息
   * @param user 用户信息
   */
  void update(User user);
  
  /**
   * 根据条件查询用户数量
   * @param map
   * @return
   */
  Integer countByMap(Map<String, Object> map);
  
  /**
   * 根据日期条件查询指定范围内用户列表
   * @param map 条件
   * @return 用户列表
   */
  List<User> findUsersByMap(Map<String, Object> map);
}
