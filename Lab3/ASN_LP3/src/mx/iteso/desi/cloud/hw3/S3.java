/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mx.iteso.desi.cloud.hw3;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Saúl
 */
public class S3 {

    private static final AmazonS3 s3;

    static {
        AWSCredentialsProvider credentialsProvider = new AWSStaticCredentialsProvider(new BasicAWSCredentials(ConfigSecret.AWS_ACCESS_KEY_ID, ConfigSecret.AWS_SECRET_ACCESS_KEY));
        s3 = AmazonS3ClientBuilder.standard().withCredentials(credentialsProvider).withRegion(Config.amazonRegion).build();
    }

    public static void putObject(String key, InputStream inputStream, long size) {
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(size);
        PutObjectRequest objectRequest = new PutObjectRequest(Config.srcBucket, key, inputStream, metadata);
        s3.putObject(objectRequest);
    }

    public static List<String> listFiles() {
        List<String> objects = new ArrayList<>();
        s3.listObjects(Config.srcBucket).getObjectSummaries().forEach((o) -> {
            objects.add(o.getKey());
        });

        return objects;
    }
}
