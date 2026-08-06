package com.fortunate.controller.client;

import com.fortunate.context.BaseContext;
import com.fortunate.query.CommentMessagePageQueryDTO;
import com.fortunate.result.Result;
import com.fortunate.service.CommentMessageService;
import com.fortunate.vo.CommentMessageVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/client/comment-message")
@RequiredArgsConstructor
@Api(tags = "用户互动消息评论接口")
public class CommentMessageController {

    private final CommentMessageService commentMessageService;

    /**
     * 查询用户的被回复评论消息
     */
    @GetMapping
    @ApiOperation("查询用户的被回复评论消息")
    public Result<List<CommentMessageVO>> queryCommentMessage(CommentMessagePageQueryDTO dto) {
        Long userId = BaseContext.getUserId();
        List<CommentMessageVO> commentMessageVOList =  commentMessageService.queryCommentMessage(userId,dto);
        return Result.success(commentMessageVOList);
    }

    @GetMapping("/unread/count")
    @ApiOperation("查询用户未读消息数量")
    public Result<Integer> count() {
        Long userId = BaseContext.getUserId();
        Integer count =  commentMessageService.count(userId);
        return Result.success(count);
    }
}
