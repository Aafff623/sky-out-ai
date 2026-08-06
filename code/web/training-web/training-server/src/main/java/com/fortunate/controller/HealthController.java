package com.fortunate.controller;

import com.fortunate.result.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 根路径探活：浏览器直接打开 :8848 时不再报 No endpoint。
 * 商家管理界面请用 Vite 端口 9527，不要把本接口当后台页面。
 */
@RestController
public class HealthController {

    @GetMapping("/")
    public Result<Map<String, Object>> root() {
        Map<String, Object> data = new HashMap<>();
        data.put("service", "training-server");
        data.put("hint", "API only. Open admin at http://127.0.0.1:9527/");
        return Result.success(data);
    }
}
