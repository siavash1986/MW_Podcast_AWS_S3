package com.example.demo;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.*;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;

@SpringBootApplication
public class DemoApplication {

  public static void main(String[] args) {
    SpringApplication.run(DemoApplication.class, args);
  }

  @PostConstruct
  void test() {
    try {

      AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
          .withRegion(Regions.US_EAST_2)
          .build();


      if (!s3Client.doesBucketExistV2(Config.getInstance().getBucketName())) {
        System.out.println("Bucket is missing");

        // Because the CreateBucketRequest object doesn't specify a region, the
        // bucket is created in the region specified in the client.
        s3Client.createBucket(new CreateBucketRequest(Config.getInstance().getBucketName()));

        // Verify that the bucket was created by retrieving it and checking its location.
        String bucketLocation = s3Client.getBucketLocation(new GetBucketLocationRequest(Config.getInstance().getBucketName()));

        AccessControlList bucketAcl = s3Client.getBucketAcl(Config.getInstance().getBucketName());
        bucketAcl.grantPermission(GroupGrantee.AllUsers, Permission.Read);
        s3Client.setBucketAcl(Config.getInstance().getBucketName(), bucketAcl);

        System.out.println("Bucket location: " + bucketLocation);
      }


    } catch (AmazonServiceException e) {
      e.printStackTrace();
    }

  }
}
