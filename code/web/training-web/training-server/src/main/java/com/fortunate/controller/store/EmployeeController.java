package com.fortunate.controller.store;

import com.fortunate.constant.Constants;
import com.fortunate.context.BaseContext;
import com.fortunate.dto.EmployeeDTO;
import com.fortunate.dto.EmployeeLoginDTO;
import com.fortunate.entity.Employee;
import com.fortunate.entity.User;
import com.fortunate.query.EmployeePageQueryDTO;
import com.fortunate.result.PageResult;
import com.fortunate.result.Result;
import com.fortunate.service.EmployeeService;
import com.fortunate.service.UserService;
import com.fortunate.vo.EmployeeLoginVO;
import com.fortunate.vo.EmployeeVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

/**
 * @description
 */
@Slf4j
@Api(tags = "员工相关接口")
@RestController
@RequestMapping("/store/employee")
@RequiredArgsConstructor
public class EmployeeController {
  
  private final EmployeeService employeeService;
  private final UserService userService;

  
  @PostMapping(value = "/login", consumes = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation("员工登录")
  public Result<EmployeeLoginVO> login(@RequestBody EmployeeLoginDTO employeeLoginDTO) {
    log.info("员工登录，参数==> {}", employeeLoginDTO);
    
    // 调用service层方法，进行登录验证
    EmployeeLoginVO employeeLoginVO = employeeService.login(employeeLoginDTO);
    // 返回登录成功的结果
    return Result.success(employeeLoginVO);
  }
  
  // 退出登录
  @PostMapping("/logout")
  @ApiOperation("员工退出")
  public Result<String> logout() {
    return Result.success();
  }

  /**
   * 获取当前登录员工信息
   */
  @GetMapping("/me")
  @ApiOperation("获取当前登录员工信息")
  public Result<EmployeeVO> getCurrentEmployee() {
    Long currentId = BaseContext.getUserId();
    log.info("获取当前员工信息，员工ID：{}", currentId);

    Employee employee = employeeService.getEmployeeById(currentId);
    if (employee == null) {
      return Result.error("员工不存在");
    }

    //准备返回的用户数据
    User userInfo = userService.getUserInfoById(Constants.CLIENT_ADMIN_ID);

    EmployeeVO employeeVO = EmployeeVO.builder()
            .id(employee.getId())
            .name(employee.getName())
            .username(employee.getUsername())
            .phone(employee.getPhone())
            .sex(employee.getSex())
            .status(employee.getStatus())
            .avatar(userInfo.getAvatar())
            .createTime(employee.getCreateTime())
            .updateTime(employee.getUpdateTime())
            .createUser(employee.getCreateUser())
            .updateUser(employee.getUpdateUser())
            .build();

    return Result.success(employeeVO);
  }
  
  /**
   * 新增员工
   * @param employeeDTO 员工信息数据传输对象
   */
  @PostMapping
  @ApiOperation("新增员工")
  public Result<?> save(@RequestBody EmployeeDTO employeeDTO) {
    log.info("新增员工，参数==> {}", employeeDTO);
    employeeService.save(employeeDTO);
    return Result.success();
  }
  
  /**
   * 员工分页查询
   * @param employeePageQueryDTO 员工分页查询数据传输对象（员工姓名、页码、每页显示条数）
   * @return 员工分页查询结果
   */
  @GetMapping("/page")
  @ApiOperation("员工分页查询")
  public Result<PageResult> page(EmployeePageQueryDTO employeePageQueryDTO) {
    log.info("员工分页查询，参数==> {}", employeePageQueryDTO);
    PageResult pageResult = employeeService.pageQuery(employeePageQueryDTO);
    return Result.success(pageResult);
  }
  
  /**
   * 启用禁用员工账号
   * @param id 员工id
   * @param status 员工账号状态（0：禁用，1：启用）
   */
  @PostMapping("/status")
  @ApiOperation("启用禁用员工账号")
  public Result<?> startOrStop(@RequestParam Long id, @RequestParam Integer status) {
    log.info("启用禁用员工账号，参数==> employeeId: {}, status: {}", id, status);
    employeeService.startOrStop(id, status);
    return Result.success();
  }
  
  @GetMapping
  @ApiOperation("通过员工id查询员工信息")
  public Result<Employee> getEmployeeById(@RequestParam Long id) {
    log.info("通过员工id查询员工信息，员工id==> {}", id);
    Employee employee = employeeService.getEmployeeById(id);
    return Result.success(employee);
  }
  
  @PutMapping
  @ApiOperation("编辑员工信息")
  public Result<?> update(@RequestBody EmployeeDTO employeeDTO) {
    log.info("编辑员工信息，参数==> {}", employeeDTO);
    employeeService.update(employeeDTO);
    return Result.success();
  }
  
  @DeleteMapping
  @ApiOperation("根据员工id删除员工")
  public Result<?> deleteEmployeeById(@RequestParam Long id) {
    log.info("删除员工，员工id==> {}", id);
    employeeService.deleteEmployeeById(id);
    return Result.success();
  }
}
