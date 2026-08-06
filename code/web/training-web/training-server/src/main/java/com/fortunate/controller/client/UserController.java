package com.fortunate.controller.client;

import com.fortunate.constant.JwtClaimsConstant;
import com.fortunate.dto.UserDTO;
import com.fortunate.dto.UserLoginDTO;
import com.fortunate.entity.User;
import com.fortunate.properties.JwtProperties;
import com.fortunate.result.Result;
import com.fortunate.service.UserService;
import com.fortunate.utils.JwtUtil;
import com.fortunate.vo.UserLoginVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

/**
 * @description
 */
@Slf4j
@Api(tags = "C端用户相关接口")
@RestController
@RequestMapping("/client/user")
@RequiredArgsConstructor
public class UserController {
  
  private final UserService userService;
  private final JwtProperties jwtProperties;
  
  @PostMapping("/login")
  @ApiOperation("微信登录")
  public Result<UserLoginVO> login(@RequestBody UserLoginDTO userLoginDTO) {
    log.info("微信用户登录，微信授权码code==> {}", userLoginDTO.getCode());
    
    // 微信登录
    User user = userService.wxLogin(userLoginDTO);
    
    // 为微信用户生成jwt token
    HashMap<String, Object> claims = new HashMap<>();
    claims.put(JwtClaimsConstant.USER_ID, user.getId());
    String token = JwtUtil.createJWT(jwtProperties.getClientSecretKey(), jwtProperties.getClientTtl(), claims);
    
    // 封装数据返回给前端
    UserLoginVO userLoginVO = UserLoginVO.builder()
            .id(user.getId())
            .openid(user.getOpenid())
            .token(token)
            .build();
    
    return Result.success(userLoginVO);
  }
  
  @GetMapping("/info")
  @ApiOperation("用户微信用户信息")
  public Result<User> getUserInfo() {
    User user = userService.getUserInfo();
    return Result.success(user);
  }
  
  @PutMapping
  @ApiOperation("修改用户信息")
  public Result<?> updateUserInfo(@RequestBody UserDTO userDTO) {
    userService.update(userDTO);
    //更新评论和回复评论对应的用户信息
    userService.updateUserInfoInComment(userDTO);
    return Result.success();
  }
}
