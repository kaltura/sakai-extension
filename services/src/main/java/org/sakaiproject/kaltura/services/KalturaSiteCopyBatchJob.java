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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.lang.StringUtils;
import org.quartz.JobExecutionException;
import org.sakaiproject.component.app.scheduler.jobs.AbstractConfigurableJob;
import org.sakaiproject.component.cover.ComponentManager;
import org.sakaiproject.component.api.ServerConfigurationService;
import org.sakaiproject.email.api.EmailService;
import org.sakaiproject.kaltura.api.dao.KalturaSiteCopyBatchDao;
import org.sakaiproject.kaltura.models.dao.KalturaSiteCopyBatch;
import org.sakaiproject.kaltura.api.dao.KalturaSiteCopyJobDao;
import org.sakaiproject.kaltura.models.dao.KalturaSiteCopyJob;

public class KalturaSiteCopyBatchJob extends AbstractConfigurableJob
 {

    private static final Log
        log = LogFactory.getLog(KalturaSiteCopyBatchJob.class);

    protected KalturaSiteCopyBatchDao kalturaSiteCopyBatchDao = null;
    protected KalturaSiteCopyJobDao kalturaSiteCopyJobDao = null;
    protected EmailService emailService = null;
    private ServerConfigurationService serverConfigurationService;
    protected String adminEmail = "";
    //This property will be overridden by "jobs.max.attempts" property
    protected int jobMaxAttempt = 10;
    protected String jobStatusToWork = KalturaSiteCopyBatch.NEW_STATUS;

    @Override
    public void runJob() throws JobExecutionException
    {
        KalturaSiteCopyBatch job = kalturaSiteCopyBatchDao.checkWorkQueue(jobStatusToWork);
        if (job == null) {
            log.debug("No \"Kaltura Site copy batch \" to work on.");            
            return;
        }
        job.setStatus(KalturaSiteCopyBatch.IN_PROGRESS_STATUS);
        job = workOnJob(job);
        if (KalturaSiteCopyBatch.COMPLETE_STATUS.equals(job.getStatus())) {

            // sendEmail(job);
        } else {
            //sendEmail(job);
            int jobMaxAttempt = serverConfigurationService.getInt("jobs.max.attempts",10);
            if(job.getAttempts() > jobMaxAttempt){
                job.setStatus(KalturaSiteCopyBatch.FAILED_STATUS);
                log.error("kaltura jobs related to site copy from "+ job.getSourceSiteId() + " to "+ job.getTargetSiteId()+ "are still on pending status");
                log.error("Max attempts to check job status reached. Setting the status to failed for kaltura site copy batchId : " + job.getBatchId());
            }
        }
        kalturaSiteCopyBatchDao.save(job, true);
    }

    protected KalturaSiteCopyBatch workOnJob(KalturaSiteCopyBatch job) {

        // retrieve the all kaltura site copy jobs related to this batch id 
        List<KalturaSiteCopyJob> kaltura_jobs = kalturaSiteCopyJobDao.getAllJobs(job.getBatchId());
        boolean result = true;
        for(KalturaSiteCopyJob kalturaJob:kaltura_jobs){
            if(!KalturaSiteCopyJob.COMPLETE_STATUS.equals(kalturaJob.getStatus())){
                result = false;
                log.debug("Kaltura job related to the site copy is pending on kaltura side :"+ kalturaJob.getJobId());
                break;
            }
        }
        if(result){
            job.setStatus(KalturaSiteCopyBatch.COMPLETE_STATUS);
            log.debug("All kaltura jobs related to site copy from "+ job.getSourceSiteId() + " to "+ job.getTargetSiteId()+ "are complete on kaltura side");
        }
        // increment attempts
        job.setAttempts( job.getAttempts()+1);                       
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

    public void setServerConfigurationService(ServerConfigurationService serverConfigurationService) {
        this.serverConfigurationService = serverConfigurationService;
    }

}
