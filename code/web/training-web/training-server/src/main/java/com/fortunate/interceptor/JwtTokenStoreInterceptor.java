package com.fortunate.interceptor;

import com.alibaba.fastjson.JSON;
import com.fortunate.constant.JwtClaimsConstant;
import com.fortunate.constant.MessageConstant;
import com.fortunate.context.BaseContext;
import com.fortunate.properties.JwtProperties;
import com.fortunate.result.Result;
import com.fortunate.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.PrintWriter;

/**
 * @description
 */
@Slf4j
@Component
public class JwtTokenStoreInterceptor implements HandlerInterceptor {
  @Resource
  private JwtProperties jwtProperties;
  
  /**
   * 校验当前是否存在token，前置拦截器（控制器方法之前执行）
   *
   * @param request 请求对象
   * @param response 响应对象
   * @param handler 处理器对象
   * @return true：放行，false：拦截
   * @throws Exception 异常
   */
  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
    // 当匹配控制层的方法时会将该方法包装成`HandlerMethod`对象
    if (!(handler instanceof HandlerMethod)) return true; // 非控制器方法直接放行
    
    // 获取前端传递过来的请求头token
    String token = request.getHeader(jwtProperties.getStoreTokenName());
    // 解析token
    try {
      Claims claims = JwtUtil.parseJWT(jwtProperties.getStoreSecretKey(), token);
      // 获取保存在token中的员工id
      Long employeeId = Long.valueOf(claims.get(JwtClaimsConstant.EMP_ID)
                                             .toString());
      log.info("校验前端传过来的【token】，token中携带的employeeId==> {}", employeeId);
      // 每一次的请求都是一个单独的线程，数据不受影响，将前端携带过来的token中的员工id获取出来并保存到线程中，方便全局获取
      BaseContext.setUserId(employeeId);
      return true; // 校验通过
    } catch (Exception e) {
      Result<?> result = new Result<>();
      // token中没有有效信息，直接返回401
      response.setStatus(HttpStatus.SC_UNAUTHORIZED);
      result.setCode(HttpStatus.SC_UNAUTHORIZED);
      result.setMsg(MessageConstant.UNAUTHORIZED);
      response.setContentType("application/json;charset=UTF-8");
      PrintWriter printWriter = response.getWriter();
      // 响应体中返回错误信息
      printWriter.write(JSON.toJSONString(result));
      printWriter.flush();
      printWriter.close();
      return false;
    }
  }
}
