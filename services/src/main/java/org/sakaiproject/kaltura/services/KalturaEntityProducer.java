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

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.Properties;
import java.util.HashMap;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

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

import org.sakaiproject.component.cover.ComponentManager;
import org.sakaiproject.entity.api.Entity;
import org.sakaiproject.entity.api.EntityManager;
import org.sakaiproject.entity.api.EntityProducer;
import org.sakaiproject.entity.api.EntityTransferrer;
import org.sakaiproject.entity.api.HttpAccess;
import org.sakaiproject.entity.api.Reference;
import org.sakaiproject.entity.api.ResourceProperties;
import org.sakaiproject.kaltura.services.KalturaLTIService;
import org.sakaiproject.component.api.ServerConfigurationService;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.apache.commons.lang.StringUtils;
import org.sakaiproject.kaltura.api.dao.KalturaSiteCopyBatchDao;
import org.sakaiproject.kaltura.models.dao.KalturaSiteCopyBatch;
import org.sakaiproject.kaltura.api.dao.KalturaSiteCopyJobDao;
import org.sakaiproject.kaltura.models.dao.KalturaSiteCopyJob;

/**
 * Implemented to support Site Copy Logic on kaltura side
 * 
 * @author Esh Nagappan (ynagappan@unicon.net)
 */
@SuppressWarnings({"unchecked","rawtypes"})
public class KalturaEntityProducer implements EntityProducer {

    private static Log log = LogFactory.getLog(KalturaEntityProducer.class);
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
        if (log.isDebugEnabled()) log.debug("kaltura EP.init()");
        try {
            entityManager.registerEntityProducer(this, REFERENCE_ROOT);
            log.info("Registered kaltura entity producer as: "+ REFERENCE_ROOT);
            // now verify if we are good to go
            if (ComponentManager.get(KalturaEntityProducer.class.getName()) != null) {
                log.debug("Found "+KalturaEntityProducer.class.getName()+" in the ComponentManager");
            } else {
                log.debug("FAILED to insert and lookup "+KalturaEntityProducer.class.getName()+" in the Sakai ComponentManager, archive imports for kaltura will not work");
            }
        } catch (Exception ex) {
            log.warn("kaltura EP.init(): "+ex, ex);
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
        boolean merge = false;
        merge = serverConfigurationService.getBoolean("kaltura.archive.support.enabled",false);
        return merge;
    }

    /* (non-Javadoc)
     * @see org.sakaiproject.entity.api.EntityProducer#archive(java.lang.String, org.w3c.dom.Document, java.util.Stack, java.lang.String, java.util.List)
     */
    public String archive(String siteId, Document doc, Stack stack, String archivePath, List attachments) {
        if (log.isDebugEnabled()) log.debug("kaltura EP.archive(siteId="+siteId+", doc="+doc+", stack="+stack+", archivePath="+archivePath+", attachments="+attachments+")");
        if(!willArchiveMerge()){
            if(log.isDebugEnabled()) log.debug("Skipping archive for kaltura as archive support is not enabled");
            return null;
        }
        String result = "kaltura: dummy archive xml created to support merge";
        Element rootElement = doc.createElement(XML_ROOT);
        ((Element) stack.peek()).appendChild(rootElement);
        stack.push(rootElement);
        stack.pop();
        if (log.isDebugEnabled()) log.debug("archive: "+result);
        return result;
    }

    /* (non-Javadoc)
     * @see org.sakaiproject.entity.api.EntityProducer#merge(java.lang.String, org.w3c.dom.Element, java.lang.String, java.lang.String, java.util.Map, java.util.Map, java.util.Set)
     */
    public String merge(String siteId, Element root, String archivePath, String fromSiteId,
            Map attachmentNames, Map userIdTrans, Set userListAllowImport) {
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
            if(service!=null){
                String module ="copy-course";
                Properties ltiProps = service.prepareSiteCopyRequest(module,fromSiteId,siteId);
                try{
                    String launch_url = serverConfigurationService.getString("kaltura.launch.url");
                    launch_url=launch_url+"/"+ module;
                    log.debug("Kaltura Merge() - Sending a POST request to "+ launch_url + " to initiate copy of kaltura media items from "+ fromSiteId + " to "+ siteId);
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
                    log.debug("Kaltura Merge() - Json string for site copy request from kaltura :" + jsonString);
                    JSONObject copyStatus = new JSONObject(jsonString);
                    if(copyStatus!=null){
                        int resultCode = copyStatus.getInt("resultCode");
                        log.debug("Kaltura Merge() - Site Copy Json Response from Kaltura : resultcode on site copy request :"+ resultCode);
                        String message = copyStatus.getString("message");
                        if(message!=null){
                            log.debug("Kaltura Merge() - Site copy Json response from Kaltura: message from site copy request :"+ message);

                        }
                        JSONArray jobs = copyStatus.getJSONArray("jobs");
                        ArrayList<Long> jobids = new ArrayList<Long>();
                        for(int i=0;i < jobs.length(); i++){
                            long jobId = jobs.getLong(i);
                            System.out.println("Job Id from kaltura :" + jobId);
                            jobids.add(new Long(jobId));
                        }
                        if(jobids.size()>0){
                            //create a batch job first with source site id, target site ids
                            KalturaSiteCopyBatch siteCopyBatch = new KalturaSiteCopyBatch(fromSiteId, siteId,KalturaSiteCopyBatch.NEW_STATUS);
                            Long batchId = kalturaSiteCopyBatchDao.save(siteCopyBatch, false);
                            if(batchId!=null){
                                log.debug("Kaltura Merge() - Added record to Kaltura_site_copy_batch_details:"+ batchId.toString());
                                // now insert kaltura jobs for each batch
                                for(Long kalturajobid:jobids){
                                    KalturaSiteCopyJob kalturajob = new KalturaSiteCopyJob(batchId,kalturajobid, KalturaSiteCopyJob.NEW_STATUS);
                                    Long kalturaJobId = kalturaSiteCopyJobDao.save(kalturajob, false);
                                    if(kalturaJobId!=null){
                                        log.debug("Kaltura Merge() - Added record to kaltura_site_copy_job:"+ kalturaJobId.toString());
                                    }
                                }// end for

                            }// end if
                        } // end if 
                    }
                }catch(Exception e){
                    log.error("Kaltura Merge() - Exception occured processing merge for Kaltura items");
                    e.printStackTrace();
                }


            }
        }

        String result = results.toString();
        log.debug("kaltura EP.merge result: "+result);
        return result;
    }


    /* (non-Javadoc)
     * @see org.sakaiproject.entity.api.EntityTransferrer#transferCopyEntities(java.lang.String, java.lang.String, java.util.List)
     */
    public void transferCopyEntities(String fromContext, String toContext, List ids) {
        if (log.isDebugEnabled()) log.debug("kaltura EP.transferCopyEntities(fromContext="+fromContext+", toContext="+toContext+", ids="+ids+")");
        transferCopyEntities(fromContext, toContext, ids, false);
    }

    /* (non-Javadoc)
     * @see org.sakaiproject.entity.api.EntityTransferrer#transferCopyEntities(java.lang.String, java.lang.String, java.util.List, boolean)
     */
    public void transferCopyEntities(String fromContext, String toContext, List ids, boolean cleanup) {
        if (log.isDebugEnabled()) log.debug("kaltura EP.transferCopyEntities(fromContext="+fromContext+", toContext="+toContext+", ids="+ids+", cleanup="+cleanup+")");
    }

    /* (non-Javadoc)
     * @see org.sakaiproject.entity.api.EntityProducer#parseEntityReference(java.lang.String, org.sakaiproject.entity.api.Reference)
     */
    public boolean parseEntityReference(String reference, Reference ref) {
       // if (log.isDebugEnabled()) log.debug("kaltura EP.parseEntityReference(reference="+reference+", ref="+ref+")");
        //not needed
        return false;
    }

    /* (non-Javadoc)
     * @see org.sakaiproject.entity.api.EntityProducer#getEntity(org.sakaiproject.entity.api.Reference)
     */
    public Entity getEntity(Reference ref) {
        if (log.isDebugEnabled()) log.debug("kaltura EP.getEntity(ref="+ref+")");
        //not needed
        return null;
    }

    public String getEntityUrl(Reference ref) {
        if (log.isDebugEnabled()) log.debug("kaltura EP.getEntityUrl(ref="+ref+")");
        // not needed
        return null;
    }

    public String getEntityDescription(Reference ref) {
        if (log.isDebugEnabled()) log.debug("kaltura EP.getEntityDescription(ref="+ref+")");
        // not needed
        return null;
    }

    public ResourceProperties getEntityResourceProperties(Reference ref) {
        if (log.isDebugEnabled()) log.debug("kaltura EP.getEntityResourceProperties(ref="+ref+")");
        // not needed
        return null;
    }

    public Collection getEntityAuthzGroups(Reference ref, String arg1) {
        if (log.isDebugEnabled()) log.debug("kaltura EP.getEntityAuthzGroups(ref="+ref+", arg1="+arg1+")");
        // not needed
        return null;
    }

    public HttpAccess getHttpAccess() {
        if (log.isDebugEnabled()) log.debug("kaltura EP.getHttpAccess()");
        // not needed
        return null;
    }

}
