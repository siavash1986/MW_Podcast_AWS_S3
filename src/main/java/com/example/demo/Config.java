package com.example.demo;

public class Config {

  private String bucketName;
  private ReportPolicy reportPolicy;

  private static Config ourInstance = new Config();

  public static Config getInstance() {
    return ourInstance;
  }

  private Config() {
    this.bucketName = "mwwottd"; //Name your S3 bucket here
    this.reportPolicy = ReportPolicy.REPORT_FAILURE;
  }

  public String getBucketName() {
    return this.bucketName;
  }

  public ReportPolicy getReportPolicy() {
    return this.reportPolicy;
  }

}
