package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.util.concurrent.Callable;

public class PodcastDownloader implements Callable<Boolean> {

  private String date;
  private String podcastUrl;
  private File file;


  public PodcastDownloader(String date) {
    this.date = date;
  }


  @Override
  public Boolean call() throws Exception {
    this.podcastUrl = getPodcastUrl(this.date);
    if (podcastUrl == null) return false;
    else {
      this.file = downloadEposide(this.podcastUrl, this.date);
    }
    if (file == null) return false;
    else {
      return uploadToAWSS3(this.file);
    }
  }


  private String getPodcastUrl(String date) {
    return Util.getPodcastUrl(date);
  }

  private File downloadEposide(String podcastUrl, String date) {
    return Util.downloadEposide(podcastUrl, date);
  }

  private boolean uploadToAWSS3(File file) {
    return Util.uploadToAWSS3(file);
  }
}
