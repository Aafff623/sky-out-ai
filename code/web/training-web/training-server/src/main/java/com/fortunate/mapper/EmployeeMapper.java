package com.fortunate.mapper;

import com.fortunate.annotation.AutoFill;
import com.fortunate.entity.Employee;
import com.fortunate.enumeration.OperationType;
import com.fortunate.query.EmployeePageQueryDTO;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.*;

/**
 * @description
 */
@Mapper
public interface EmployeeMapper {
  
  /**
   * 根据用户名查询员工信息
   *
   * @param username 用户名
   * @return 员工信息
   * @description 对于简单的sql查询，直接使用注解就可以了
   */
  @Select("select * from employee where username = #{username}")
  Employee getByUsername(@Param("username") String username);
  
  /**
   * 新增员工
   *
   * @param employee 员工信息
   */
  @Insert("insert into employee (`name`, `username`, `password`, `phone`, `sex`, `create_time`, " +
          "`update_time`, `create_user`, `update_user`, `status`) values (#{name},#{username},#{password}," +
          "#{phone},#{sex},#{createTime},#{updateTime},#{createUser},#{updateUser},#{status})")
  @AutoFill(OperationType.INSERT)
  void insert(Employee employee);
  
  /**
   * 分页查询员工信息
   * @param employeePageQueryDTO 员工分页查询条件
   * @description 使用pagehelper分页插件进行分页查询，动态帮我们拼接limit语句
   * @return 分页查询结果
   */
  Page<Employee> pageQuery(EmployeePageQueryDTO employeePageQueryDTO);
  
  /**
   * 更新员工信息
   * @param employee 员工信息实体
   */
  @AutoFill(OperationType.UPDATE)
  void update(Employee employee);
  
  /**
   * 根据id查询员工信息
   * @param id 员工id
   * @return 员工信息实体
   */
  @Select("select * from `employee` where `id` = #{employeeId}")
  Employee getEmployeeById(@Param("employeeId") Long id);
  
  /**
   * 根据id删除员工信息
   * @param id 员工id
   */
  @Delete("delete from `employee` where `id` = #{employeeId}")
  void deleteEmployeeById(@Param("employeeId") Long id);
}
