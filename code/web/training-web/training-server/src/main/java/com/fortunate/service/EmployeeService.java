package com.fortunate.service;

import com.fortunate.dto.EmployeeDTO;
import com.fortunate.dto.EmployeeLoginDTO;
import com.fortunate.entity.Employee;
import com.fortunate.query.EmployeePageQueryDTO;
import com.fortunate.result.PageResult;
import com.fortunate.vo.EmployeeLoginVO;

/**
 * @description 员工服务接口
 */
public interface EmployeeService {
  /**
   * 员工登录
   * @param employeeLoginDTO 登录信息（由前端传递过来）
   * @return 登录成功的员工实体EmployeeLoginDTO（entity与数据库中的字段一一对应）
   */
  EmployeeLoginVO login(EmployeeLoginDTO employeeLoginDTO);
  
  /**
   * 新增员工
   * @param employeeDTO 员工信息（由前端传递过来）
   */
  void save(EmployeeDTO employeeDTO);
  
  /**
   * 分页查询员工信息
   * @param employeePageQueryDTO 分页查询条件（员工姓名、页码、每页显示条数）
   * @return 分页查询结果
   */
  PageResult pageQuery(EmployeePageQueryDTO employeePageQueryDTO);
  
  /**
   * 启用禁用员工账号
   * @param id 员工id
   * @param status 状态（0：禁用，1：启用）
   */
  void startOrStop(Long id, Integer status);
  
  /**
   * 根据员工id获取员工信息
   * @param id 员工id
   * @return 员工信息
   */
  Employee getEmployeeById(Long id);
  
  /**
   * 编辑员工信息
   * @param employeeDTO 员工信息（由前端传递过来）
   */
  void update(EmployeeDTO employeeDTO);
  
  /**
   * 根据员工id删除员工信息
   * @param id 员工id
   */
  void deleteEmployeeById(Long id);
}
