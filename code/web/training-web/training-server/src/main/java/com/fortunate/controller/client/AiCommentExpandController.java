package com.fortunate.controller.client;

import com.fortunate.dto.CommentSaveDTO;
import com.fortunate.result.Result;
import com.fortunate.service.AiCommentExpandService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/client/ai-comment")
@RequiredArgsConstructor
@Api(tags = "Ai评论接口")
public class AiCommentExpandController {
    private final AiCommentExpandService aiCommentExpandService;

    /**
     * ai评论帮写、续写、润色、精简
     */
    @PostMapping("/expand")
    @ApiOperation("ai评论帮写、续写、润色、精简")
    public Result<Map<String, String>> expand(@RequestBody CommentSaveDTO dto) {
        return Result.success(aiCommentExpandService.expand(dto));
    }
}
