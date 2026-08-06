package com.fortunate.controller.upload;

import com.fortunate.constant.MessageConstant;
import com.fortunate.exception.FileException;
import com.fortunate.result.Result;
import com.fortunate.service.FileService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import static com.fortunate.constant.MessageConstant.FILE_NOT_SELECTED;

/**
 * @description
 */
@Slf4j
@Api(tags = "上传文件接口")
@RestController
@RequestMapping("/upload")
@RequiredArgsConstructor
public class UploadController {
    private final FileService fileService;

    /**
     * 文件上传Minio版
     *
     * @param file
     * @return
     */
    @PostMapping
    @ApiOperation("文件上传")
    public Result<String> uploadMinio(@RequestPart("file") MultipartFile file) {
        log.info("前端传过来的文件==> {}", file);
        if (file.isEmpty()) {
            throw new FileException(FILE_NOT_SELECTED);
        }
        // 上传文件到Minio，成功之后返回文件的路径
        try {
            String filePath = fileService.uploadMinio(file);
            return Result.success(filePath);
        } catch (Exception e) {
            log.error("文件上传失败，原因==> {}", e.getMessage());
        }

        return Result.error(MessageConstant.UPLOAD_FAILED);
    }
}
