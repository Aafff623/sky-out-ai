package com.fortunate.annotation;

import com.fortunate.enumeration.OperationType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @description 填充公共字段注解
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AutoFill {
  
  /** 数据库操作类型：INSERT、UPDATE */
  OperationType value() default OperationType.INSERT;
}
