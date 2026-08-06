package com.fortunate.aspect;

import com.fortunate.annotation.AutoFill;
import com.fortunate.constant.AutoFillConstant;
import com.fortunate.context.BaseContext;
import com.fortunate.enumeration.OperationType;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.time.LocalDateTime;

/**
 * @description 自动填充切面类
 */
@Slf4j
@Aspect
@Component
public class AutoFillAspect {
  
  /**
   * 公共切入点
   * @description 切入点匹配`com.sky.mapper`包下的所有类中带有`@AutoFill`注解的方法
   */
  @Pointcut("execution(* com.fortunate.mapper.*.*(..)) && @annotation(com.fortunate.annotation.AutoFill)")
  public void autoFillPointcut() {}
  
  @Before("autoFillPointcut()")
  public void autoFill(JoinPoint joinPoint) {
    log.info("自动填充公共字段开始......");
    
    // 获取到当前被拦截的方法上`@AutoFill`注解中的value值（数据库操作类型：INSERT、UPDATE）
    MethodSignature signature = (MethodSignature) joinPoint.getSignature(); // 方法签名对象
    Method method = signature.getMethod(); // 方法对象
    AutoFill autoFill = method.getAnnotation(AutoFill.class); // 获取方法上面的`@AutoFill`注解对象
    OperationType operationType = autoFill.value(); // 获取注解中的value值
    
    // 获取当前被拦截的方法参数
    Object[] args = joinPoint.getArgs();
    // 我们规定使用`@AutoFill`注解第一个参数必须是实体对象
    if (args == null || args.length == 0) {
      throw new RuntimeException("@AutoFill注解的第一个参数必须是实体对象！");
    }
    
    // 获取到实体对象
    Object entity = args[0];
    
    // 准备好需要填充的值
    LocalDateTime now = LocalDateTime.now();
    Long currentId = BaseContext.getUserId();
    
    // 判断当前数据操作类型：INSERT、UPDATE
    Method setUpdateTime = null;
    Method setCreateTime = null;
    if (operationType == OperationType.INSERT) {
      try {
        /* 操作类型为`INSERT`时，需要填充的公共字段为`setCreateUser、setCreateTime、setUpdateUser、setUpdateTime` */
        // 通过反射获取需要填充的公共字段set方法（通过方法名和参数列表类型获取对应的set方法）
         setCreateTime = entity.getClass()
                .getDeclaredMethod(AutoFillConstant.SET_CREATE_TIME, LocalDateTime.class);
         setUpdateTime = entity.getClass()
                .getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);
        Method setCreateUser = entity.getClass()
                .getDeclaredMethod(AutoFillConstant.SET_CREATE_USER, Long.class);
        Method setUpdateUser = entity.getClass()
                .getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER, Long.class);
        
        // 调用并执行set方法设置值（目标实体，填充值）
        // 只有方法存在时才调用
        if (setUpdateTime != null) {
          setUpdateTime.invoke(entity, now);
        }
        if (setCreateTime != null) {
          setCreateTime.invoke(entity, now);
        }
        setCreateUser.invoke(entity, currentId);
        setUpdateUser.invoke(entity, currentId);
        // 调用set方法设置值
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    } else if (operationType == OperationType.UPDATE) {
      try {
        /* 操作类型为`UPDATE`时，需要填充的公共字段为`setUpdateTime、setUpdateUser` */
         setUpdateTime = entity.getClass()
                .getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);
        Method setUpdateUser = entity.getClass()
                .getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER, Long.class);
        
        // 通过反射为对象属性赋值
        if (setUpdateTime != null) {
          setUpdateTime.invoke(entity, now);
        }
        setUpdateUser.invoke(entity, currentId);
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }
}
