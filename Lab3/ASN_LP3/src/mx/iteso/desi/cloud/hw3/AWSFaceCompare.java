/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mx.iteso.desi.cloud.hw3;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.rekognition.AmazonRekognition;
import com.amazonaws.services.rekognition.AmazonRekognitionClientBuilder;
import com.amazonaws.services.rekognition.model.CompareFacesMatch;
import com.amazonaws.services.rekognition.model.CompareFacesRequest;
import com.amazonaws.services.rekognition.model.CompareFacesResult;
import com.amazonaws.services.rekognition.model.Image;
import com.amazonaws.services.rekognition.model.S3Object;
import java.nio.ByteBuffer;
import java.util.List;

public class AWSFaceCompare {

    String srcBucket;
    AmazonRekognition rekognition;
    String accessKey;
    String secretKey;
    Regions region;

    public AWSFaceCompare(String accessKey, String secretKey, Regions region, String srcBucket) {
        this.srcBucket = srcBucket;
        this.region = region;
        this.accessKey = accessKey;
        this.secretKey = secretKey;
        this.region = region;

        AWSCredentialsProvider credProvider = new AWSStaticCredentialsProvider(new BasicAWSCredentials(accessKey, secretKey));
        rekognition = AmazonRekognitionClientBuilder.standard().withCredentials(credProvider).withRegion(region).build();
    }

    public Face compare(ByteBuffer imageBuffer) {
        Face finalFace = new Face("", 0f);

        List<String> names = S3.listFiles();
        names.stream().filter((name) -> !(!name.endsWith(".jpg"))).forEachOrdered((String name) -> {
            CompareFacesRequest compareFaceReq = new CompareFacesRequest()
                    .withSourceImage(new Image().withBytes(imageBuffer))
                    .withTargetImage(new Image().withS3Object(
                            new S3Object().withBucket(srcBucket).withName(name)));

            CompareFacesResult result = rekognition.compareFaces(compareFaceReq);
            result.getFaceMatches().stream().filter((match) -> (match.getSimilarity() > finalFace.getCofidence())).forEachOrdered((match) -> {
                finalFace.name = name;
                finalFace.cofidence = match.getSimilarity();
            });
        });

        return finalFace;
    }

}
