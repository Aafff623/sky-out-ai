package com.fortunate.controller.client;


import com.fortunate.dto.CommentRepayDTO;
import com.fortunate.dto.CommentRepayLikeDTO;
import com.fortunate.dto.CommentRepaySaveDTO;
import com.fortunate.result.Result;
import com.fortunate.service.CommentRepayService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.fortunate.constant.CommentConstants.COMMENT_STATUS_USER;

@RestController
@RequestMapping("/client/comment-repay")
@RequiredArgsConstructor
@Api(tags = "评论回复接口")
public class CommentRepayController {

    private final CommentRepayService commentRepayService;

    /**
     * 评论回复
     *
     * @param dto
     * @return
     */
    @PostMapping("/save")
    @ApiOperation("评论回复")
    public Result commentRepay(@RequestBody CommentRepaySaveDTO dto) {
        return commentRepayService.commentRepay(dto,COMMENT_STATUS_USER);
    }

    /**
     * 评论回复的点赞
     * @param dto
     * @return
     */
    @PostMapping("/like")
    @ApiOperation("评论回复的点赞")
    public Result like(@RequestBody CommentRepayLikeDTO dto){
        return commentRepayService.like(dto);
    }

    /**
     * 评论回复列表
     *
     * @param dto
     * @return
     */
    @PostMapping("/load")
    @ApiOperation("评论回复列表")
    public Result load(@RequestBody CommentRepayDTO dto) {
        return commentRepayService.load(dto);
    }

    /**
     * 根据回复评论id集合批量删除回复
     * @param ids
     * @return
     */
    @DeleteMapping
    @ApiOperation("删除回复评论")
    public Result deleteCommentRepay(@RequestParam List<String> ids){
        return commentRepayService.deleteCommentRepay(ids);
    }
}
