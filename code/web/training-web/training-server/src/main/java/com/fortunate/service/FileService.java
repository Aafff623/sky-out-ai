package com.fortunate.service;

import org.springframework.web.multipart.MultipartFile;

public interface FileService {

    /**
     * 文件上传Minio版
     *
     * @param file
     * @return
     * @throws Exception
     */
    String uploadMinio(MultipartFile file) throws Exception;

    /**
     * 文件删除Minio版
     * @param url
     */
    void deleteFileMinio(String url);
}
