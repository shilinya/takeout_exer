package com.sky.service.impl;

import com.sky.minio.MinioProperties;
import com.sky.service.FileService;
import io.minio.*;
import io.minio.errors.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

@Service
public class FileServiceImpl implements FileService {
    @Autowired
    private MinioClient minioClient;
    @Autowired
    private MinioProperties minioProperties;

    @Override
    public String upload(MultipartFile file) throws InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, XmlParserException, InternalException, ServerException, InvalidResponseException {

            boolean bucketExists = minioClient.bucketExists(BucketExistsArgs.builder().bucket(minioProperties
                    .getBucketName()).build());
            if (!bucketExists) {
                //创建桶   建造者模式
                minioClient.makeBucket(MakeBucketArgs.builder()
                        .bucket(minioProperties
                                .getBucketName())
                        .build());
                //设置桶的修改访问权限
                minioClient.setBucketPolicy(SetBucketPolicyArgs
                        .builder()
                        .bucket(minioProperties
                                .getBucketName())
                        .config(createBucketPolicyConfig(minioProperties
                                .getBucketName()))
                        .build());
            }
            String filename = new SimpleDateFormat("yyyyMMdd").format(new Date()) +
                    "/" + UUID.randomUUID() + "-" + file.getOriginalFilename();
            //上传文件
            minioClient.putObject(PutObjectArgs
                    .builder()
                    .bucket(minioProperties.getBucketName())
                    .stream(file.getInputStream(), file.getSize(), -1)
                    .object(filename)
                    .contentType(file.getContentType())
                    .build());
            String url = minioProperties.getEndpoint() + "/" + minioProperties.getBucketName() + "/" + filename;
            return url;

    }

    private String createBucketPolicyConfig(String bucketName) {

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
