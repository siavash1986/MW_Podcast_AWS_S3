package com.example.demo;


import com.amazonaws.regions.Regions;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClientBuilder;
import com.amazonaws.services.simpleemail.model.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.logging.Level;
import java.util.logging.Logger;

@Component
public class AmazonSES {

  @Value("${emailFrom}")
  private String FROM;

  @Value("${emailTo}")
  private String TO;

  public boolean sendEmail(String subject, String mail) {

    try {
      AmazonSimpleEmailService client =
          AmazonSimpleEmailServiceClientBuilder.standard()
              .withRegion(Regions.US_WEST_2)
              .build();
      SendEmailRequest request = new SendEmailRequest()
          .withDestination(
              new Destination().withToAddresses(TO))
          .withMessage(new Message()
              .withBody(new Body()
                  .withText(new Content()
                      .withCharset("UTF-8").withData(mail))
              )
              .withSubject(new Content()
                  .withCharset("UTF-8").withData(subject)))
          .withSource(FROM);

      client.sendEmail(request);
      Logger.getGlobal().log(Level.INFO, "Email sent!");
    } catch (Exception ex) {
      Logger.getGlobal().log(Level.SEVERE, "The email was not sent. Error message: "
          + ex.getMessage());
      return false;
    }

    return true;
  }
}