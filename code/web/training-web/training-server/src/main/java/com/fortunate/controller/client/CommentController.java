package com.fortunate.controller.client;

import com.fortunate.dto.CommentDTO;
import com.fortunate.dto.CommentLikeDTO;
import com.fortunate.dto.CommentSaveDTO;
import com.fortunate.dto.IsCommentDTO;
import com.fortunate.result.Result;
import com.fortunate.service.CommentService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("storeCommentController")
@RequestMapping("/client/comment")
@RequiredArgsConstructor
@Api(tags = "C端-评论接口")
@Slf4j
public class CommentController {

    private final CommentService commentService;

    /**
     * 完成订单保存评论
     * @param dto
     * @return
     */
    @PostMapping("/save")
    @ApiOperation("完成订单保存评论")
    public Result comment(@RequestBody CommentSaveDTO dto) {
        return commentService.comment(dto);
    }

    /**
     *
     * 菜品评论点赞
     * @param dto
     * @return
     */
    @PostMapping("/like")
    public Result list(@RequestBody CommentLikeDTO dto) {
        return commentService.like(dto);
    }

    /**
     * 查看评论列表
     * @param dto
     * @return
     */
    @PostMapping("/load")
    @ApiOperation("查看评论列表")
    public Result load(@RequestBody CommentDTO dto) {
        return commentService.load(dto);
    }

    /**
     * 查询订单是否已评论
     */
    @PostMapping("/isComment")
    @ApiOperation("查询订单是否已评论")
    public Result isComment(@RequestBody IsCommentDTO orderIds) {
        return commentService.isComment(orderIds);
    }

    /**
     * 删除评论
     */
    @DeleteMapping
    @ApiOperation("删除评论")
    public Result delete(@RequestParam List<String> ids) {
        log.info("删除评论==> {}", ids);
        return commentService.deleteById(ids);
    }
}
