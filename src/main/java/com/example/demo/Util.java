package com.example.demo;

import com.amazonaws.SdkClientException;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.*;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

public class Util {

  private static AmazonS3 s3Client;

  static {
    s3Client = AmazonS3ClientBuilder.standard()
        .withRegion(Regions.US_EAST_2)
        .build();
  }


  public static String getPodcastUrl(String date) {
    String baseUrl = "https://www.merriam-webster.com/word-of-the-day/" + date;
    Document document;
    String val = "";
    try {
      document = Jsoup.connect(baseUrl).get();
      Element element = document.select("#art19-podcast-player").get(0);
      for (Attribute attribute : element.attributes()) {
        if (attribute.getKey().equals("data-episode-id")) {
          val = attribute.getValue();
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
      return null;
    }

    return getPodcastUrlHelper(val);
  }

  private static String getPodcastUrlHelper(String offset) {
    String url = "https://rss.art19.com/episodes/" + offset + ".mp3";
    OkHttpClient client = new OkHttpClient();


    Request request = new Request.Builder()
        .url(url)
        .build();

    try (Response response = client.newCall(request).execute()) {
//       response = client.newCall(request).execute();
      return response == null ? "" : response.request().url().toString();
    } catch (IOException e) {
      e.printStackTrace();
    }

//    return response == null? "" :  response.request().url().toString();
    return null;

  }


  public static File downloadEposide(String podcastUrl, String episodeNumber) {
    String episodeFileName = String.format("wd%s.mp3", episodeNumber.replace("-", ""));
    try {
      URL website = new URL(podcastUrl);
      ReadableByteChannel rbc = Channels.newChannel(website.openStream());
      FileOutputStream fos = new FileOutputStream(episodeFileName);
      fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
    } catch (IOException e) {
      e.printStackTrace();
      return null;
    }

    return new File(episodeFileName);
  }

  public static boolean uploadToAWSS3(File file) {

    try {
      String keyName = String.format("episodes/%s", file.getName());
      PutObjectRequest request = new PutObjectRequest(Config.getInstance().getBucketName(), keyName, file);
      ObjectMetadata metadata = new ObjectMetadata();
      metadata.setContentType("audio/mpeg");
      request.setMetadata(metadata);
      s3Client.putObject(request);
      AccessControlList acl = s3Client.getObjectAcl(Config.getInstance().getBucketName(), keyName);
      acl.grantPermission(GroupGrantee.AllUsers, Permission.Read);
      s3Client.setObjectAcl(Config.getInstance().getBucketName(), keyName, acl);
    } catch (SdkClientException e) {
      e.printStackTrace();
      return false;
    }
    return true;
  }
}
