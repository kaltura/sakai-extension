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
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Arrays;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.lang.StringUtils;
import org.quartz.JobExecutionException;
import org.sakaiproject.component.app.scheduler.jobs.AbstractConfigurableJob;
import org.sakaiproject.component.cover.ComponentManager;
import org.sakaiproject.component.cover.ServerConfigurationService;
import org.sakaiproject.email.api.EmailService;
import org.sakaiproject.entity.api.ResourceProperties;
import org.sakaiproject.id.api.IdManager;
import org.sakaiproject.site.api.Site;
import org.sakaiproject.site.api.SitePage;
import org.sakaiproject.site.api.SiteService;
import org.sakaiproject.site.api.ToolConfiguration;
import org.sakaiproject.tool.api.Session;
import org.sakaiproject.tool.api.SessionManager;
import org.sakaiproject.user.api.User;
import org.sakaiproject.user.api.UserDirectoryService;
import org.sakaiproject.user.api.UserNotDefinedException;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.tool.api.Tool;
import org.sakaiproject.tool.cover.ToolManager;

public class AddMyMediaJob extends AbstractConfigurableJob
 {

    private static final Log
        LOG = LogFactory.getLog(AddMyMediaJob.class);

    protected SessionManager sessionManager = null;
    protected UserDirectoryService userDirectoryService = null;
    protected SiteService siteService = null;
    protected EmailService emailService = null;
    protected String adminEmail = "";
    private static final String toolid="kaltura.lti";
    private static final String tooltitle="My Media";
    private static final String pagetitle="My Media";

    @Override
    public void runJob() throws JobExecutionException
    {
        LOG.debug("Started Adding My Media to Existing users");
        startSession(); // start admin user session to allow updates to any my workspace sites

        int totalCount =0;
        int count = 0;
        try{
            //Re-using code from sakai web services to add tool to all my workspace

            //get special users
            String config = ServerConfigurationService.getString("sakai.specialUsers", "admin,postmaster");
            String[] items = StringUtils.split(config, ',');
            //get the myworkspace site id for those users
            List<String> specialUserWSIds = new ArrayList<String>();
            for (Iterator userIds = Arrays.asList(items).iterator(); userIds.hasNext();) {
                String myWorkspaceId = siteService.getUserSiteId((String) userIds.next());
                specialUserWSIds.add(myWorkspaceId);
            }
            // get a list of all myworkspace sites (seems to only return sites for provided users)
            // IMPORTANT: 
            // the Site Object returned by the following call is result of lazy-get, so that the tool set in SitePage object is not populated
            // You need to get the fully populated Site object by using SiteService.getSite(siteId) call
            List<Site> sites = siteService.getSites(org.sakaiproject.site.api.SiteService.SelectionType.ANY, "myworkspace", 
                    null, null, null, null);
            HashSet<String> siteIdSet = new HashSet<String>();
            for (Iterator j = sites.iterator(); j.hasNext();) { 
                Site thisSite = (Site)j.next();
                siteIdSet.add( thisSite.getId());
            }
            // now add additional myworkspace sites for native sakai users
            List<User> users = userDirectoryService.getUsers();
            for (Iterator i = users.iterator(); i.hasNext();) { 
                User user = (User) i.next();
                String myWorkspaceId = siteService.getUserSiteId(user.getId());
                if ( siteIdSet.contains(myWorkspaceId) )
                {
                    LOG.debug("Contains site for " + myWorkspaceId);
                    continue;
                }
                try {
                    Site siteEdit = siteService.getSite(myWorkspaceId);
                    siteIdSet.add(siteEdit.getId());
                } catch (IdUnusedException e) {
                    LOG.info("No workspace for user: " + myWorkspaceId + ", skipping...");
                    continue;
                }

            }
            // set up count
            totalCount = siteIdSet.size();
            //now add a page to each site, and the tool to that page
            for (Iterator<String> j = siteIdSet.iterator(); j.hasNext();) { 
                String siteId = j.next();
                Site siteEdit = siteService.getSite(siteId);
                String pageId = null;
                // if the site is not for special user
                if(!specialUserWSIds.contains(siteId) && !siteService.isSpecialSite(siteId))
                {
                    SitePage sitePageEdit = null;
                    // check whether there is already a page with the page title
                    List<SitePage> pageList = siteEdit.getOrderedPages();
                    for (Iterator<SitePage> iPageList = pageList.iterator();iPageList.hasNext();)
                    {
                        SitePage iPage= iPageList.next();
                        if (pagetitle.equals(iPage.getTitle()))
                        {
                            // found the right page
                            pageId = iPage.getId();
                            break;
                        }
                    }
                    if (pageId != null)
                    {
                        LOG.debug("Page found for site:" + siteId + " with pageId=" + pageId);
                    }
                    else
                    { 
                        sitePageEdit = siteEdit.addPage();
                        pageId = sitePageEdit.getId();
                        sitePageEdit.setTitle(pagetitle);
                        sitePageEdit.setLayout(0); // single column
                        sitePageEdit.setPopup(false);
                        sitePageEdit.addTool(ToolManager.getTool(toolid));
                        siteService.save(siteEdit);
                        LOG.debug("Page added for site:" + siteId);
                        count++;
                    }

                    // get the saved site
                    siteEdit=siteService.getSite(siteId);
                    sitePageEdit = siteEdit.getPage(pageId);
                    siteService.save(siteEdit);                
                   
                }
            }
            sendEmail(totalCount,count,"COMPLETED");
        }catch(Exception e){
            LOG.debug("Exception occured while adding my media for existing users : "+ e.getMessage());
            sendEmail(totalCount,count,"FAILED");
        }
    }

    /**
     * Starts an admin user session
     */
    protected void startSession() {
        // start a session for admin so we have full permissions
        Session session = sessionManager.startSession();
        sessionManager.setCurrentSession(session);
        session.setUserEid("admin");
        session.setUserId("admin");
    }

    /**
     * Sends an email to address specified in jobs.admin.email in sakai.properties
     * @totalusers - total count of myworkspace sites found in platform
     * @updatedcount - number of myworkspace sites updated as part of this job execution
     * @status - COMPLETED - if no error was encountered during update , FAILED- if any errors occur
     */
      
    protected void sendEmail(int totalusers, int updatedcount, String status) {

        StringBuilder builder = new StringBuilder();
        String text = null;
        text = String.format("Add My Media for Existing Users Job Run status:"+ status);
        builder.append(text).append('\n').append('\n');
        text =String.format("Total number of My workspace sites:"+ totalusers);
        builder.append(text).append('\n').append('\n');
        text = String.format("Number of My workspace sites updated:"+ updatedcount);
        builder.append(text).append('\n');
        final String emailText = builder.toString();
        final String emailSubject = "Status: Adding My Media to user's My Workspace";

        List<String> additionalHeaders = new ArrayList<String>(1);
        String content_type = "Content-Type: text/plain; charset=UTF-8";
        additionalHeaders.add(content_type);
        emailService.send(adminEmail,adminEmail, emailSubject,
                emailText, adminEmail,null, additionalHeaders);
    }
    
    public void setSessionManager(SessionManager mgr) {
        sessionManager = mgr;
    }

    public SessionManager getSessionManager() {
        return sessionManager;
    }

    public void setUserDirectoryService(UserDirectoryService service)
    {
        userDirectoryService = service;
    }

    public UserDirectoryService getUserDirectoryService()
    {
        return userDirectoryService;
    }

    public void setSiteService(SiteService service)
    {
        siteService = service;
    }

    public SiteService getSiteService()
    {
        return siteService;
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
