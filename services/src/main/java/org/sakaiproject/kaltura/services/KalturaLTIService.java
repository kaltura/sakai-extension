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

import java.util.Properties;
import java.util.Map;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.imsglobal.basiclti.BasicLTIUtil;
import org.imsglobal.basiclti.BasicLTIConstants;
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
import org.sakaiproject.kaltura.services.AuthCodeService;

public class KalturaLTIService {
    private static final Log LOG = LogFactory.getLog(KalturaLTIService.class);

    private static ResourceLoader rb = new ResourceLoader("basiclti");
    public static final boolean verbosePrint = true;

    public static final String LTI_SECRET =    "secret";
    public static final String LTI_NEWPAGE =   "newpage";
    public static final String LTI_DEBUG = "debug";
    public static final String LTI_FRAMEHEIGHT = "frameheight";
    public static final String LTI1_PATH = "/imsblis/service/";
    public static final String CKEDITOR_REQUEST="ckeditor";
    public static final String LAUNCH_MEDIA = "launchmedia";
    
    private RoleService roleService;
    public void setRoleService(RoleService roleService) {
        this.roleService = roleService;
    }

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
    
    public String[] launchLTIRequest(String module, String userId, String siteId) {
        User user = null;
        try {
            if (StringUtils.isNotBlank(userId)) {
                user = userDirectoryService.getUser(userId);
            }
        } catch (UserNotDefinedException e1) {
            LOG.error("User not found with ID: " + userId, e1);
        }

        String placementId = "placementId123";
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
        
        String launch_url = serverConfigurationService.getString("kaltura.launch.url");
        if(!module.isEmpty()){
            launch_url=launch_url+"/"+ module;
        }
        setProperty(toolProps, "launch_url", launch_url);
        
        setDefaultReturnUrl(ltiProps, siteId);

        setProperty(ltiProps,BasicLTIConstants.RESOURCE_LINK_ID,placementId);

        if ( rb != null ) setProperty(ltiProps,BasicLTIConstants.LAUNCH_PRESENTATION_LOCALE,rb.getLocale().toString());

        setRole(ltiProps, siteId);
        setAuthCode(ltiProps, userId);
        setDebugOption(toolProps,module);
        setWindowOption(toolProps);
        
        // Pull in all of the custom parameters
        for(Object okey : toolProps.keySet() ) {
            String skey = (String) okey;
            if ( ! skey.startsWith(BasicLTIConstants.CUSTOM_PREFIX) ) continue;
            String value = toolProps.getProperty(skey);
            if ( value == null ) continue;
            setProperty(ltiProps, skey, value);
        }
        
        Map<String,String> extra = new HashMap<String,String> ();
       
        String org_guid = serverConfigurationService.getString("basiclti.consumer_instance_guid",null);
        String org_desc = serverConfigurationService.getString("basiclti.consumer_instance_description",null);
        String org_url = serverConfigurationService.getString("basiclti.consumer_instance_url",null);
                         
        ltiProps = BasicLTIUtil.signProperties(ltiProps, launch_url, "POST",
                key, secret, org_guid, org_desc, org_url, extra);

        if ( ltiProps == null ) {
            return postError("<p>" + "Error signing message."+"</p>");
        }

        dPrint("LAUNCH III="+ltiProps);

        String debugProperty = toolProps.getProperty(LTI_DEBUG);
        
        boolean dodebug = StringUtils.equals("on",debugProperty) || StringUtils.equals("1",debugProperty);

        String postData = postLaunchHTML(ltiProps, launch_url, dodebug, extra);

        String [] retval = { postData, launch_url };
        
        return retval;

    }
    

    public String[] launchCKEditorRequest(String module, String userId, String siteId) {
        User user = null;
        try {
            if (StringUtils.isNotBlank(userId)) {
                user = userDirectoryService.getUser(userId);
            }
        } catch (UserNotDefinedException e1) {
            LOG.error("User not found with ID: " + userId, e1);
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
        setProperty(toolProps, LTI_SECRET, secret );
        setProperty(toolProps, "key", key );

        // TODO handle null result
        String ckeditorUrl = serverConfigurationService.getString("kaltura.ckeditor.url");
        String serverUrl = serverConfigurationService.getServerUrl();
        String ckeditorCallbackUrl = serverUrl + "/media-gallery-tool/ckeditorcallback.htm";
        LOG.info("ckeditorCallbackUrl: [" + ckeditorCallbackUrl + "]");
        
        setProperty(ltiProps, BasicLTIConstants.LAUNCH_PRESENTATION_RETURN_URL, ckeditorCallbackUrl);        
        setProperty(toolProps, "launch_url", ckeditorUrl);
        
        setProperty(ltiProps,BasicLTIConstants.RESOURCE_LINK_ID,placementId);

        if ( rb != null ) setProperty(ltiProps,BasicLTIConstants.LAUNCH_PRESENTATION_LOCALE,rb.getLocale().toString());
        
        setRole(ltiProps, siteId);
        setDebugOption(toolProps , CKEDITOR_REQUEST);
        setWindowOption(toolProps);
                
        // Pull in all of the custom parameters
        for(Object okey : toolProps.keySet() ) {
            String skey = (String) okey;
            if ( ! skey.startsWith(BasicLTIConstants.CUSTOM_PREFIX) ) continue;
            String value = toolProps.getProperty(skey);
            if ( value == null ) continue;
            setProperty(ltiProps, skey, value);
        }

        Map<String,String> extra = new HashMap<String,String> ();     

        String org_guid = serverConfigurationService.getString("basiclti.consumer_instance_guid",null);
        String org_desc = serverConfigurationService.getString("basiclti.consumer_instance_description",null);
        String org_url = serverConfigurationService.getString("basiclti.consumer_instance_url",null);

        ltiProps = BasicLTIUtil.signProperties(ltiProps, ckeditorUrl, "POST",
                key, secret, org_guid, org_desc, org_url, extra);

        if ( ltiProps == null ) {
            return postError("<p>" + "Error signing message."+"</p>");
        }

        dPrint("LAUNCH III="+ltiProps);

        String debugProperty = toolProps.getProperty(LTI_DEBUG);
        boolean dodebug = "on".equals(debugProperty) || "1".equals(debugProperty);
        
        String postData = postLaunchHTML(ltiProps, ckeditorUrl, dodebug, extra);

        String [] retval = { postData, ckeditorUrl };
        return retval;

    }
    public String[] launchLTIDisplayRequest(String module, String userId, String siteId) {
        User user = null;
        try {
            if (StringUtils.isNotBlank(userId)) {
                user = userDirectoryService.getUser(userId);
            }
        } catch (UserNotDefinedException e1) {
            LOG.error("User not found with ID: " + userId, e1);
        }

        String placementId = "placementId123";
        return launchLTIDisplayRequest(module, user, siteId, placementId);
    }
    
    /**
     * given a media item URL, initiates an LTI call to begin a session (if necessary) and return the 
     * html to render the media item in an iFrame
     * @return
     */
    public String[] launchLTIDisplayRequest(String launch_url, User user, String siteId, String placementId) {
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
        
        setProperty(toolProps, "launch_url", launch_url);

        setProperty(ltiProps,BasicLTIConstants.RESOURCE_LINK_ID,placementId);

        if ( rb != null ) setProperty(ltiProps,BasicLTIConstants.LAUNCH_PRESENTATION_LOCALE,rb.getLocale().toString());

        setAuthCode(ltiProps, userId);
        setRole(ltiProps, siteId);
        setDebugOption(toolProps,LAUNCH_MEDIA);
        setWindowOption(toolProps);
                
        // Pull in all of the custom parameters
        for(Object okey : toolProps.keySet() ) {
            String skey = (String) okey;
            if ( ! skey.startsWith(BasicLTIConstants.CUSTOM_PREFIX) ) continue;
            String value = toolProps.getProperty(skey);
            if ( value == null ) continue;
            setProperty(ltiProps, skey, value);
        }

        Map<String,String> extra = new HashMap<String,String> ();        
        
        String org_guid = serverConfigurationService.getString("basiclti.consumer_instance_guid",null);
        String org_desc = serverConfigurationService.getString("basiclti.consumer_instance_description",null);
        String org_url = serverConfigurationService.getString("basiclti.consumer_instance_url",null);

        ltiProps = BasicLTIUtil.signProperties(ltiProps, launch_url, "POST",
                key, secret, org_guid, org_desc, org_url, extra);

        if ( ltiProps == null ) {
            return postError("<p>" + "Error signing message."+"</p>");
        }

        dPrint("LAUNCH III="+ltiProps);

        String debugProperty = toolProps.getProperty(LTI_DEBUG);
        boolean dodebug = "on".equals(debugProperty) || "1".equals(debugProperty);

        String postData = postLaunchHTML(ltiProps, launch_url, dodebug, extra);

        String [] retval = { postData, launch_url };
        return retval;    	
    }
    
    public Properties initLTIProps(User user, String siteId) {
        
        Properties ltiProps = new Properties();
        setProperty(ltiProps,BasicLTIConstants.LTI_VERSION,BasicLTIConstants.LTI_VERSION_1);
        
        // KAF required LTI launch parameters-http://knowledge.kaltura.com/understanding-kaltura-application-framework-kaf#auth
        
        String sakaiVersion = serverConfigurationService.getString("version.sakai","2");
        setProperty(ltiProps,"ext_lms", "sakai-"+sakaiVersion);
        setProperty(ltiProps,BasicLTIConstants.TOOL_CONSUMER_INFO_PRODUCT_FAMILY_CODE,
                "sakai");
        setProperty(ltiProps,BasicLTIConstants.TOOL_CONSUMER_INFO_VERSION, sakaiVersion);
        
        // add user info
        if ( user != null )
        {
            setProperty(ltiProps,BasicLTIConstants.USER_ID,user.getId());
            setProperty(ltiProps,BasicLTIConstants.LIS_PERSON_SOURCEDID,user.getEid());
            setProperty(ltiProps,BasicLTIConstants.LIS_PERSON_NAME_GIVEN,user.getFirstName());
            setProperty(ltiProps,BasicLTIConstants.LIS_PERSON_NAME_FAMILY,user.getLastName());
            setProperty(ltiProps,BasicLTIConstants.LIS_PERSON_NAME_FULL,user.getDisplayName());
            setProperty(ltiProps,BasicLTIConstants.LIS_PERSON_CONTACT_EMAIL_PRIMARY,user.getEmail());
            // Only send the display ID if it's different to the EID.
            LOG.error("eid:displayId: [" + user.getEid() + ":" + user.getDisplayId() + "]");
            if (!user.getEid().equals(user.getDisplayId())) {
                setProperty(ltiProps,BasicLTIConstants.EXT_SAKAI_PROVIDER_DISPLAYID,user.getDisplayId());
            }
        }

        //add site info
        Site site =  null;
        try{
            site = siteService.getSite(siteId);
        }catch(Exception e){

        }
        if(site!=null){
            setProperty(ltiProps,BasicLTIConstants.CONTEXT_ID,site.getId());
            setProperty(ltiProps,BasicLTIConstants.CONTEXT_TITLE,site.getTitle());
        }

        // add oauth call back 
        String oauth_callback = serverConfigurationService.getString("basiclti.oauth_callback",null);
        // Too bad there is not a better default callback url for OAuth
        // Actually since we are using signing-only, there is really not much point 
        // In OAuth 6.2.3, this is after the user is authorized

        if ( oauth_callback == null ) oauth_callback = "about:blank";
        setProperty(ltiProps, "oauth_callback", oauth_callback);
        setProperty(ltiProps, BasicLTIUtil.BASICLTI_SUBMIT, "Press to Launch External Tool");

        return ltiProps;
    }

    /**
     * Prepares LTI properties that need to be sent as POST parameters to initiate copy on kaltura server on sakai site import 
     * @param module - string indicating which module in kaltura is using this service
     * @param fromSiteId - sakai Site Id which is copied 
     * @param targetSiteId - sakai site Id to which kaltura media items are copied to
     * @return properties - Properties object holding LTI properties
     */
    public Properties prepareSiteCopyRequest(String module,String fromSiteId, String targetSiteId){

        // get admin user 
        User user = null;
        try {
            user = userDirectoryService.getUser("admin");
        } catch (UserNotDefinedException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        // Start building up the properties
        Properties ltiProps = initLTIProps(user, fromSiteId);
        Properties toolProps = new Properties();
        
        // Add key and secret
        String key = serverConfigurationService.getString("kaltura.launch.key");
        String secret = serverConfigurationService.getString("kaltura.launch.secret");
        setProperty(toolProps, LTI_SECRET, secret );
        setProperty(toolProps, "key", key );

        String launch_url = serverConfigurationService.getString("kaltura.launch.url");
       
        if(!module.isEmpty()){
            launch_url=launch_url+"/"+ module;
        }

        setProperty(toolProps, "launch_url", launch_url);

        setDefaultReturnUrl(ltiProps, fromSiteId);
        String theRole = "Instructor,Administrator,urn:lti:instrole:ims/lis/Administrator,urn:lti:sysrole:ims/lis/Administrator";
        setProperty(ltiProps,BasicLTIConstants.ROLES,theRole);

        Site fromSite =  null;
        try{
            fromSite = siteService.getSite(fromSiteId);
        }catch(Exception e){

        }

        String placementId ="copySitePlacement123";
        setProperty(ltiProps,BasicLTIConstants.RESOURCE_LINK_ID,placementId);

        if(!StringUtils.isBlank(targetSiteId)){
            // set custom parameters for Site copy lti data

            String custom_copy_source_course_id="";
            String custom_copy_target_course_id="";
            String custom_copy_target_course_name= "";
            String custom_copy_content_owners="";
            String custom_copy_incontext=serverConfigurationService.getString("kaltura.site.copy.incontext", "false");

            Site targetSite =  null;
            try{
                targetSite = siteService.getSite(targetSiteId);
            }catch(Exception e){

            }

            setProperty(toolProps,"custom_copy_source_course_id",fromSiteId);
            setProperty(toolProps,"custom_copy_target_course_id",targetSiteId);
            if(targetSite!=null){
                setProperty(toolProps,"custom_copy_target_course_name", targetSite.getTitle());
            }
            setProperty(toolProps,"custom_copy_incontext", "true");
        }

        // Pull in all of the custom parameters
        for(Object okey : toolProps.keySet() ) {
            String skey = (String) okey;
            if ( ! skey.startsWith(BasicLTIConstants.CUSTOM_PREFIX) ) continue;
            String value = toolProps.getProperty(skey);
            if ( value == null ) continue;
            setProperty(ltiProps, skey, value);
        }

        // Pull in all of the custom parameters
        for(Object okey : toolProps.keySet() ) {
            String skey = (String) okey;
            if ( ! skey.startsWith(BasicLTIConstants.CUSTOM_PREFIX) ) continue;
            String value = toolProps.getProperty(skey);
            if ( value == null ) continue;
            setProperty(ltiProps, skey, value);
        }

        Map<String,String> extra = new HashMap<String,String> ();

        String org_guid = serverConfigurationService.getString("basiclti.consumer_instance_guid",null);
        String org_desc = serverConfigurationService.getString("basiclti.consumer_instance_description",null);
        String org_url = serverConfigurationService.getString("basiclti.consumer_instance_url",null);
        
        ltiProps = BasicLTIUtil.signProperties(ltiProps, launch_url, "POST",
                key, secret, org_guid, org_desc, org_url, extra);

        return ltiProps;
    }

    /**
     * Prepares LTI properties that need to be sent as POST parameters to initiate copy on kaltura server on sakai site import 
     * @param module - string indicating which module in kaltura is using this service
     * @param fromSiteId - sakai Site Id which is copied 
     * @param jobId - kaltura job id 
     * @return properties - Properties object holding LTI properties
     */
    public Properties prepareJobStatusRequest(String module,String fromSiteId, String jobId){

        // get admin user 
        User user = null;
        try {
            user = userDirectoryService.getUser("admin");
        } catch (UserNotDefinedException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        // Start building up the properties
        Properties ltiProps = initLTIProps(user, fromSiteId);
        Properties toolProps = new Properties();

        // Add key and secret
        String key = serverConfigurationService.getString("kaltura.launch.key");
        String secret = serverConfigurationService.getString("kaltura.launch.secret");
        setProperty(toolProps, LTI_SECRET, secret );
        setProperty(toolProps, "key", key );

        String launch_url = serverConfigurationService.getString("kaltura.launch.url");

        if(!module.isEmpty()){
            launch_url=launch_url+"/"+ module;
        }

        setProperty(toolProps, "launch_url", launch_url);
        setDefaultReturnUrl(ltiProps, fromSiteId);
        String theRole = "Instructor,Administrator,urn:lti:instrole:ims/lis/Administrator,urn:lti:sysrole:ims/lis/Administrator";
        setProperty(ltiProps,BasicLTIConstants.ROLES,theRole);

        String placementId ="copySitePlacement123";
        setProperty(ltiProps,BasicLTIConstants.RESOURCE_LINK_ID,placementId);
        setProperty(toolProps,"custom_jobid",jobId);

        // Pull in all of the custom parameters
        for(Object okey : toolProps.keySet() ) {
            String skey = (String) okey;
            if ( ! skey.startsWith(BasicLTIConstants.CUSTOM_PREFIX) ) continue;
            String value = toolProps.getProperty(skey);
            if ( value == null ) continue;
            setProperty(ltiProps, skey, value);
        }

        Map<String,String> extra = new HashMap<String,String> ();

        String org_guid = serverConfigurationService.getString("basiclti.consumer_instance_guid",null);
        String org_desc = serverConfigurationService.getString("basiclti.consumer_instance_description",null);
        String org_url = serverConfigurationService.getString("basiclti.consumer_instance_url",null);

        ltiProps = BasicLTIUtil.signProperties(ltiProps, launch_url, "POST",
                key, secret, org_guid, org_desc, org_url, extra);

        return ltiProps;
    }

    /**
     * adds the lti role for the current user to the LTI Properties sent on the lti request
     * @param ltiProps
     * @param siteId
     */
    private void setRole(Properties ltiProps, String siteId) {
        String theRole = "Learner";
        if ( securityService.isSuperUser() )
        {
            theRole = "Instructor,Administrator,urn:lti:instrole:ims/lis/Administrator,urn:lti:sysrole:ims/lis/Administrator";
        }
        else if ( siteService.allowUpdateSite(siteId) )
        {
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
        }catch(Exception e){
            LOG.error("Error thrown generating auth code from user id : " + e);
            e.printStackTrace();
        }
    }

    /**
     * Adds default return url to the LTI Properties sent on the lti request
     * @param ltiProps 
     */
    private void setDefaultReturnUrl(Properties ltiProps , String siteId){

        String returnUrl =  serverConfigurationService.getString("basiclti.consumer_return_url",null);
        if ( returnUrl == null ) {
            returnUrl = getOurServerUrl() + LTI1_PATH + "return-url";
            Session s = sessionManager.getCurrentSession();
            if (s != null) {
                String controllingPortal = (String) s.getAttribute("sakai-controlling-portal");
                if ( controllingPortal == null ) {
                    returnUrl = returnUrl + "/site";
                } else {
                    returnUrl = returnUrl + "/" + controllingPortal;
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
        if(!StringUtils.isBlank(module)){
            debug = serverConfigurationService.getString("kaltura."+module+".debug","off");  
        }
        setProperty(toolProps, LTI_DEBUG, debug+"");
    }

    /**
     * Adds window option to the LTI Properties sent on the lti request
     * @param toolProps 
     */
    private void setWindowOption(Properties toolProps){
        int newpage =1;
        int frameheight = 0;
        
        setProperty(toolProps, LTI_FRAMEHEIGHT, frameheight+"" );
        setProperty(toolProps, LTI_NEWPAGE, newpage+"" );
    }

    public static String[] postError(String str) {
        String [] retval = { str };
        return retval;
    }

    public String getOurServerUrl() {
        String ourUrl = serverConfigurationService.getString("sakai.lti.serverUrl");
        if (ourUrl == null || ourUrl.equals(""))
            ourUrl = serverConfigurationService.getString("serverUrl");
        if (ourUrl == null || ourUrl.equals(""))
            ourUrl = serverConfigurationService.getServerUrl();
        if (ourUrl == null || ourUrl.equals(""))
            ourUrl = "http://127.0.0.1:8080";

        if ( ourUrl.endsWith("/")  && ourUrl.length() > 2 )
            ourUrl = ourUrl.substring(0,ourUrl.length()-1);

        return ourUrl;
    }

    // To make absolutely sure we never send an XSS, we clean these values
    public static void setProperty(Properties props, String key, String value)
    {
        if ( value == null ) return;
        if ( props == null ) return;
        value = Web.cleanHtml(value);
        if ( value.trim().length() < 1 ) return;
        props.setProperty(key, value);
    }

    public static void dPrint(String str)
    {
        if ( verbosePrint ) System.out.println(str);
    }

	/**
	 * Create the HTML to render a POST form and then automatically submit it.
	 * Make sure to call {@link #cleanupProperties(Properties)} before signing.
	 * 
	 * @param cleanProperties
	 *		  Assumes you have called {@link #cleanupProperties(Properties)}
	 *		  beforehand.
	 * @param endpoint
	 *		  The LTI launch url.
	 * @param debug
	 *		  Useful for viewing the HTML before posting to end point.
	 * @param extra
	 * @return the HTML ready for IFRAME src = inclusion.
	 */
	public static String postLaunchHTML(final Properties cleanProperties,
			String endpoint, boolean debug, Map<String,String> extra) {
		Map<String, String> map = BasicLTIUtil.convertToMap(cleanProperties);
		return postLaunchHTML(map, endpoint, debug, extra);
	}

	/**
	 * Create the HTML to render a POST form and then automatically submit it.
	 * Make sure to call {@link #cleanupProperties(Properties)} before signing.
	 * This is a virtually identical copy of BasicLTIUtil.postLauchHTML,
	 * except it does not submit the form automatically.  Instead the form
	 * submit needs to be called as part of another script.
	 * 
	 * @param cleanProperties
	 *		  Assumes you have called {@link #cleanupProperties(Properties)}
	 *		  beforehand.
	 * @param endpoint
	 *		  The LTI launch url.
	 * @param debug
	 *		  Useful for viewing the HTML before posting to end point.
	 * @param extra
	 *		  Useful for viewing the HTML before posting to end point.
	 * @return the HTML ready for IFRAME src = inclusion.
	 */
	public static String postLaunchHTML(
			final Map<String, String> cleanProperties, String endpoint, 
			boolean debug, Map<String,String> extra) {

		if (cleanProperties == null || cleanProperties.isEmpty()) {
			throw new IllegalArgumentException(
					"cleanProperties == null || cleanProperties.isEmpty()");
		}
		if (endpoint == null) {
			throw new IllegalArgumentException("endpoint == null");
		}
		Map<String, String> newMap = null;
		if (debug) {
			// sort the properties for readability
			newMap = new TreeMap<String, String>(cleanProperties);
		} else {
			newMap = cleanProperties;
		}
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
			if (value == null)
				continue;
			// This will escape the contents pretty much - at least
			// we will be safe and not generate dangerous HTML
			key = BasicLTIUtil.htmlspecialchars(key);
			value = BasicLTIUtil.htmlspecialchars(value);
			if (key.equals(BasicLTIUtil.BASICLTI_SUBMIT)) {
				text.append("<input type=\"submit\" name=\"");
			} else {
				text.append("<input type=\"hidden\" name=\"");
			}
			text.append(key);
			text.append("\" value=\"");
			text.append(value);
			text.append("\"/>\n");
		}
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
				if (value == null)
					continue;
				text.append(BasicLTIUtil.htmlspecialchars(key));
				text.append("=");
				text.append(BasicLTIUtil.htmlspecialchars(value));
				text.append("\n");
			}
			text.append("</pre>\n");
			if ( extra != null ) {
				String base_string = extra.get("BaseString");
				if ( base_string != null ) {
					text.append("<!-- Base String\n");
					text.append(base_string.replaceAll("-->","__>"));
					text.append("\n-->\n");
				}
			}
		} else {
			// paint auto submit script
			text
				.append(" <script language=\"javascript\"> \n"
						+ "	document.getElementById(\"ltiLaunchFormSubmitArea\").style.display = \"none\";\n"
						+ "	nei = document.createElement('input');\n"
						+ "	nei.setAttribute('type', 'hidden');\n"
						+ "	nei.setAttribute('name', '"
						+ BasicLTIUtil.BASICLTI_SUBMIT
						+ "');\n"
						+ "	nei.setAttribute('value', '"
						+ newMap.get(BasicLTIUtil.BASICLTI_SUBMIT)
						+ "');\n"
						+ "	document.getElementById(\"ltiLaunchForm\").appendChild(nei);\n"
						+ "</script>");
		}

		String htmltext = text.toString();
		return htmltext;
	}
}
