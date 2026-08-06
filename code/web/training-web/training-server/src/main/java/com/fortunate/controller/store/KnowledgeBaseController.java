package com.fortunate.controller.store;

import com.fortunate.exception.FileException;
import com.fortunate.query.KnowledgeBasePageQueryDTO;
import com.fortunate.result.PageResult;
import com.fortunate.result.Result;
import com.fortunate.service.KnowledgeBaseService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static com.fortunate.constant.MessageConstant.FILE_NOT_SELECTED;

@RestController
@RequestMapping("/store/knowledgeBase")
@RequiredArgsConstructor
@Slf4j
@Api(tags = "向量存储相关接口")
public class KnowledgeBaseController {

    private final KnowledgeBaseService knowledgeBaseService;

    @PostMapping("/embed")
    @ApiOperation("新增知识库")
    public Result addKnowledgeBase(@RequestPart("file") MultipartFile file) throws Exception {
        log.info("新增知识库 ==> {}", file);
        if (file.isEmpty()) {
            throw new FileException(FILE_NOT_SELECTED);
        }
        String result = knowledgeBaseService.addKnowledgeBase(file);
        return Result.success(result);
    }

    @GetMapping("/page")
    @ApiOperation("知识库分页查询")
    public Result<PageResult> page(KnowledgeBasePageQueryDTO dto) {
        log.info("分页查询知识库 ==> {}", dto);
        PageResult pageResult = knowledgeBaseService.page(dto);
        return Result.success(pageResult);
    }

    /**
     * 批量删除知识库
     *
     * @param ids
     * @return
     */
    @DeleteMapping("/delete")
    @ApiOperation("删除知识库")
    public Result batchDelete(@RequestParam List<Integer> ids) {
        log.info("删除知识库 ==> {}", ids);
        int i = knowledgeBaseService.deleteByIds(ids);
        if (i <= 0) {
            return Result.error();
        }
        return Result.success();
    }
}
