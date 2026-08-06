package com.fortunate;

import com.fortunate.properties.MinioProperties;
import io.minio.*;
import io.minio.errors.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.websocket.servlet.WebSocketServletAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

@SpringBootTest
@EnableAutoConfiguration(exclude = {WebSocketServletAutoConfiguration.class})
public class MinioTest {
    @Autowired
    private MinioClient minioClient;
    @Autowired
    private MinioProperties minioProperties;

//    @Test
    public void testUpload() throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        File file = new File("D:\\TestAvatar\\4.jpg");
        FileInputStream fileInputStream = new FileInputStream(file);
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
                UUID.randomUUID() + "-" + file.getName();
        //上传到minio
        minioClient.putObject(PutObjectArgs.builder()
                .bucket(bucketName)
                .object(fileName)
                .contentType("image/png")
                .stream(fileInputStream, file.length(), -1)
                .build());
        String url = String.join("/", minioProperties.getEndpoint(), bucketName, fileName);
        System.out.println("上传文件成功：" + url);
    }

    //    @Test
    void testDelete() throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        String url = "http://192.168.88.130:9000/training-take-out/2025/09/21/12d598fd-16fd-4ef1-ac46-bfbc02975894-31321c9bf88d4265c5f8da4dac30c92.jpg";
        RemoveObjectArgs objectArgs = RemoveObjectArgs.builder().bucket(minioProperties.getBucketName())
                .object("2025/09/21/2587f329-7a02-4856-abf6-6a17b1913f8a-b5e27f9960dbaaf0eb7ae4d411c42b3.jpg")
                .build();
        minioClient.removeObject(objectArgs);

    }


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
