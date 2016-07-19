/*
 * Copyright ©2016 Kaltura, Inc.
 */
package org.sakaiproject.kaltura.services;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;
import org.quartz.JobExecutionException;
import org.sakaiproject.component.api.ServerConfigurationService;
import org.sakaiproject.component.app.scheduler.jobs.AbstractConfigurableJob;
import org.sakaiproject.email.api.EmailService;
import org.sakaiproject.kaltura.api.dao.KalturaSiteCopyBatchDao;
import org.sakaiproject.kaltura.api.dao.KalturaSiteCopyJobDao;
import org.sakaiproject.kaltura.models.dao.KalturaSiteCopyBatch;
import org.sakaiproject.kaltura.models.dao.KalturaSiteCopyJob;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

public class CheckKalturaSiteCopyJob extends AbstractConfigurableJob {

    private static final Logger log = LoggerFactory.getLogger(CheckKalturaSiteCopyJob.class);

    protected KalturaSiteCopyBatchDao kalturaSiteCopyBatchDao = null;
    protected KalturaSiteCopyJobDao kalturaSiteCopyJobDao = null;
    protected EmailService emailService = null;
    protected String adminEmail = "";
    protected String jobStatusToWork = KalturaSiteCopyBatch.NEW_STATUS;

    private ServerConfigurationService serverConfigurationService;
    public void setServerConfigurationService(ServerConfigurationService serverConfigurationService) {
        this.serverConfigurationService = serverConfigurationService;
    }

    private KalturaLTIService service;
    public void setService(KalturaLTIService kalturaLTIService) {
        this.service = kalturaLTIService;
    }

    @Override
    public void runJob() throws JobExecutionException {
        Optional<KalturaSiteCopyJob> job = kalturaSiteCopyJobDao.checkWorkQueue(jobStatusToWork);

        if (!job.isPresent()) {
            log.debug("No \"Kaltura Site copy job \" to work on.");
            return;
        }

        KalturaSiteCopyJob j = workOnJob(job.get());

        if (StringUtils.equals(KalturaSiteCopyJob.COMPLETE_STATUS, j.getStatus())) {
            log.debug("Kaltura job for site copy with kaltura job Id : "+ j.getKalturaJobId() + " is complete");
        } else {
            // if max attempts is reached, then set the job to failed status and send a notification to admin email with message from kaltura
            int jobMaxAttempt = serverConfigurationService.getInt("jobs.max.attempt", 10);

            if (j.getAttempts() > jobMaxAttempt) {
                j.setStatus(KalturaSiteCopyJob.FAILED_STATUS);
                log.error("Setting Kaltura Job status to failed after checking for max attempts :"+ j.toString());
            }
        }

        kalturaSiteCopyJobDao.save(j, true);
    }

    protected KalturaSiteCopyJob workOnJob(KalturaSiteCopyJob job) {
       // use kalturaLtiService to get lti properties
       // make a POST request to kaltura server
       // based on JSOn response, update job status
        KalturaSiteCopyBatch siteCopyBatch = kalturaSiteCopyBatchDao.getSiteCopyBatch(job.getBatchId());
        String sourceSiteId = null;

        if (siteCopyBatch != null) {
            sourceSiteId = siteCopyBatch.getSourceSiteId();
        }

        if (service != null) {
            String module ="check-job";
            Properties ltiProps = service.prepareJobStatusRequest(module,sourceSiteId,job.getKalturaJobId().toString());

            try {
                String launch_url = serverConfigurationService.getString("kaltura.host") + "/hosted/index";
                launch_url=launch_url + "/" + module;
                HttpClient client = new DefaultHttpClient();
                HttpPost post = new HttpPost(launch_url);
                List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
                StringBuffer nameValues = new StringBuffer();

                for (Object lkey : ltiProps.keySet()) {
                    String ltiKey = (String) lkey;
                    String ltiValue = ltiProps.getProperty(ltiKey);
                    nameValues.append(ltiKey + "=" + ltiValue + "\n");
                    urlParameters.add(new BasicNameValuePair(ltiKey, ltiValue));
                }

                log.debug("Kaltura Job Status check - POST params sent are : " + nameValues.toString());
                post.setEntity(new UrlEncodedFormEntity(urlParameters));
                HttpResponse response = client.execute(post);

                String line = "";
                try (BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"))) {
                    StringBuilder sb = new StringBuilder();
                    String nextLine;
                    while ((nextLine = rd.readLine()) != null) {
                        sb.append(nextLine);
                        break;
                    }
                    if (sb.length() != 0) {
                        line = sb.toString();
                    }
                }

                //first line contains required JSON string
                log.debug("Json Response from kaltura for job :" + job.getKalturaJobId() + " is :" + line);
                JSONObject jobStatus = new JSONObject(line);
                if (jobStatus != null) {
                    if (!jobStatus.isNull("resultCode")) {
                        int resultCode = jobStatus.getInt("resultCode");
                        log.debug("Resultcode on kaltura check job status is :" + resultCode);

                        if (resultCode == 0) {
                            // it is still running on kaltura end so set our job status to new
                            job.setStatus(KalturaSiteCopyJob.NEW_STATUS);
                        }else if (resultCode == 1) {
                            job.setStatus(KalturaSiteCopyJob.COMPLETE_STATUS);
                        }else {
                            job.setStatus(KalturaSiteCopyJob.NEW_STATUS);
                        }
                    }

                    if (!jobStatus.isNull("status")) {
                        int status = jobStatus.getInt("status");
                        log.debug("status on kaltura check job status is :"+ status);
                    }

                    if (!jobStatus.isNull("description")) {
                        String description = jobStatus.getString("description");
                        log.debug("description on kaltura check job status is : " + description);
                    }
                }

            } catch (Exception e) {
                log.error("Exception while sending post to kaltura to check site copy job status:" + job.getKalturaJobId(), e);
            }
            //increment job attempts
            job.setAttempts(job.getAttempts() +1);
        }

        return job;
    }
    
    public void setKalturaSiteCopyBatchDao(KalturaSiteCopyBatchDao service) {
        kalturaSiteCopyBatchDao = service;
    }

    public KalturaSiteCopyBatchDao getKalturaSiteCopyBatchDao() {
        return kalturaSiteCopyBatchDao;
    }

    public void setKalturaSiteCopyJobDao(KalturaSiteCopyJobDao service) {
        kalturaSiteCopyJobDao = service;
    }

    public KalturaSiteCopyJobDao getKalturaSiteCopyJobDao() {
        return kalturaSiteCopyJobDao;
    }

    public void setEmailService(EmailService service) {
        emailService = service;
    }

    public EmailService getEmailService() {
        return emailService;
    }

    public void setAdminEmail(String email) {
        adminEmail = email;
    }

    public String getAdminEmail() {
        return adminEmail;
    }

}
