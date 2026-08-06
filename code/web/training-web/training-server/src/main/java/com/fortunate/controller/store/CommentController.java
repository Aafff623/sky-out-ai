package com.fortunate.controller.store;

import com.fortunate.query.CommentPageQueryDTO;
import com.fortunate.result.PageResult;
import com.fortunate.result.Result;
import com.fortunate.service.CommentService;
import com.fortunate.vo.CommentVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("clientCommentController")
@RequestMapping("/store/comment")
@Api(tags = "评论相关接口")
@RequiredArgsConstructor
@Slf4j
public class CommentController {
    private final CommentService commentService;

    /**
     * 查询所有评论
     *
     * @param dto
     * @return
     */
    @GetMapping("/page")
    @ApiOperation("分页查询评论")
    public Result<PageResult> page(CommentPageQueryDTO dto) {
        log.info("分页查询菜品信息==> {}", dto);
        PageResult pageResult = commentService.page(dto);
        return Result.success(pageResult);
    }

    /**
     * 查看评论详情
     */
    @GetMapping("/{id}")
    @ApiOperation("查看评论详情")
    public Result<CommentVo> getById(@PathVariable String id) {
        return commentService.getById(id);
    }

    /**
     * 批量删除评论
     */
    @DeleteMapping
    @ApiOperation("批量删除评论")
    public Result batchDelete(@RequestParam List<String> ids) {
        log.info("删除评论==> {}", ids);
        return commentService.deleteById(ids);
    }
}
