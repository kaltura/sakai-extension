/*
 * Copyright ©2016 Kaltura, Inc.
 */
package org.sakaiproject.kaltura.services;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.Stack;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;
import org.sakaiproject.component.api.ServerConfigurationService;
import org.sakaiproject.component.cover.ComponentManager;
import org.sakaiproject.entity.api.Entity;
import org.sakaiproject.entity.api.EntityManager;
import org.sakaiproject.entity.api.EntityProducer;
import org.sakaiproject.entity.api.EntityTransferrer;
import org.sakaiproject.entity.api.HttpAccess;
import org.sakaiproject.entity.api.Reference;
import org.sakaiproject.entity.api.ResourceProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Implemented to support Site Copy Logic on kaltura side
 *
 * @author Esh Nagappan (ynagappan@unicon.net)
 */
@SuppressWarnings({"unchecked", "rawtypes"})
public class KalturaEntityProducer implements EntityProducer, EntityTransferrer {

    private static final Logger log = LoggerFactory.getLogger(KalturaEntityProducer.class);
    /**
     * The XML root of the archive MUST match the actual name of the entity producer service
     * for this content type. It will be fetched using: (EntityProducer) ComponentManager.get({xml_root_element});
     * NOTE: xml_root_element will actually be the element inside the archive element
     */
    public static final String XML_ROOT = KalturaEntityProducer.class.getName(); //"kaltura";

    public static final String KALTURA = "kaltura";
    public static final String REFERENCE_ROOT = Entity.SEPARATOR + KALTURA;

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

    public void init() {
        if (log.isDebugEnabled()) {
            log.debug("kaltura EP.init()");
        }

        try {
            entityManager.registerEntityProducer(this, REFERENCE_ROOT);
            log.info("Registered kaltura entity producer as: " + REFERENCE_ROOT);

            // now verify if we are good to go
            if (ComponentManager.get(KalturaEntityProducer.class.getName()) != null) {
                log.debug("Found " + KalturaEntityProducer.class.getName() + " in the ComponentManager");
            } else {
                log.debug("FAILED to insert and lookup " + KalturaEntityProducer.class.getName() + " in the Sakai ComponentManager, archive imports for kaltura will not work");
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
            log.debug("archive: " + result);
        }

        return result;
    }

    /* (non-Javadoc)
     * @see org.sakaiproject.entity.api.EntityProducer#merge(java.lang.String, org.w3c.dom.Element, java.lang.String, java.lang.String, java.util.Map, java.util.Map, java.util.Set)
     */
    public String merge(String siteId, Element root, String archivePath, String fromSiteId, Map attachmentNames, Map userIdTrans, Set userListAllowImport) {
        log.debug("kaltura EP.merge(siteId=" + siteId + ", fromSiteId=" + fromSiteId + ", archivePath=" + archivePath + ", userIdTrans=" + userIdTrans + ", userListAllowImport=" + userListAllowImport + ")");

        StringBuilder results = new StringBuilder();
        results.append("Starting kaltura merge");

        if (!willArchiveMerge()) {
            log.debug("kaltura tool site archive support is not enabled so no content will be merged from archive for site ({}), use 'kaltura.archive.support.enabled = true' to enable", fromSiteId);
            results.append("no kaltura data merged from archive for location " + fromSiteId + ", archiving support disabled\n");
        } else {
            List<String> copyResults = copySiteToSite(siteId, fromSiteId);
            results.append(String.join("\n", copyResults));
        }

        String result = results.toString();
        log.debug("kaltura EP.merge result: " + result);

        return result;
    }


    /* (non-Javadoc)
     * @see org.sakaiproject.entity.api.EntityTransferrer#transferCopyEntities(java.lang.String, java.lang.String, java.util.List)
     */
    public void transferCopyEntities(String fromContext, String toContext, List ids) {
        log.debug("kaltura EP.transferCopyEntities(fromContext={}, toContext={}, ids={})", fromContext, toContext, ids);
        transferCopyEntities(fromContext, toContext, ids, false);
    }

    /* (non-Javadoc)
     * @see org.sakaiproject.entity.api.EntityTransferrer#transferCopyEntities(java.lang.String, java.lang.String, java.util.List, boolean)
     */
    public void transferCopyEntities(String fromContext, String toContext, List ids, boolean cleanup) {
        log.debug("kaltura EP.transferCopyEntities(fromContext={}, toContext={}, ids={}, cleanup={}", fromContext, toContext, ids, cleanup);
        if (serverConfigurationService.getBoolean("kaltura.site.import.enabled", true)) {
            copySiteToSite(toContext, fromContext);
        } else {
            log.warn("kaltura EP.transferCopyEntities is disabled");
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
        log.debug("kaltura EP.getEntity(ref=" + ref + ")");

        return null;
    }

    public String getEntityUrl(Reference ref) {
        log.debug("kaltura EP.getEntityUrl(ref=" + ref + ")");

        return null;
    }

    public String getEntityDescription(Reference ref) {
        log.debug("kaltura EP.getEntityDescription(ref=" + ref + ")");

        return null;
    }

    public ResourceProperties getEntityResourceProperties(Reference ref) {
        log.debug("kaltura EP.getEntityResourceProperties(ref=" + ref + ")");

        return null;
    }

    public Collection getEntityAuthzGroups(Reference ref, String arg1) {
        log.debug("kaltura EP.getEntityAuthzGroups(ref=" + ref + ", arg1=" + arg1 + ")");

        return null;
    }

    public HttpAccess getHttpAccess() {
        log.debug("kaltura EP.getHttpAccess()");

        return null;
    }

    @Override
    public String[] myToolIds() {
        return new String[]{"kaltura.media"};
    }

    private List<String> copySiteToSite(String toSiteId, String fromSiteId) {
        List<String> results = new ArrayList<>();
        log.debug("kaltura copy toSiteId={}, fromSiteId={}", toSiteId, fromSiteId);
        results.add("kaltura copy start");
        results.add(String.format("kaltura copy toSiteId=%1s, fromSiteId=%2s", toSiteId, fromSiteId));

        // call KAF endpoint to do lti request to initiate copy on kaltura end
        if (service != null) {
            String module = "copy-course";
            Properties ltiProps = service.prepareSiteCopyRequest(module, fromSiteId, toSiteId);

            try {
                String launchUrl = ltiProps.getProperty("launch_url");
                HttpClient client = null;
                if (serverConfigurationService.getBoolean("kaltura.site.import.ssl.hostname.verify", true)) {
                    client = HttpClientBuilder.create().build();
                } else {
                    client = HttpClientBuilder.create().setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE).build();
                }
                HttpPost post = new HttpPost(launchUrl);
                List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
                StringBuffer nameValues = new StringBuffer();

                for (Object key : ltiProps.keySet()) {
                    String ltiKey = (String) key;
                    String ltiValue = ltiProps.getProperty(ltiKey);
                    nameValues.append(ltiKey + "=" + ltiValue + "\n");
                    urlParameters.add(new BasicNameValuePair(ltiKey, ltiValue));
                }

                log.debug("kaltura copy - POST params are:\n{}", nameValues.toString());
                results.add("kaltura copy - POST params are: " + nameValues.toString());
                post.setEntity(new UrlEncodedFormEntity(urlParameters));

                log.debug("kaltura copy - http POST url: {}", post);
                results.add("kaltura copy - http POST url: " + post);
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
                log.debug("kaltura copy - received JSON string from kaltura service: {}", line);
                JSONObject status = new JSONObject(line);

                if (status != null) {
                    if (!status.isNull("resultCode")) {
                        log.debug("kaltura copy - JSON response from kaltura service: result code = {}", status.getInt("resultCode"));
                        results.add("kaltura copy - JSON response from kaltura service: result code = " + status.getInt("resultCode"));
                    }

                    if (!status.isNull("message")) {
                        log.debug("kaltura copy - JSON response from kaltura service: message = {}", status.getString("message"));
                        results.add("kaltura copy - JSON response from kaltura service: message = {}" + status.getString("message"));
                    }

                    if (!status.isNull("jobs")) {
                        JSONArray jobs = status.getJSONArray("jobs");
                        ArrayList<Long> jobids = new ArrayList<Long>();

                        for (int i = 0; i < jobs.length(); i++) {
                            long jobId = jobs.getLong(i);
                            jobids.add(jobId);
                        }

                        if (!jobids.isEmpty()) {
                            //create a batch job first with source site id, target site ids
                            for (Long jobid : jobids) {
                                log.debug("kaltura copy - kaltura job created: {}", jobid);
                                results.add("kaltura copy - kaltura job created: " + jobid);
                            }
                        }
                    }
                }
            } catch (Exception e) {
                log.error("kaltura copy - exception occured: {}", e.getMessage(), e);
                results.add("kaltura copy - exception occured: " + e.getMessage());
            }
        }
        results.add("kaltura copy end");
        return results;
    }
}
