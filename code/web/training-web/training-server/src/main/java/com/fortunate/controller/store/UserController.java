package com.fortunate.controller.store;

import com.fortunate.query.UserPageQueryDTO;
import com.fortunate.result.PageResult;
import com.fortunate.result.Result;
import com.fortunate.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @description
 */
@Slf4j
@Api(tags = "用户管理相关接口")
@RestController("storeUserController")
@RequestMapping("/store/user")
@RequiredArgsConstructor
public class UserController {

  private final UserService userService;

  /**
   * 用户分页查询
   * @param userPageQueryDTO 用户分页查询参数
   * @return 分页结果
   */
  @GetMapping("/page")
  @ApiOperation("用户分页查询")
  public Result<PageResult> page(UserPageQueryDTO userPageQueryDTO) {
    log.info("用户分页查询，参数==> {}", userPageQueryDTO);
    return Result.success(userService.pageQuery(userPageQueryDTO));
  }
}
