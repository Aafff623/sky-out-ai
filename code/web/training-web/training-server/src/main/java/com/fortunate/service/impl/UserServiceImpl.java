package com.fortunate.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fortunate.constant.Constants;
import com.fortunate.constant.MessageConstant;
import com.fortunate.context.BaseContext;
import com.fortunate.dto.UserDTO;
import com.fortunate.dto.UserLoginDTO;
import com.fortunate.entity.Comment;
import com.fortunate.entity.CommentRepay;
import com.fortunate.entity.User;
import com.fortunate.exception.LoginFailedException;
import com.fortunate.mapper.UserMapper;
import com.fortunate.properties.WeChatProperties;
import com.fortunate.query.UserPageQueryDTO;
import com.fortunate.result.PageResult;
import com.fortunate.service.UserService;
import com.fortunate.utils.HttpClientUtil;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.BeanUtils;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * @description
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final WeChatProperties weChatProperties;
    private final MongoTemplate mongoTemplate;

    // 微信服务接口地址
    public static final String WX_LOGIN = "https://api.weixin.qq.com/sns/jscode2session";
    private static final String WX_USER_AVATAR = "https://thirdwx.qlogo.cn/mmopen/vi_32" +
            "/POgEwh4mIHO4nibH0KlMECNjjGxQUq24ZEaGT4poC6icRiccVGKSyXwibcPq4BWmiaIGuG1icwxaQX6grC9VemZoJ8rg/132";

    /**
     * 微信登录
     * @param userLoginDTO 用户登录DTO
     * @return
     */
    @Override
    public User wxLogin(UserLoginDTO userLoginDTO) {
        // 首先通过微信授权码获取openid用户唯一标识
        JSONObject jsonObject = this.getOpenId(userLoginDTO.getCode());
        String openId = jsonObject.getString("openid");
        String errMsg = jsonObject.getString("errmsg");
        String errCode = jsonObject.getString("errcode");

        // 判断获取到的openid是否为空，为空证明传递过来的code或者参数错误
        if (openId == null) {
            throw new LoginFailedException(Integer.parseInt(errCode), errMsg != null ? errMsg : MessageConstant.LOGIN_FAILED);
        }

        // openid正确，判断该用户是否为新用户
        User user = userMapper.getUserByOpenId(openId);

        if (user == null) {
            user = User.builder()
                    .openid(openId)
                    .name("微信用户" + UUID.randomUUID()
                            .toString()
                            .substring(0, 5))
                    // 默认微信用户灰白头像，详见：https://developers.weixin.qq.com/community/develop/doc/00022c683e8a80b29bed2142b56c01
                    .avatar(WX_USER_AVATAR)
                    .createTime(LocalDateTime.now())
                    .build();
            // 新用户，插入数据库
            userMapper.insert(user);
        }
        return user;
    }

    @Override
    public User getUserInfo() {
        Long userId = BaseContext.getUserId();
        if (userId == null) {
            throw new LoginFailedException(MessageConstant.GET_USER_INFO_FAILED);
        }
        return userMapper.getUserById(userId);
    }

    /**
     * 更新用户信息
     * @param userDTO 用户DTO
     */
    @Override
    public void update(UserDTO userDTO) {
        User user = new User();
        BeanUtils.copyProperties(userDTO, user);
        userMapper.update(user);
    }

    /**
     * 更新评论中的用户信息
     *
     * @param userDTO
     */
    @Override
    public void updateUserInfoInComment(UserDTO userDTO) {
        Query query = Query.query(Criteria.where("authorId").is(userDTO.getId()));
        //获取到对应的评论信息
        List<Comment> comments = mongoTemplate.find(query, Comment.class);
        comments.forEach(comment -> {
            comment.setAuthorName(userDTO.getName());
            comment.setImage(userDTO.getAvatar());
            //更新评论信息
            mongoTemplate.save(comment);
        });
        //获取到对应的回复信息
        List<CommentRepay> commentRepays = mongoTemplate.find(query, CommentRepay.class);
        commentRepays.forEach(commentRepay -> {
            commentRepay.setAuthorName(userDTO.getName());
            commentRepay.setAuthorAvatar(userDTO.getAvatar());
            //更新回复信息
            mongoTemplate.save(commentRepay);
        });
    }

    /**
     * @return
     */
    @Override
    public User getUserInfoById(Long userId) {
        if (userId == null){
            throw new LoginFailedException(MessageConstant.GET_USER_INFO_FAILED);
        }
        return userMapper.getUserById(userId);
    }

    /**
     * 用户分页查询
     *
     * @param userPageQueryDTO 用户分页查询参数
     * @return 分页结果
     */
    @Override
    public PageResult pageQuery(UserPageQueryDTO userPageQueryDTO) {
        PageHelper.startPage(userPageQueryDTO.getPage(), userPageQueryDTO.getPageSize());
        Page<User> users = userMapper.pageQuery(userPageQueryDTO);
        return new PageResult(users.getTotal(), users.getResult());
    }

    /**
     * 通过微信授权码获取openid用户唯一标识
     *
     * @param code 微信授权码
     * @return openid用户唯一标识
     */
    private JSONObject getOpenId(String code) {
        HashMap<String, String> map = new HashMap<>();
        map.put("appid", weChatProperties.getAppid());
        map.put("secret", weChatProperties.getSecret());
        map.put("js_code", code);
        map.put("grant_type", "authorization_code");
        // 发送请求获取openid
        String json = HttpClientUtil.doGet(WX_LOGIN, map);

        // 返回HttpClient请求结果
        return JSON.parseObject(json);
    }
}
