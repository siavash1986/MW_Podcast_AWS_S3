package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;


@org.springframework.stereotype.Controller
public class Controller {

  private AmazonSES emailClient;

  @Autowired
  Controller(AmazonSES amazonMailClient) {
    this.emailClient = amazonMailClient;
  }

  @PostMapping(value = "/podcast")
  @ResponseBody
  public boolean getCurrentPodcast() {
    String currentPodcast = LocalDate.now().toString();
    return getPodcast(currentPodcast);
  }

  @PostMapping(value = "/podcast/{date}")
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public boolean getPodcast(@PathVariable String date) {

    ExecutorService service = Executors.newSingleThreadExecutor();
    Future<Boolean> submit = service.submit(new PodcastDownloader(date));
    boolean result = false;

    try {
      result = submit.get();
      report(result, date);
    } catch (ExecutionException | InterruptedException e) {
      report(false, date);
    }

    return result;
  }

  @PostMapping(value = "/podcast/{dateFrom}/{dateTo}")
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public int getPodcasts(@PathVariable("dateFrom") String dateFrom,
                         @PathVariable("dateTo") String dateTo) {

    int success = 0;
    List<String> dateRange = new ArrayList<>();
    LocalDate localDateFrom = LocalDate.parse(dateFrom);
    LocalDate localDateTo = LocalDate.parse(dateTo);

    while (localDateFrom.isBefore(localDateTo.plusDays(1))) {
      dateRange.add(localDateFrom.format(DateTimeFormatter.ISO_DATE));
      localDateFrom = localDateFrom.plusDays(1);
    }


    ArrayList<Callable<Boolean>> tasks = dateRange.parallelStream()
        .collect(ArrayList::new,
            (a, d) -> a.add(new PodcastDownloader(d)),
            ArrayList::addAll);

    ExecutorService service = Executors.newCachedThreadPool();
    List<Future<Boolean>> futures = null;


    try {
      futures = service.invokeAll(tasks);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

    //Calculates number of successful task and returns it
    if (futures == null) {
      return 0;
    } else {
      for (Future<Boolean> result : futures) {
        try {
          if (result.get()) {
            success++;
          }
        } catch (ExecutionException | InterruptedException e) {
          e.printStackTrace();
        }
      }
      return success;
    }

  }

  private void report(boolean result, String episode) {

    String subject = result ? String.format("Successfully uploaded %s to AWS", episode) : String.format("failed to upload %s to AWS", episode);
    String body = result ? String.format("TimeStamp: %s \n episode \"%s\" successfully uploaded to AWS S3", LocalDateTime.now(), episode) :
        String.format("TimeStamp: %s \n Failed to upload episode \"%s\" to AWS S3", LocalDateTime.now(), episode);

    ReportPolicy policy = Config.getInstance().getReportPolicy();

    if (result && (policy == ReportPolicy.REPORT_SUCCESS || policy == ReportPolicy.REPORT_ALL)) {
      emailClient.sendEmail(subject, body);
    }
    if (!result && (policy == ReportPolicy.REPORT_FAILURE || policy == ReportPolicy.REPORT_ALL)) {
      emailClient.sendEmail(subject, body);
    }

  }

}
