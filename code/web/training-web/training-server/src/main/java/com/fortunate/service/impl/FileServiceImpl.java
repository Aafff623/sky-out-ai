package com.fortunate.service.impl;

import com.fortunate.properties.MinioProperties;
import com.fortunate.service.FileService;
import io.minio.*;
import io.minio.errors.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileServiceImpl implements FileService {
    private final MinioClient minioClient;
    private final MinioProperties minioProperties;

    /**
     * 文件上传Minio版
     *
     * @param file
     * @return
     */
    @Override
    public String uploadMinio(MultipartFile file) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        String bucketName = minioProperties.getBucketName();
        boolean bucketExists = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
        if (!bucketExists) {
            //如果桶不存在
            //创建桶
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
            //设置权限
            String policyConfig = this.createBucketPolicyConfig(bucketName);
            minioClient.setBucketPolicy(SetBucketPolicyArgs.builder().bucket(bucketName).config(policyConfig).build());
        }
        //文件名称
        String fileName = new SimpleDateFormat("yyyy/MM/dd").format(new Date()) + "/" +
                UUID.randomUUID() + "-" + file.getOriginalFilename();
        //上传到minio
        minioClient.putObject(PutObjectArgs.builder()
                .bucket(bucketName)
                .object(fileName)
                .contentType("image/png")
                .stream(file.getInputStream(), file.getSize(), -1)
                .build());
        String url = String.join("/", minioProperties.getEndpoint(), bucketName, fileName);
        log.info("上传文件到minio成功：{}", url);
        return url;
    }

    /**
     * 文件删除Minio版
     *
     * @param url
     */
    @Override
    public void deleteFileMinio(String url) {
        try {
            String bucketName = minioProperties.getBucketName();
            int bucketIndex = url.indexOf(bucketName);
            if (bucketIndex != -1) {
                String objectPath = url.substring(bucketIndex + bucketName.length() + 1);
                //2025/09/21/12d598fd-16fd-4ef1-ac46-bfbc02975894-31321c9bf88d4265c5f8da4dac30c92.jpg
                log.info("删除minio文件: {}", objectPath);
                minioClient.removeObject(RemoveObjectArgs.builder().bucket(bucketName).object(objectPath).build());
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }


    /**
     * 创建桶的权限
     *
     * @param bucketName
     * @return
     */
    private String createBucketPolicyConfig(String bucketName) {
        //解释  "Action" : "s3:GetObject" 获取读取对象
        // "Effect" : "Allow" 效果：允许
        //"Principal" : "*" 用户：* 所有用户
        //"Resource" : "arn:aws:s3:::%s/*" 资源  arn:aws:s3:::bucketName/* 桶下边的所有资源
        // 允许所有人读取这个桶下面的所有资源 写：则是默认只有自己才能写
        return """
                {
                  "Statement" : [ {
                    "Action" : "s3:GetObject",
                    "Effect" : "Allow",
                    "Principal" : "*",
                    "Resource" : "arn:aws:s3:::%s/*"
                  } ],
                  "Version" : "2012-10-17"
                }
                """.formatted(bucketName);
    }
}
