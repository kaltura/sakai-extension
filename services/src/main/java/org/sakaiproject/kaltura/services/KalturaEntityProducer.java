/*
 * Copyright ©2016 Kaltura, Inc.
 */
package org.sakaiproject.kaltura.services;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;
import org.sakaiproject.component.api.ServerConfigurationService;
import org.sakaiproject.component.cover.ComponentManager;
import org.sakaiproject.entity.api.*;
import org.sakaiproject.kaltura.api.dao.KalturaSiteCopyBatchDao;
import org.sakaiproject.kaltura.api.dao.KalturaSiteCopyJobDao;
import org.sakaiproject.kaltura.models.dao.KalturaSiteCopyBatch;
import org.sakaiproject.kaltura.models.dao.KalturaSiteCopyJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.*;

/**
 * Implemented to support Site Copy Logic on kaltura side
 * 
 * @author Esh Nagappan (ynagappan@unicon.net)
 */
@SuppressWarnings({"unchecked","rawtypes"})
public class KalturaEntityProducer implements EntityProducer {

    private static final Logger log = LoggerFactory.getLogger(KalturaEntityProducer.class);
    /**
     * The XML root of the archive MUST match the actual name of the entity producer service
     * for this content type. It will be fetched using: (EntityProducer) ComponentManager.get({xml_root_element});
     * NOTE: xml_root_element will actually be the element inside the archive element
     */
    public static final String XML_ROOT = KalturaEntityProducer.class.getName(); //"kaltura";

    public static final String KALTURA = "kaltura";
    public static final String REFERENCE_ROOT = Entity.SEPARATOR + KALTURA;

    private KalturaSiteCopyBatchDao kalturaSiteCopyBatchDao = null;
    private KalturaSiteCopyJobDao kalturaSiteCopyJobDao = null;

    private EntityManager entityManager;
    public void setEntityManager(EntityManager em) {
        entityManager = em;
    }

    private KalturaLTIService service;
    public void setService(KalturaLTIService kalturaLTIService) {
        this.service = kalturaLTIService;
    }

    private ServerConfigurationService serverConfigurationService;
    public void setServerConfigurationService(ServerConfigurationService serverConfigurationService) {
        this.serverConfigurationService = serverConfigurationService;
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

    public void init() {
        if (log.isDebugEnabled()) {
            log.debug("kaltura EP.init()");
        }

        try {
            entityManager.registerEntityProducer(this, REFERENCE_ROOT);
            log.info("Registered kaltura entity producer as: " + REFERENCE_ROOT);

            // now verify if we are good to go
            if (ComponentManager.get(KalturaEntityProducer.class.getName()) != null) {
                log.debug("Found "+KalturaEntityProducer.class.getName() +" in the ComponentManager");
            } else {
                log.debug("FAILED to insert and lookup "+KalturaEntityProducer.class.getName()+" in the Sakai ComponentManager, archive imports for kaltura will not work");
            }
        } catch (Exception ex) {
            log.warn("kaltura EP.init(): " + ex, ex);
        }
    }

    /*
     * EntityProducer Methods
     */

    /* (non-Javadoc)
     * @see org.sakaiproject.entity.api.EntityProducer#getLabel()
     */
    public String getLabel() {
        return KALTURA; // this will define the filename used during archive (among other things)
    }

    /* (non-Javadoc)
     * @see org.sakaiproject.entity.api.EntityProducer#willArchiveMerge()
     */
    public boolean willArchiveMerge() {
        /*
         * we only do the merge If archive support is enabled
         */
        return serverConfigurationService.getBoolean("kaltura.archive.support.enabled", false);
    }

    /* (non-Javadoc)
     * @see org.sakaiproject.entity.api.EntityProducer#archive(java.lang.String, org.w3c.dom.Document, java.util.Stack, java.lang.String, java.util.List)
     */
    public String archive(String siteId, Document doc, Stack stack, String archivePath, List attachments) {
        if (log.isDebugEnabled()) {
            log.debug("kaltura EP.archive(siteId=" + siteId + ", doc=" + doc + ", stack=" + stack + ", archivePath=" + archivePath + ", attachments=" + attachments + ")");
        }

        if (!willArchiveMerge()) {
            if (log.isDebugEnabled()) {
                log.debug("Skipping archive for kaltura as archive support is not enabled");
            }

            return null;
        }

        String result = "kaltura: dummy archive xml created to support merge";
        Element rootElement = doc.createElement(XML_ROOT);
        ((Element) stack.peek()).appendChild(rootElement);
        stack.push(rootElement);
        stack.pop();
 
        if (log.isDebugEnabled()) {
            log.debug("archive: "+result);
        }

        return result;
    }

    /* (non-Javadoc)
     * @see org.sakaiproject.entity.api.EntityProducer#merge(java.lang.String, org.w3c.dom.Element, java.lang.String, java.lang.String, java.util.Map, java.util.Map, java.util.Set)
     */
    public String merge(String siteId, Element root, String archivePath, String fromSiteId, Map attachmentNames, Map userIdTrans, Set userListAllowImport) {
        if (log.isDebugEnabled()){
             log.debug("kaltura EP.merge(siteId="+siteId+", fromSiteId="+fromSiteId+", archivePath="+archivePath+", userIdTrans="+userIdTrans+", userListAllowImport="+userListAllowImport+")");
        }

        StringBuilder results = new StringBuilder();
        results.append("Starting kaltura merge");

        if (!willArchiveMerge()) {
            log.debug("kaltura tool site archive support is not enabled so no content will be merged from archive for site ("+fromSiteId+"), use 'kaltura.archive.support.enabled = true' to enable");
            results.append("no kaltura data merged from archive for location "+ fromSiteId + ", archiving support disabled\n");
        } else {
            // call KAF endpoint to do lti request to initiate copy on kaltura end       
            if (service != null) {
                String module ="copy-course";
                Properties ltiProps = service.prepareSiteCopyRequest(module,fromSiteId,siteId);

                try{
                    String launchUrl = serverConfigurationService.getString("kaltura.host") + "/hosted/index";
                    launchUrl += "/" + module;
                    log.debug("Kaltura Merge() - Sending a POST request to " + launchUrl + " to initiate copy of kaltura media items from " + fromSiteId + " to " + siteId);
                    HttpClient client = new DefaultHttpClient();
                    HttpPost post = new HttpPost(launchUrl);
                    List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
                    StringBuffer nameValues = new StringBuffer();

                    for(Object lkey : ltiProps.keySet()) {
                        String ltiKey = (String) lkey;
                        String ltiValue = ltiProps.getProperty(ltiKey);
                        nameValues.append(ltiKey+"="+ltiValue+"\n");
                        urlParameters.add(new BasicNameValuePair(ltiKey, ltiValue));
                    }

                    log.debug("Kaltura Site Copy Request - POST params sent are : " + nameValues.toString());
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
                    log.debug("Kaltura Merge() - Json string for site copy request from kaltura :" + line);
                    JSONObject copyStatus = new JSONObject(line);

                    if (copyStatus != null) {
                        if (!copyStatus.isNull("resultCode")) {
                            int resultCode = copyStatus.getInt("resultCode");
                            log.debug("Kaltura Merge() - Site Copy Json Response from Kaltura : resultcode on site copy request :"+ resultCode);
                        }

                        if (!copyStatus.isNull("message")) {
                            String message = copyStatus.getString("message");
                            log.debug("Kaltura Merge() - Site copy Json response from Kaltura: message from site copy request :"+ message);
                        }

                        if (!copyStatus.isNull("jobs")) {
                            JSONArray jobs = copyStatus.getJSONArray("jobs");
                            ArrayList<Long> jobids = new ArrayList<Long>();

                            for (int i=0;i < jobs.length(); i++) {
                                long jobId = jobs.getLong(i);
                                jobids.add(jobId);
                            }

                            if (jobids.size()>0) {
                                //create a batch job first with source site id, target site ids
                                KalturaSiteCopyBatch siteCopyBatch = new KalturaSiteCopyBatch(fromSiteId, siteId, KalturaSiteCopyBatch.NEW_STATUS);
                                Long batchId = kalturaSiteCopyBatchDao.save(siteCopyBatch, false);

                                if (batchId!=null) {
                                    log.debug("Kaltura Merge() - Added record to Kaltura_site_copy_batch:"+ batchId.toString());
                                    // now insert kaltura jobs for each batch
 
                                    for (Long kalturajobid : jobids) {
                                        KalturaSiteCopyJob kalturajob = new KalturaSiteCopyJob(batchId,kalturajobid, KalturaSiteCopyJob.NEW_STATUS);
                                        Long kalturaJobId = kalturaSiteCopyJobDao.save(kalturajob, false);

                                        if (kalturaJobId != null) {
                                            log.debug("Kaltura Merge() - Added record to kaltura_site_copy_job:"+ kalturaJobId.toString());
                                        }
                                    }
                                }
                            }
                        }
                    }
                }catch(Exception e){
                    log.error("Kaltura Merge() - Exception occured processing merge for Kaltura items", e);
                }
            }
        }

        String result = results.toString();
        log.debug("kaltura EP.merge result: " + result);

        return result;
    }


    /* (non-Javadoc)
     * @see org.sakaiproject.entity.api.EntityTransferrer#transferCopyEntities(java.lang.String, java.lang.String, java.util.List)
     */
    public void transferCopyEntities(String fromContext, String toContext, List ids) {
        if (log.isDebugEnabled()) {
            log.debug("kaltura EP.transferCopyEntities(fromContext=" + fromContext + ", toContext=" + toContext + ", ids=" + ids + ")");
        }

        transferCopyEntities(fromContext, toContext, ids, false);
    }

    /* (non-Javadoc)
     * @see org.sakaiproject.entity.api.EntityTransferrer#transferCopyEntities(java.lang.String, java.lang.String, java.util.List, boolean)
     */
    public void transferCopyEntities(String fromContext, String toContext, List ids, boolean cleanup) {
        if (log.isDebugEnabled()) {
            log.debug("kaltura EP.transferCopyEntities(fromContext=" + fromContext + ", toContext=" + toContext + ", ids=" + ids + ", cleanup=" + cleanup + ")");
        }
    }

    /* (non-Javadoc)
     * @see org.sakaiproject.entity.api.EntityProducer#parseEntityReference(java.lang.String, org.sakaiproject.entity.api.Reference)
     */
    public boolean parseEntityReference(String reference, Reference ref) {
        return false;
    }

    /* (non-Javadoc)
     * @see org.sakaiproject.entity.api.EntityProducer#getEntity(org.sakaiproject.entity.api.Reference)
     */
    public Entity getEntity(Reference ref) {
        if (log.isDebugEnabled()) {
            log.debug("kaltura EP.getEntity(ref=" + ref + ")");
        }

        return null;
    }

    public String getEntityUrl(Reference ref) {
        if (log.isDebugEnabled()) {
            log.debug("kaltura EP.getEntityUrl(ref=" + ref + ")");
        }

        return null;
    }

    public String getEntityDescription(Reference ref) {
        if (log.isDebugEnabled()) {
            log.debug("kaltura EP.getEntityDescription(ref=" + ref + ")");
        }

        return null;
    }

    public ResourceProperties getEntityResourceProperties(Reference ref) {
        if (log.isDebugEnabled()) {
            log.debug("kaltura EP.getEntityResourceProperties(ref=" + ref + ")");
        }

        return null;
    }

    public Collection getEntityAuthzGroups(Reference ref, String arg1) {
        if (log.isDebugEnabled()) {
            log.debug("kaltura EP.getEntityAuthzGroups(ref=" + ref + ", arg1=" + arg1 + ")");
        }

        return null;
    }

    public HttpAccess getHttpAccess() {
        if (log.isDebugEnabled()) {
            log.debug("kaltura EP.getHttpAccess()");
        }

        return null;
    }

}
