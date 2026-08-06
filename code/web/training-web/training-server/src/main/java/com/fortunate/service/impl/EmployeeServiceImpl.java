package com.fortunate.service.impl;

import com.fortunate.constant.*;
import com.fortunate.context.BaseContext;
import com.fortunate.dto.EmployeeDTO;
import com.fortunate.dto.EmployeeLoginDTO;
import com.fortunate.dto.UserDTO;
import com.fortunate.entity.Employee;
import com.fortunate.entity.User;
import com.fortunate.exception.*;
import com.fortunate.mapper.EmployeeMapper;
import com.fortunate.properties.JwtProperties;
import com.fortunate.query.EmployeePageQueryDTO;
import com.fortunate.result.PageResult;
import com.fortunate.service.EmployeeService;
import com.fortunate.service.UserService;
import com.fortunate.utils.JwtUtil;
import com.fortunate.vo.EmployeeLoginVO;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import static com.fortunate.constant.Constants.ADMIN_ID;
import static com.fortunate.constant.MessageConstant.*;

/**
 * @description 员工service实现类
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeMapper employeeMapper;
    private final JwtProperties jwtProperties;
    private final UserService userService;

    @Override
    public EmployeeLoginVO login(EmployeeLoginDTO employeeLoginDTO) {
        // 获取前端登录表单传递过来的参数
        String username = employeeLoginDTO.getUsername();
        String password = employeeLoginDTO.getPassword();

        // 1、根据用户名查询是否存在该用户
        Employee employee = employeeMapper.getByUsername(username);
        // 2、处理各种异常情况（用户名不存在、密码不对、账号被锁定）
        if (employee == null) {
            throw new AccountNotFoundException(MessageConstant.ACCOUNT_NOT_FOUND);
        }

        // 密码对比，将前端传递过来的明文密码进行md5加密之后再和数据库中的密码进行对比
        password = DigestUtils.md5DigestAsHex(password.getBytes());
        if (!password.equals(employee.getPassword())) {
            throw new PasswordErrorException(MessageConstant.PASSWORD_ERROR);
        }

        // 账号是否被锁定
        if (Objects.equals(employee.getStatus(), StatusConstant.DISABLE)) {
            throw new AccountLockedException(MessageConstant.ACCOUNT_LOCKED);
        }

        // 3、验证都通过，返回实体对象
        // 生成token
        HashMap<String, Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.EMP_ID, employee.getId());
        String token = JwtUtil.createJWT(jwtProperties.getStoreSecretKey(), jwtProperties.getStoreTtl(), claims);

        // 将数据封装到专门的VO对象中，供前端使用展示
        EmployeeLoginVO employeeLoginVO = EmployeeLoginVO.builder()
                .id(employee.getId())
                .userName(employee.getUsername())
                .name(employee.getName())
                .token(token)
                .build();
        log.info("员工登录成功，返回结果==> {}", employeeLoginVO);

        return employeeLoginVO;
    }

    /**
     * 新增员工
     *
     * @param employeeDTO 员工信息（由前端传递过来）
     */
    @Override
    public void save(EmployeeDTO employeeDTO) {
        Employee employee = new Employee();
        BeanUtils.copyProperties(employeeDTO, employee);
        employee.setStatus(StatusConstant.ENABLE);
        employee.setPassword(DigestUtils.md5DigestAsHex(PasswordConstant.DEFAULT_PASSWORD.getBytes()));
        employee.setCreateUser(BaseContext.getUserId());
        employee.setUpdateUser(BaseContext.getUserId());
        employeeMapper.insert(employee);
    }

    /**
     * 员工分页查询
     *
     * @param employeePageQueryDTO 分页查询条件（员工姓名、页码、每页显示条数）
     * @return
     */
    @Override
    public PageResult pageQuery(EmployeePageQueryDTO employeePageQueryDTO) {
        int page = employeePageQueryDTO.getPage();
        int pageSize = employeePageQueryDTO.getPageSize();
        PageHelper.startPage(page, pageSize);

        Page<Employee> pageInfo = employeeMapper.pageQuery(employeePageQueryDTO);
        long total = pageInfo.getTotal();
        List<Employee> records = pageInfo.getResult();
        return new PageResult(total, records);
    }

    /**
     * 启用或禁用员工账号
     *
     * @param id     员工id
     * @param status 状态（0：禁用，1：启用）
     */
    @Override
    public void startOrStop(Long id, Integer status) {
        Employee isAdmin = employeeMapper.getEmployeeById(id);
        //不能禁用管理员账号
        if (ADMIN_ID.equals(isAdmin.getId())) {
            throw new DeletionNotAllowedException(CANNOT_STOP_ADMIN);
        }
        Long userId = BaseContext.getUserId();
        //非管理员不具备禁用权限
        if (!ADMIN_ID.equals(userId)) {
            throw new DeletionNotAllowedException(NO_STOP_PERMISSION);
        }
        Employee employee = Employee.builder()
                .id(id)
                .status(status)
                .build();
        employeeMapper.update(employee);
    }

    /**
     * 根据id查询员工信息
     *
     * @param id 员工id
     * @return
     */
    @Override
    public Employee getEmployeeById(Long id) {
        Employee employee = employeeMapper.getEmployeeById(id);
        // 密码设置为******
        employee.setPassword("******");
        return employee;
    }

    /**
     * 编辑员工信息
     *
     * @param employeeDTO 员工信息（由前端传递过来）
     */
    @Override
    public void update(EmployeeDTO employeeDTO) {
        Employee employee = new Employee();
        BeanUtils.copyProperties(employeeDTO, employee);
        Long userId = BaseContext.getUserId();
        //商家头像只能由管理员进行更新，不能被员工自己更新
        String avatar = employeeDTO.getAvatar();
        //非管理员不能更新员工头像
        if (StringUtils.isNotBlank(avatar) && !ADMIN_ID.equals(userId)) {
            throw new BaseException(NO_UPDATE_PERMISSION);
        }

        //头像更新
        if (StringUtils.isNotBlank(avatar)) {
            UserDTO userDTO = new UserDTO();
            userDTO.setAvatar(avatar);
            userDTO.setName(employeeDTO.getName());
            userDTO.setId(Constants.CLIENT_ADMIN_ID);
            userService.updateUserInfoInComment(userDTO);
            userService.update(userDTO);
        }

        employeeMapper.update(employee);
    }

    /**
     * 根据id删除员工
     *
     * @param id 员工id
     */
    @Override
    public void deleteEmployeeById(Long id) {
        Employee employee = employeeMapper.getEmployeeById(id);
        //管理员不能被删除
        if (ADMIN_ID.equals(employee.getId())) {
            throw new DeletionNotAllowedException(CANNOT_DELETE_ADMIN);
        }
        Long userId = BaseContext.getUserId();
        //非管理员不具备删除权限
        if (!ADMIN_ID.equals(userId)) {
            throw new DeletionNotAllowedException(NO_DELETE_PERMISSION);
        }
        employeeMapper.deleteEmployeeById(id);
    }
}
