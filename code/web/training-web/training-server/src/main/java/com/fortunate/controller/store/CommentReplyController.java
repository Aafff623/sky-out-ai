package com.fortunate.controller.store;

import com.fortunate.dto.CommentRepayDTO;
import com.fortunate.dto.CommentRepaySaveDTO;
import com.fortunate.result.Result;
import com.fortunate.service.CommentRepayService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.fortunate.constant.CommentConstants.COMMENT_STATUS_SELLER;

@RestController
@RequestMapping("/store/comment-repay")
@Api(tags = "评论回复相关接口")
@RequiredArgsConstructor
public class CommentReplyController {

    private final CommentRepayService commentRepayService;

    @PostMapping("/save")
    @ApiOperation("评论回复")
    public Result commentRepay(@RequestBody CommentRepaySaveDTO dto) {
        return commentRepayService.commentRepay(dto, COMMENT_STATUS_SELLER);
    }

    /**
     * 查询所有评论回复
     *
     * @param dto
     * @return
     */
    @GetMapping("/list")
    @ApiOperation("查询所有评论回复")
    public Result list(@RequestBody CommentRepayDTO dto) {
        return commentRepayService.list(dto);
    }

    /**
     * 根据回复评论id集合批量删除回复
     *
     * @param ids
     * @return
     */
    @DeleteMapping
    @ApiOperation("删除回复评论")
    public Result deleteCommentRepay(@RequestParam List<String> ids) {
        return commentRepayService.deleteCommentRepay(ids);
    }

}
