package com.fortunate.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;

/**
 * @description
 */
public class JwtUtil {
  
  /**
   * 创建JWT（Token），使用Hs256算法, 私匙使用固定秘钥
   *
   * @param secretKey 服务器端的秘钥key
   * @param ttlMillis token的有效期，单位：毫秒
   * @param claims 自定义的一些信息，一般是用户信息
   * @return JWT（Token）
   */
  public static String createJWT(String secretKey, long ttlMillis, Map<String, Object> claims) {
    // 使用的签名算法
    SignatureAlgorithm hs256 = SignatureAlgorithm.HS256;
    // 过期时间，当前时间 + ttlMillis = 过期时间
    long expMillis = System.currentTimeMillis() + ttlMillis;
    Date expiration = new Date(expMillis);
    
    return Jwts.builder()
            // 设置头部信息
            // .setHeaderParam("typ", "JWT")
            // 设置载荷信息（也就是查出来的用户信息）
            .setClaims(claims)
            // 设置签名算法和秘钥（指定编码方式为UTF-8）
            .signWith(hs256, secretKey.getBytes(StandardCharsets.UTF_8))
            // 设置过期时间
            .setExpiration(expiration)
            .compact();
  }
  
  /**
   * 解析JWT（Token）
   *
   * @param secretKey 服务器端的秘钥key
   * @param token JWT（Token）
   * @return Claims对象，包含了载荷信息（用户信息，后台管理就是保存了员工的employeeId）
   */
  public static Claims parseJWT(String secretKey, String token) {
    // 通过JWT解析器解析JWT（Token）
    JwtParser jwtParser = Jwts.parser();
    return jwtParser.setSigningKey(secretKey.getBytes(StandardCharsets.UTF_8))
            .parseClaimsJws(token)
            .getBody();
  }
}
