/*
 * Copyright ©2016 Kaltura, Inc.
 */
package org.sakaiproject.kaltura.services;

import java.util.Arrays;
import java.util.Properties;
import java.util.Map;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sakaiproject.user.api.User;
import org.sakaiproject.user.api.UserNotDefinedException;
import org.sakaiproject.user.api.UserDirectoryService;
import org.sakaiproject.component.api.ServerConfigurationService;
import org.sakaiproject.tool.api.ToolManager;
import org.sakaiproject.site.api.Site;
import org.sakaiproject.site.api.SiteService;
import org.sakaiproject.tool.api.Session;
import org.sakaiproject.tool.api.SessionManager;
import org.sakaiproject.util.ResourceLoader;
import org.sakaiproject.util.Web;
import org.sakaiproject.authz.api.SecurityService;
import org.sakaiproject.kaltura.Constants;
import org.tsugi.basiclti.BasicLTIConstants;
import org.tsugi.basiclti.BasicLTIUtil;

public class KalturaLTIService {
    private static final Logger LOG = LoggerFactory.getLogger(KalturaLTIService.class);

    private static ResourceLoader rb = new ResourceLoader("basiclti");
    public static final String LTI_SECRET =    "secret";
    public static final String LTI_NEWPAGE =   "newpage";
    public static final String LTI_DEBUG = "debug";
    public static final String LTI_FRAMEHEIGHT = "frameheight";
    public static final String LTI1_PATH = "/imsblis/service/";
    public static final String CKEDITOR_REQUEST="ckeditor";
    public static final String LAUNCH_MEDIA = "launchmedia";

    private AuthCodeService authCodeService;
    public void setAuthCodeService(AuthCodeService authCodeService) {
        this.authCodeService = authCodeService;
    }

    private SecurityService securityService;
    public void setSecurityService(SecurityService securityService) {
        this.securityService = securityService;
    }

    private ServerConfigurationService serverConfigurationService;
    public void setServerConfigurationService(ServerConfigurationService serverConfigurationService) {
        this.serverConfigurationService = serverConfigurationService;
    }

    private SessionManager sessionManager;
    public void setSessionManager(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    private SiteService siteService;
    public void setSiteService(SiteService siteService) {
        this.siteService = siteService;
    }

    private ToolManager toolManager;
    public void setToolManager(ToolManager toolManager) {
        this.toolManager = toolManager;
    }

    private UserDirectoryService userDirectoryService;
    public void setUserDirectoryService(UserDirectoryService userDirectoryService) {
        this.userDirectoryService = userDirectoryService;
    }

    public String[] launchLTIRequest(String module) {
        User user = userDirectoryService.getCurrentUser();

        String siteId = toolManager.getCurrentPlacement().getContext();
        String placementId = toolManager.getCurrentPlacement().getId();

        return launchLTIRequest(module, user, placementId, siteId);
    }

    public String[] launchLTIRequest(String module, User user, String placementId, String siteId){
        String userId = Constants.DEFAULT_ANONYMOUS_USER_ID;

        if (user != null) {
            userId = user.getId();
        }

        // Start building up the properties
        Properties ltiProps = initLTIProps(user, siteId);
        Properties toolProps = new Properties();

        // Add key and secret
        String key = serverConfigurationService.getString("kaltura.launch.key");
        String secret = serverConfigurationService.getString("kaltura.launch.secret");
        setProperty(toolProps, LTI_SECRET, secret );
        setProperty(toolProps, "key", key );
        
        String launchUrl = serverConfigurationService.getString("kaltura.host") + "/hosted/index";

        if(StringUtils.isNotBlank(module)){
            launchUrl += "/" + module;
        }

        setProperty(toolProps, "launch_url", launchUrl);
        setDefaultReturnUrl(ltiProps, siteId);
        setProperty(ltiProps, BasicLTIConstants.RESOURCE_LINK_ID,placementId);

        if (rb != null) {
            setProperty(ltiProps, BasicLTIConstants.LAUNCH_PRESENTATION_LOCALE, rb.getLocale().toString());
        }

        setRole(ltiProps, siteId);
        setAuthCode(ltiProps, userId);
        setDebugOption(toolProps,module);
        setWindowOption(toolProps);

        // Pull in all of the custom parameters
        for (Object okey : toolProps.keySet()) {
            String skey = (String) okey;

            if (!StringUtils.startsWith(skey, BasicLTIConstants.CUSTOM_PREFIX)) {
                continue;
            }

            String value = toolProps.getProperty(skey);

            if (value == null) {
                continue;
            }

            setProperty(ltiProps, skey, value);
        }

        Map<String,String> extra = new HashMap<String,String>();
        String orgGuid = serverConfigurationService.getString("basiclti.consumer_instance_guid", null);
        String org_desc = serverConfigurationService.getString("basiclti.consumer_instance_description", null);
        String org_url = serverConfigurationService.getString("basiclti.consumer_instance_url", null);
        ltiProps = BasicLTIUtil.signProperties(ltiProps, launchUrl, "POST", key, secret, orgGuid, org_desc, org_url, extra);

        if (ltiProps == null) {
            return postError("<p>Error signing message.</p>");
        }

        LOG.debug("LAUNCH III=" + ltiProps);

        String debugProperty = toolProps.getProperty(LTI_DEBUG);
        boolean dodebug = Arrays.asList(new String[]{"on", "1"}).contains(debugProperty);
        String postData = postLaunchHTML(ltiProps, launchUrl, "Press to Launch External Tool", dodebug, extra);
        String[] retval = {postData, launchUrl};

        return retval;
    }

    public String[] launchCKEditorRequest(String module, String userId, String siteId) {
        if (StringUtils.isBlank(userId)) {
            throw new IllegalArgumentException("User ID cannot be null.");
        }
        if (StringUtils.isBlank(siteId)) {
            throw new IllegalArgumentException("Site ID cannot be null.");
        }

        User user = null;

        try {
            user = userDirectoryService.getUser(userId);
        } catch (Exception e) {
            try {
                user = userDirectoryService.getUserByEid(userId);
            } catch (Exception e1) {
                LOG.warn("User not found with ID: {}", userId);
            }
        }

        if (user == null) {
            throw new IllegalArgumentException("User not found with ID: " + userId);
        }

        String placementId = "placementId123";

        return launchCKEditorRequest(module, user, placementId, siteId);
    }

    
    public String[] launchCKEditorRequest(String module, User user, String placementId, String siteId){
        // Start building up the properties
        Properties ltiProps = initLTIProps(user, siteId);
        Properties toolProps = new Properties();

        // Add key and secret
        String key = serverConfigurationService.getString("kaltura.launch.key");
        String secret = serverConfigurationService.getString("kaltura.launch.secret");
        setProperty(toolProps, LTI_SECRET, secret);
        setProperty(toolProps, "key", key );

        // TODO handle null result
        String ckeditorUrl = serverConfigurationService.getString("kaltura.host") + "/browseandembed/index/browseandembed";
        String serverUrl = serverConfigurationService.getServerUrl();
        String ckeditorCallbackUrl = serverUrl + "/media-gallery-tool/ckeditorcallback.htm";
        LOG.info("ckeditorCallbackUrl: [" + ckeditorCallbackUrl + "]");
        
        setProperty(ltiProps, BasicLTIConstants.LAUNCH_PRESENTATION_RETURN_URL, ckeditorCallbackUrl);
        setProperty(toolProps, "launch_url", ckeditorUrl);
        setProperty(ltiProps,BasicLTIConstants.RESOURCE_LINK_ID,placementId);

        if (rb != null) {
            setProperty(ltiProps, BasicLTIConstants.LAUNCH_PRESENTATION_LOCALE, rb.getLocale().toString());
        }

        setAuthCode(ltiProps, (user != null ? user.getId() : Constants.DEFAULT_ANONYMOUS_USER_ID));
        setRole(ltiProps, siteId);
        setDebugOption(toolProps , CKEDITOR_REQUEST);
        setWindowOption(toolProps);

        // Pull in all of the custom parameters
        for(Object okey : toolProps.keySet() ) {
            String skey = (String) okey;

            if (!StringUtils.startsWith(skey, BasicLTIConstants.CUSTOM_PREFIX)) {
                continue;
            }


            String value = toolProps.getProperty(skey);
            if (value == null) {
                continue;
            }

            setProperty(ltiProps, skey, value);
        }

        Map<String,String> extra = new HashMap<String,String>();
        String orgGuid = serverConfigurationService.getString("basiclti.consumer_instance_guid",null);
        String orgDesc = serverConfigurationService.getString("basiclti.consumer_instance_description",null);
        String orgUrl = serverConfigurationService.getString("basiclti.consumer_instance_url",null);
        ltiProps = BasicLTIUtil.signProperties(ltiProps, ckeditorUrl, "POST", key, secret, orgGuid, orgDesc, orgUrl, extra);

        if (ltiProps == null) {
            return postError("<p>Error signing message.</p>");
        }

        LOG.debug("LAUNCH III=" + ltiProps);

        String debugProperty = toolProps.getProperty(LTI_DEBUG);
        boolean dodebug = Arrays.asList(new String[]{"on", "1"}).contains(debugProperty);
        String postData = postLaunchHTML(ltiProps, ckeditorUrl, "Press to Launch External Tool", dodebug, extra);
        String[] retval = {postData, ckeditorUrl};

        return retval;
    }

    public String[] launchLTIDisplayRequest(String module, String userId, String siteId) {
        User user = null;

        try {
            if (StringUtils.isNotBlank(userId)) {
                user = userDirectoryService.getUser(userId);
            }
        } catch (UserNotDefinedException e) {
            LOG.error("User not found with ID: " + userId, e);
        }

        String placementId = "placementId123";

        return launchLTIDisplayRequest(module, user, siteId, placementId);
    }

    public String[] launchLTIDisplayStaticRequest(String entryId, String userId, String siteId) {
        User user = null;

        try {
            if (StringUtils.isNotBlank(userId)) {
                user = userDirectoryService.getUser(userId);
            }
        } catch (UserNotDefinedException e1) {
            LOG.error("User not found with ID: " + userId, e1);
        }

        String placementId = "placementId123";
        String kalturaHost = serverConfigurationService.getString("kaltura.host");
        String playerSize = serverConfigurationService.getString("kaltura.media.static.playersize");
        String entryUrl = kalturaHost + "/browseandembed/index/media" +
            "/entryid/" + entryId +
            "/showDescription/false" +
            "/showTitle/false" +
            "/showTags/false" +
            "/showDuration/false" +
            "/showOwner/false" +
            "/showUploadDate/false" +
            "/playerSize/" + playerSize;

        return launchLTIDisplayRequest(entryUrl, user, siteId, placementId);
    }

    /**
     * given a media item URL, initiates an LTI call to begin a session (if necessary) and return the 
     * html to render the media item in an iFrame
     * @return
     */
    public String[] launchLTIDisplayRequest(String launchUrl, User user, String siteId, String placementId) {
        LOG.debug("launch_url: [" + launchUrl + "], " +
            "user: [" + (user != null ? user.getEid() : "user is null") + "], " +
            "siteId: [" + siteId + "], " +
            "placementId: [" + placementId + "]");

        String userId = Constants.DEFAULT_ANONYMOUS_USER_ID;

        if (user != null) {
            userId = user.getId();
        }

        // Start building up the properties
        Properties ltiProps = initLTIProps(user, siteId);
        Properties toolProps = new Properties();

        // Add key and secret
        String key = serverConfigurationService.getString("kaltura.launch.key");
        String secret = serverConfigurationService.getString("kaltura.launch.secret");
        setProperty(toolProps, LTI_SECRET, secret );
        setProperty(toolProps, "key", key );
        setProperty(toolProps, "launch_url", launchUrl);

        setProperty(ltiProps,BasicLTIConstants.RESOURCE_LINK_ID,placementId);

        if (rb != null) {
            setProperty(ltiProps, BasicLTIConstants.LAUNCH_PRESENTATION_LOCALE, rb.getLocale().toString());
        }

        setAuthCode(ltiProps, userId);
        setRole(ltiProps, siteId);
        setDebugOption(toolProps,LAUNCH_MEDIA);
        setWindowOption(toolProps);

        // Pull in all of the custom parameters
        for(Object okey : toolProps.keySet()) {
            String skey = (String) okey;
            if (!StringUtils.startsWith(skey, BasicLTIConstants.CUSTOM_PREFIX)) {
                continue;
            }

            String value = toolProps.getProperty(skey);

            if (value == null) {
                continue;
            }

            setProperty(ltiProps, skey, value);
        }

        Map<String,String> extra = new HashMap<String,String> ();        
        
        String orgGuid = serverConfigurationService.getString("basiclti.consumer_instance_guid", null);
        String orgDesc = serverConfigurationService.getString("basiclti.consumer_instance_description", null);
        String orgUrl = serverConfigurationService.getString("basiclti.consumer_instance_url", null);

        ltiProps = BasicLTIUtil.signProperties(ltiProps, launchUrl, "POST", key, secret, orgGuid, orgDesc, orgUrl, extra);

        if (ltiProps == null) {
            return postError("<p>Error signing message.</p>");
        }

        LOG.debug("LAUNCH III="+ltiProps);

        String debugProperty = toolProps.getProperty(LTI_DEBUG);
        boolean dodebug = Arrays.asList(new String[]{"on", "1"}).contains(debugProperty);

        String postData = postLaunchHTML(ltiProps, launchUrl, "Press to Launch External Tool", dodebug, extra);

        String[] retval = {postData, launchUrl};

        return retval;
    }

    public Properties initLTIProps(User user, String siteId) {
        Properties ltiProps = new Properties();
        setProperty(ltiProps,BasicLTIConstants.LTI_VERSION,BasicLTIConstants.LTI_VERSION_1);
        // KAF required LTI launch parameters-http://knowledge.kaltura.com/understanding-kaltura-application-framework-kaf#auth
        String sakaiVersion = serverConfigurationService.getString("version.sakai", "2");
        setProperty(ltiProps,"ext_lms", "sakai-" + sakaiVersion);
        setProperty(ltiProps, BasicLTIConstants.TOOL_CONSUMER_INFO_PRODUCT_FAMILY_CODE, "sakai");
        setProperty(ltiProps, BasicLTIConstants.TOOL_CONSUMER_INFO_VERSION, sakaiVersion);

        // add user info
        if (user != null) {
            setProperty(ltiProps,BasicLTIConstants.USER_ID,user.getId());
            setProperty(ltiProps,BasicLTIConstants.LIS_PERSON_SOURCEDID,user.getEid());
            setProperty(ltiProps,BasicLTIConstants.LIS_PERSON_NAME_GIVEN,user.getFirstName());
            setProperty(ltiProps,BasicLTIConstants.LIS_PERSON_NAME_FAMILY,user.getLastName());
            setProperty(ltiProps,BasicLTIConstants.LIS_PERSON_NAME_FULL,user.getDisplayName());
            setProperty(ltiProps,BasicLTIConstants.LIS_PERSON_CONTACT_EMAIL_PRIMARY,user.getEmail());
            setProperty(ltiProps,BasicLTIConstants.EXT_SAKAI_PROVIDER_EID, user.getEid());
            // Only send the display ID if it's different to the EID.
            if (!StringUtils.equalsIgnoreCase(user.getEid(), user.getDisplayId())) {
                LOG.info("eid:displayId: [" + user.getEid() + ":" + user.getDisplayId() + "]");
                setProperty(ltiProps,BasicLTIConstants.EXT_SAKAI_PROVIDER_DISPLAYID,user.getDisplayId());
            }
        }

        //add site info
        Site site =  null;

        try {
            site = siteService.getSite(siteId);
        } catch(Exception e) {
            LOG.warn("Site not found: {}", siteId);
        }

        if (site != null) {
            setProperty(ltiProps, BasicLTIConstants.CONTEXT_ID, site.getId());
            setProperty(ltiProps, BasicLTIConstants.CONTEXT_TITLE, site.getTitle());
        }

        // add oauth call back 
        String oauth_callback = serverConfigurationService.getString("basiclti.oauth_callback", null);
        // Too bad there is not a better default callback url for OAuth
        // Actually since we are using signing-only, there is really not much point 
        // In OAuth 6.2.3, this is after the user is authorized

        if (oauth_callback == null) {
            oauth_callback = "about:blank";
        }

        setProperty(ltiProps, "oauth_callback", oauth_callback);

        return ltiProps;
    }

    /**
     * Prepares LTI properties that need to be sent as POST parameters to initiate copy on kaltura server on sakai site import 
     * @param module - string indicating which module in kaltura is using this service
     * @param fromSiteId - sakai Site Id which is copied 
     * @param targetSiteId - sakai site Id to which kaltura media items are copied to
     * @return properties - Properties object holding LTI properties
     */
    public Properties prepareSiteCopyRequest(String module,String fromSiteId, String targetSiteId) {
        // get admin user
        User user = null;

        try {
            user = userDirectoryService.getUser("admin");
        } catch (UserNotDefinedException e1) {
            LOG.warn("admin user not found");
        }

        // Start building up the properties
        Properties ltiProps = initLTIProps(user, fromSiteId);
        Properties toolProps = new Properties();
        
        // Add key and secret
        String key = serverConfigurationService.getString("kaltura.launch.key");
        String secret = serverConfigurationService.getString("kaltura.launch.secret");
        setProperty(toolProps, LTI_SECRET, secret);
        setProperty(toolProps, "key", key);

        String launchUrl = serverConfigurationService.getString("kaltura.host") + "/hosted/index";

        if(StringUtils.isNotBlank(module)){
            launchUrl += "/" + module;
        }

        setProperty(ltiProps, "launch_url", launchUrl);

        setDefaultReturnUrl(ltiProps, fromSiteId);
        String theRole = "Instructor,Administrator,urn:lti:instrole:ims/lis/Administrator,urn:lti:sysrole:ims/lis/Administrator";
        setProperty(ltiProps, BasicLTIConstants.ROLES, theRole);

        String placementId ="copySitePlacement123";
        setProperty(ltiProps,BasicLTIConstants.RESOURCE_LINK_ID,placementId);

        if(!StringUtils.isBlank(targetSiteId)){
            Site targetSite =  null;

            try{
                targetSite = siteService.getSite(targetSiteId);
            }catch(Exception e){
                LOG.warn("Site not found: {}", targetSiteId);
            }

            setProperty(toolProps, "custom_copy_source_course_id", fromSiteId);
            setProperty(toolProps, "custom_copy_target_course_id", targetSiteId);
            
            if (targetSite != null) {
                setProperty(toolProps,"custom_copy_target_course_name", targetSite.getTitle());
            }

            setProperty(toolProps,"custom_copy_incontext", "true");
        }

        // Pull in all of the custom parameters
        for(Object okey : toolProps.keySet()) {
            String skey = (String) okey;

            if (!StringUtils.startsWith(skey, BasicLTIConstants.CUSTOM_PREFIX)) {
                continue;
            }

            String value = toolProps.getProperty(skey);

            if (value == null) {
                continue;
            }

            setProperty(ltiProps, skey, value);
        }


        Map<String,String> extra = new HashMap<String,String>();

        String orgGuid = serverConfigurationService.getString("basiclti.consumer_instance_guid", null);
        String orgDesc = serverConfigurationService.getString("basiclti.consumer_instance_description", null);
        String orgUrl = serverConfigurationService.getString("basiclti.consumer_instance_url", null);

        ltiProps = BasicLTIUtil.signProperties(ltiProps, launchUrl, "POST", key, secret, orgGuid, orgDesc, orgUrl, extra);

        return ltiProps;
    }

    /**
     * adds the lti role for the current user to the LTI Properties sent on the lti request
     * @param ltiProps
     * @param siteId
     */
    private void setRole(Properties ltiProps, String siteId) {
        String theRole = "Learner";

        if (securityService.isSuperUser()) {
            theRole = "Instructor,Administrator,urn:lti:instrole:ims/lis/Administrator,urn:lti:sysrole:ims/lis/Administrator";
        } else if (siteService.allowUpdateSite(siteId)) {
            theRole = "Instructor";
        }

        setProperty(ltiProps,BasicLTIConstants.ROLES,theRole);
    }

    /**
     * Adds auth code for give user to the LTI Properties sent on the lti request
     * @param ltiProps
     * @parm userId
     */
    private void setAuthCode(Properties ltiProps, String userId){
        if (StringUtils.isBlank(userId)) {
            userId = Constants.DEFAULT_ANONYMOUS_USER_ID;
        }

        try{
            String authCode = authCodeService.createAuthCode(userId).getAuthCode();
            setProperty(ltiProps, Constants.AUTHORIZATION_CODE_KEY, authCode);
            // Sakai 11 Basic LTI stopped adapting LTI properties with the CUSTOM_PREFIX
            // so we do it here for compatibility
            setProperty(ltiProps, BasicLTIConstants.CUSTOM_PREFIX + Constants.AUTHORIZATION_CODE_KEY, authCode);
        }catch(Exception e){
            LOG.error("Error thrown generating auth code from user id : {}", e.getMessage(), e);
        }
    }

    /**
     * Adds default return url to the LTI Properties sent on the lti request
     * @param ltiProps 
     */
    private void setDefaultReturnUrl(Properties ltiProps , String siteId){
        String returnUrl =  serverConfigurationService.getString("basiclti.consumer_return_url",null);

        if (returnUrl == null) {
            returnUrl = getOurServerUrl() + LTI1_PATH + "return-url";
            Session s = sessionManager.getCurrentSession();
            if (s != null) {
                String controllingPortal = (String) s.getAttribute("sakai-controlling-portal");

                if (controllingPortal == null) {
                    returnUrl += "/site";
                } else {
                    returnUrl += "/" + controllingPortal;
                }
            }

            returnUrl = returnUrl + "/" + siteId;
        }

        setProperty(ltiProps, BasicLTIConstants.LAUNCH_PRESENTATION_RETURN_URL, returnUrl);
    }

    /**
     * Adds debug option to the LTI Properties sent on the lti request
     * @param toolProps
     * @param module
     */
    private void setDebugOption(Properties toolProps, String module){
    
        String debug = "off";

        if(StringUtils.isNotBlank(module)){
            debug = serverConfigurationService.getString("kaltura." + module + ".debug", "off");  
        }

        setProperty(toolProps, LTI_DEBUG, debug);
    }

    /**
     * Adds window option to the LTI Properties sent on the lti request
     * @param toolProps 
     */
    private void setWindowOption(Properties toolProps){
        String newpage = "1";
        String frameheight = "0";

        setProperty(toolProps, LTI_FRAMEHEIGHT, frameheight);
        setProperty(toolProps, LTI_NEWPAGE, newpage);
    }

    public static String[] postError(String str) {
        return new String[]{str};
    }

    public String getOurServerUrl() {
        String ourUrl = serverConfigurationService.getString("sakai.lti.serverUrl");

        if (StringUtils.isBlank(ourUrl)) {
            ourUrl = serverConfigurationService.getString("serverUrl");
        }

        if (StringUtils.isBlank(ourUrl)) {
            ourUrl = serverConfigurationService.getServerUrl();
        }

        if (StringUtils.isBlank(ourUrl)) {
            ourUrl = "http://127.0.0.1:8080";
        }

        if(ourUrl.endsWith("/") && ourUrl.length() > 2) {
            ourUrl = ourUrl.substring(0,ourUrl.length()-1);
        }

        return ourUrl;
    }

    // To make absolutely sure we never send an XSS, we clean these values
    public static void setProperty(Properties props, String key, String value)  {
        if (value == null) {
            return;
        }

        if (props == null) {
            return;
        }

        value = Web.cleanHtml(value);

        if (value.trim().length() < 1) {
            return;
        }

        props.setProperty(key, value);
    }

    /**
     * Create the HTML to render a POST form and then automatically submit it.
     *
     * @param cleanProperties
     *          LTI properties
     * @param endpoint
     *          The LTI launch url.
     * @param debug
     *          Useful for viewing the HTML before posting to end point.
     * @param extra
     * @return the HTML ready for IFRAME src = inclusion.
     */
    public static String postLaunchHTML(final Properties cleanProperties,
            String endpoint, String launchtext, boolean debug, Map<String,String> extra) {
        Map<String, String> map = BasicLTIUtil.convertToMap(cleanProperties);
        return postLaunchHTML(map, endpoint, launchtext, debug, extra);
    }

    /**
     * Create the HTML to render a POST form and then automatically submit it.
     * This is a virtually identical copy of BasicLTIUtil.postLauchHTML,
     * except it does not submit the form automatically.  Instead the form
     * submit needs to be called as part of another script.
     * 
     * @param cleanProperties
     *          LTI properties
     * @param endpoint
     *          The LTI launch url.
     * @param debug
     *          Useful for viewing the HTML before posting to end point.
     * @param extra
     *          Useful for viewing the HTML before posting to end point.
     * @return the HTML ready for IFRAME src = inclusion.
     */
    public static String postLaunchHTML(final Map<String, String> cleanProperties, String endpoint, String launchtext, boolean debug, Map<String,String> extra) {
        if (cleanProperties == null || cleanProperties.isEmpty()) {
            throw new IllegalArgumentException("cleanProperties == null || cleanProperties.isEmpty()");
        }
        if (endpoint == null) {
            throw new IllegalArgumentException("endpoint == null");
        }

        Map<String, String> newMap = debug ? new TreeMap<String, String>(cleanProperties) : cleanProperties;

        StringBuilder text = new StringBuilder();
        // paint form
        text.append("<div id=\"ltiLaunchFormSubmitArea\">\n");
        text.append("<form action=\"");
        text.append(endpoint);
        text.append("\" name=\"ltiLaunchForm\" id=\"ltiLaunchForm\" method=\"post\" ");
        text.append(" encType=\"application/x-www-form-urlencoded\" accept-charset=\"utf-8\">\n");
        for (Entry<String, String> entry : newMap.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();

            if (value == null) {
                continue;
            }

            // This will escape the contents pretty much - at least
            // we will be safe and not generate dangerous HTML
            key = BasicLTIUtil.htmlspecialchars(key);
            value = BasicLTIUtil.htmlspecialchars(value);

            text.append("<input type=\"hidden\" name=\"");
            text.append(key);
            text.append("\" value=\"");
            text.append(value);
            text.append("\"/>\n");
        }

        // Paint the submit button
        text.append("<input type=\"submit\" value=\"");
        text.append(BasicLTIUtil.htmlspecialchars(launchtext));
        text.append("\">\n");

        text.append("</form>\n");
        text.append("</div>\n");

        // Paint the auto-pop up if we are transitioning from https: to http:
        // and are not already the top frame...
        text.append("<script type=\"text/javascript\">\n");
        text.append("if (window.top!=window.self) {\n");
        text.append("  theform = document.getElementById('ltiLaunchForm');\n");
        text.append("  if ( theform && theform.action ) {\n");
        text.append("   formAction = theform.action;\n");
        text.append("   ourUrl = window.location.href;\n");
        text.append("   if ( formAction.indexOf('http://') == 0 && ourUrl.indexOf('https://') == 0 ) {\n");
        text.append("      theform.target = '_blank';\n");
        text.append("      window.console && console.log('Launching http from https in new window!');\n");
        text.append("    }\n");
        text.append("  }\n");
        text.append("}\n");
        text.append("</script>\n");

        // paint debug output
        if (debug) {
            text.append("<pre>\n");
            text.append("<b>BasicLTI Endpoint</b>\n");
            text.append(endpoint);
            text.append("\n\n");
            text.append("<b>BasicLTI Parameters:</b>\n");

            for (Entry<String, String> entry : newMap.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();

                if (value == null) {
                    continue;
                }

                text.append(BasicLTIUtil.htmlspecialchars(key));
                text.append("=");
                text.append(BasicLTIUtil.htmlspecialchars(value));
                text.append("\n");
            }

            text.append("</pre>\n");

            if (extra != null) {
                String baseString = extra.get("BaseString");

                if (baseString != null) {
                    text.append("<!-- Base String\n");
                    text.append(baseString.replaceAll("-->","__>"));
                    text.append("\n-->\n");
                }
            }
        } else {
            text.append(" <script language=\"javascript\"> \n"
                    + "    document.getElementById(\"ltiLaunchFormSubmitArea\").style.display = \"none\";\n"
                    + "    document.ltiLaunchForm.submit(); \n" + " </script> \n");
        }

        String htmltext = text.toString();

        return htmltext;
    }

}
