/**
 * Copyright 2014 Sakaiproject Licensed under the
 * Educational Community License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may
 * obtain a copy of the License at
 *
 * http://www.osedu.org/licenses/ECL-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS"
 * BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */

package org.sakaiproject.kaltura.services;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.Properties;
import java.util.HashMap;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.lang.StringUtils;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import org.json.JSONArray;
import org.json.JSONObject;


import org.quartz.JobExecutionException;
import org.sakaiproject.component.app.scheduler.jobs.AbstractConfigurableJob;
import org.sakaiproject.component.cover.ComponentManager;
import org.sakaiproject.component.api.ServerConfigurationService;
import org.sakaiproject.email.api.EmailService;
import org.sakaiproject.kaltura.api.dao.KalturaSiteCopyBatchDao;
import org.sakaiproject.kaltura.models.dao.KalturaSiteCopyBatch;
import org.sakaiproject.kaltura.api.dao.KalturaSiteCopyJobDao;
import org.sakaiproject.kaltura.models.dao.KalturaSiteCopyJob;
import org.sakaiproject.kaltura.services.KalturaLTIService;

public class CheckKalturaSiteCopyJob extends AbstractConfigurableJob
 {

    private static final Log
        log = LogFactory.getLog(CheckKalturaSiteCopyJob.class);

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
    public void runJob() throws JobExecutionException
    {
        KalturaSiteCopyJob job = kalturaSiteCopyJobDao.checkWorkQueue(jobStatusToWork);
        if (job == null) {
            log.debug("No \"Kaltura Site copy job \" to work on.");
            return;
        }
        job = workOnJob(job);
        if (KalturaSiteCopyJob.COMPLETE_STATUS.equals(job.getStatus())) {
            log.debug("Kaltura job for site copy with kaltura job Id : "+ job.getKalturaJobId() + " is complete");
        } else {
            // if max attempts is reached, then set the job to failed status and send a notification to admin email with message from kaltura
            int jobMaxAttempt = serverConfigurationService.getInt("jobs.max.attempt",10);
            if(job.getAttempts() > jobMaxAttempt){
                job.setStatus(KalturaSiteCopyJob.FAILED_STATUS);
            }
            log.error("Setting Kaltura Job status to failed after checking for max attempts :"+ job.toString());
        }
        kalturaSiteCopyJobDao.save(job,true);
    }

    protected KalturaSiteCopyJob workOnJob(KalturaSiteCopyJob job) {

       // use kalturaLtiService to get lit properties

       // make a POST request to kaltura server

       // based on JSOn response , update job status
        KalturaSiteCopyBatch siteCopyBatch = kalturaSiteCopyBatchDao.getSiteCopyBatch(job.getBatchId());
        String sourceSiteId = null;
        if(siteCopyBatch!=null){
            sourceSiteId = siteCopyBatch.getSourceSiteId();
        }
        if(service!=null){
            String module ="check-job";
            Properties ltiProps = service.prepareJobStatusRequest(module,sourceSiteId,job.getKalturaJobId().toString());
            try{
                String launch_url = serverConfigurationService.getString("kaltura.launch.url");
                launch_url=launch_url+"/"+ module;
                HttpClient client = new DefaultHttpClient();
                HttpPost post = new HttpPost(launch_url);
                List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
                for(Object lkey : ltiProps.keySet()) {
                    String ltiKey = (String) lkey;
                    String ltiValue = ltiProps.getProperty(ltiKey);
                    urlParameters.add(new BasicNameValuePair(ltiKey, ltiValue));
                }
                post.setEntity(new UrlEncodedFormEntity(urlParameters));

                HttpResponse response = client.execute(post);
                BufferedReader rd = new BufferedReader(
                        new InputStreamReader(response.getEntity().getContent()));

                StringBuffer result = new StringBuffer();
                String line = "";
                while ((line = rd.readLine()) != null) {
                    result.append(line);
                    break;
                }
                //first line contains required JSON string
                String jsonString = StringUtils.substringBetween(line,"{","}");
                jsonString = "{" + jsonString + "}";
                log.debug("Json Response from kaltura for job :" + job.getKalturaJobId() + " is :" + jsonString);
                JSONObject jobStatus = new JSONObject(jsonString);
                if(jobStatus!=null){
                    int resultCode = jobStatus.getInt("resultCode");
                    int status = jobStatus.getInt("status");
                    String description = jobStatus.getString("description");
                    log.debug("Resultcode on kaltura check job status is :"+ resultCode);
                    log.debug("status on kaltura check job status is :"+ status);
                    if(description!=null){
                        log.debug("description on kaltura check job status is :" + description);
                    }
                    if(resultCode==0){
                        // it is still running on kaltura end so set our job status to new 
                        job.setStatus(KalturaSiteCopyJob.NEW_STATUS);                        
                    }else if(resultCode == 1){
                        job.setStatus(KalturaSiteCopyJob.COMPLETE_STATUS);
                    }else{
                        job.setStatus(KalturaSiteCopyJob.NEW_STATUS);
                        log.debug("Kaltura site copy job with id :" + job.getKalturaJobId() + " has failed on kaltura side. Here is the message from kaltura:"+ description);
                    }
                }

            }catch(Exception e){
               log .error("Exception while sending post to kaltura to check site copy job status:" + job.getKalturaJobId());               
                e.printStackTrace();
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

    public void setEmailService(EmailService service)
    {
        emailService = service;
    }

    public EmailService getEmailService()
    {
        return emailService;
    }

    public void setAdminEmail(String email)
    {
        adminEmail = email;
    }

    public String getAdminEmail()
    {
        return adminEmail;
    }

}
