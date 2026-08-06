package com.fortunate.controller.store;

import com.fortunate.query.SensitivePageQueryDTO;
import com.fortunate.result.PageResult;
import com.fortunate.result.Result;
import com.fortunate.service.SensitiveService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/sensitive")
@Slf4j
@RequiredArgsConstructor
@Api(tags = "敏感词接口")
public class SensitiveController {
    private final SensitiveService sensitiveService;

    /**
     * 新增敏感词
     */
    @PostMapping
    @ApiOperation(value = "新增敏感词")
    public Result addSensitive(@RequestParam String sensitive) {
        log.info("新增敏感词==> {}", sensitive);
        return sensitiveService.addSensitive(sensitive);
    }

    /**
     * 修改敏感词
     */
    @PutMapping("/{id}")
    @ApiOperation(value = "修改敏感词")
    public Result updateSensitive(@PathVariable Integer id, @RequestParam String sensitive) {
        log.info("修改敏感词==> id:{},修改词语：{}", id, sensitive);
        return sensitiveService.updateSensitive(id, sensitive);
    }

    /**
     * 分页查询敏感词
     */
    @GetMapping
    @ApiOperation(value = "分页查询敏感词")
    public Result<PageResult> page(SensitivePageQueryDTO dto) {
        log.info("分页查询敏感词==> SensitivePageQueryDTO:{},", dto);
        PageResult page = sensitiveService.page(dto);
        return Result.success(page);
    }

    /**
     * 删除敏感词
     */
    @DeleteMapping
    @ApiOperation(value = "删除敏感词")
    public Result delete(@RequestParam List<Long> ids) {
        log.info("删除敏感词==> {}", ids);
        return sensitiveService.deleteByIds(ids);
    }
}
