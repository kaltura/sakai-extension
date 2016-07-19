/*
 * Copyright ©2016 Kaltura, Inc.
 */
package org.sakaiproject.kaltura.services;

import java.util.List;
import java.util.Optional;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.quartz.JobExecutionException;
import org.sakaiproject.component.app.scheduler.jobs.AbstractConfigurableJob;
import org.sakaiproject.component.api.ServerConfigurationService;
import org.sakaiproject.email.api.EmailService;
import org.sakaiproject.kaltura.api.dao.KalturaSiteCopyBatchDao;
import org.sakaiproject.kaltura.models.dao.KalturaSiteCopyBatch;
import org.sakaiproject.kaltura.api.dao.KalturaSiteCopyJobDao;
import org.sakaiproject.kaltura.models.dao.KalturaSiteCopyJob;

public class KalturaSiteCopyBatchJob extends AbstractConfigurableJob {

    private static final Logger log = LoggerFactory.getLogger(KalturaSiteCopyBatchJob.class);

    protected KalturaSiteCopyBatchDao kalturaSiteCopyBatchDao = null;
    protected KalturaSiteCopyJobDao kalturaSiteCopyJobDao = null;
    protected EmailService emailService = null;
    private ServerConfigurationService serverConfigurationService;
    protected String adminEmail = "";
    //This property will be overridden by "jobs.max.attempts" property
    protected int jobMaxAttempt = 10;
    protected String jobStatusToWork = KalturaSiteCopyBatch.NEW_STATUS;

    @Override
    public void runJob() throws JobExecutionException {
        Optional<KalturaSiteCopyBatch> job = kalturaSiteCopyBatchDao.checkWorkQueue(jobStatusToWork);
        if (!job.isPresent()) {
            log.debug("No \"Kaltura Site copy batch \" to work on.");
            return;
        }

        KalturaSiteCopyBatch j = workOnJob(job.get());
        if (!StringUtils.equalsIgnoreCase(KalturaSiteCopyBatch.COMPLETE_STATUS, j.getStatus())) {
            int jobMaxAttempt = serverConfigurationService.getInt("jobs.max.attempts",10);

            if (j.getAttempts() > jobMaxAttempt) {
                j.setStatus(KalturaSiteCopyBatch.FAILED_STATUS);
                log.error("kaltura jobs related to site copy from "+ j.getSourceSiteId() + " to "+ j.getTargetSiteId()+ "are still on pending status");
                log.error("Max attempts to check job status reached. Setting the status to failed for kaltura site copy batchId : " + j.getBatchId());
            }
        }

        kalturaSiteCopyBatchDao.save(j, true);
    }

    protected KalturaSiteCopyBatch workOnJob(KalturaSiteCopyBatch job) {
        // retrieve the all kaltura site copy jobs related to this batch id 
        List<KalturaSiteCopyJob> kaltura_jobs = kalturaSiteCopyJobDao.getAllJobs(job.getBatchId());
        boolean result = true;

        for (KalturaSiteCopyJob kalturaJob : kaltura_jobs) {
            if (!StringUtils.equalsIgnoreCase(KalturaSiteCopyJob.COMPLETE_STATUS, kalturaJob.getStatus())){
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

    public void setServerConfigurationService(ServerConfigurationService serverConfigurationService) {
        this.serverConfigurationService = serverConfigurationService;
    }

}
