# Merriam Webster Word of the Day Podcast Downloader

This simple Spring applicatin parses Merriam Webster Word of the Day web page and retrieves the MP3 podcast and upload it to Amazon AWS S3 bucket. 
Also this application leverage Amazon Simple Email Service (AWS SES) to report to administrator.

### Build

* On you server create <code>~/.aws/credentials</code> and add the following lines to <code>credentials</code>
    * <code>[default]<br/>
      aws_access_key_id = [YOUR_ACCESS_KEY_ID]<br />
      aws_secret_access_key = [YOUR_SECRET_ACCESS_KEY]+TaWc4PAOUN0oIo9x</code>

* Add the following lines to <code>application.properties</code>
    * <code>cloud.aws.region.static=[AWS_REGION]<br/>
     emailFrom=[AWS_SES_EMAIL]<br/>
     emailTo=[ADMIN_EMAIL]</code>
     

### Endpoints

<code>POST /podcast</code><br/>
Downloads the podcast for current date of server and upload it to AWS S3. Report to the administrator if the proper policy was chosen in <code>Config.java</code> singleton<br/>
returns <code>true</code> if the operation was successful and <code>false</code> if failed
---
 
<code>POST /podcast/{date}</code><br/>
<code>date</code> must be in <code>YYYY-MM-DD</code> format.<br/>
Downloads the podcast for specified date upload it to AWS S3. Report to the administrator if the proper policy was chosen in <code>Config.java</code> singleton<br/>
returns <code>true</code> if the operation was successful and <code>false</code> if failed<br/>
---

<code>POST /podcast/{date}/{date}</code><br/>
<code>date</code> must be in <code>YYYY-MM-DD</code> format.<br/>
Downloads all podcast between the two dates provided as the parameter, upload all of them to AWS S3.<br/>
returns an integer indicating the number of podcasts successfully uploaded

