package com.fortunate.service;

import com.fortunate.dto.UserDTO;
import com.fortunate.dto.UserLoginDTO;
import com.fortunate.entity.User;
import com.fortunate.query.UserPageQueryDTO;
import com.fortunate.result.PageResult;

/**
 * @description
 */
public interface UserService {

    /**
     * 微信小程序用户登录
     *
     * @param userLoginDTO 用户登录DTO
     * @return 用户实体
     */
    User wxLogin(UserLoginDTO userLoginDTO);

    /**
     * 获取用户信息
     */
    User getUserInfo();

    /**
     * 更新用户信息
     *
     * @param userDTO 用户DTO
     */
    void update(UserDTO userDTO);

    /**
     * 更新评论中的用户信息
     * @param userDTO
     */
    void updateUserInfoInComment(UserDTO userDTO);

    User getUserInfoById(Long userId);

    /**
     * 用户分页查询
     *
     * @param userPageQueryDTO 用户分页查询参数
     * @return 分页结果
     */
    PageResult pageQuery(UserPageQueryDTO userPageQueryDTO);

}
