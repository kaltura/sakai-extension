/*
 * Copyright ©2016 Kaltura, Inc.
 */
package org.sakaiproject.kaltura.services;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.quartz.JobExecutionException;
import org.sakaiproject.component.app.scheduler.jobs.AbstractConfigurableJob;
import org.sakaiproject.component.cover.ServerConfigurationService;
import org.sakaiproject.email.api.EmailService;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.site.api.Site;
import org.sakaiproject.site.api.SitePage;
import org.sakaiproject.site.api.SiteService;
import org.sakaiproject.tool.api.Session;
import org.sakaiproject.tool.api.SessionManager;
import org.sakaiproject.tool.api.ToolManager;
import org.sakaiproject.user.api.User;
import org.sakaiproject.user.api.UserDirectoryService;

import java.util.*;

public class AddMyMediaJob extends AbstractConfigurableJob {

    private static final Logger LOG = LoggerFactory.getLogger(AddMyMediaJob.class);

    protected SessionManager sessionManager;
    protected UserDirectoryService userDirectoryService;
    protected SiteService siteService;
    protected EmailService emailService;
    protected ToolManager toolManager;
    protected String adminEmail = "";
    private static final String TOOL_ID="kaltura.my.media";
    private static final String PAGE_TITLE="My Media";

    @Override
    public void runJob() throws JobExecutionException {
        LOG.debug("Started Adding My Media to Existing users");
        startSession(); // start admin user session to allow updates to any my workspace sites

        int totalCount = 0;
        int count = 0;
        try {
            //get special users
            String config = ServerConfigurationService.getString("sakai.specialUsers", "admin,postmaster");
            String[] items = StringUtils.split(config, ',');
            //get the myworkspace site id for those users
            List<String> specialUserWSIds = new ArrayList<String>();

            for (Iterator<String> userIds = Arrays.asList(items).iterator(); userIds.hasNext();) {
                String myWorkspaceId = siteService.getUserSiteId(userIds.next());
                specialUserWSIds.add(myWorkspaceId);
            }

            // get a list of all myworkspace sites (seems to only return sites for provided users)
            // IMPORTANT: 
            // the Site Object returned by the following call is result of lazy-get, so that the tool set in SitePage object is not populated
            // You need to get the fully populated Site object by using SiteService.getSite(siteId) call
            List<Site> sites = siteService.getSites(org.sakaiproject.site.api.SiteService.SelectionType.ANY, "myworkspace", null, null, null, null);
            HashSet<String> siteIdSet = new HashSet<String>();

            for (Iterator<Site> j = sites.iterator(); j.hasNext();) {
                Site thisSite = j.next();
                siteIdSet.add(thisSite.getId());
            }

            // now add additional myworkspace sites for native sakai users
            List<User> users = userDirectoryService.getUsers();
            for (Iterator<User> i = users.iterator(); i.hasNext();) {
                User user = i.next();
                String myWorkspaceId = siteService.getUserSiteId(user.getId());

                if (siteIdSet.contains(myWorkspaceId)) {
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
                if(!specialUserWSIds.contains(siteId) && !siteService.isSpecialSite(siteId)) {
                    SitePage sitePageEdit = null;
                    // check whether there is already a page with the page title
                    List<SitePage> pageList = siteEdit.getOrderedPages();

                    for (Iterator<SitePage> iPageList = pageList.iterator();iPageList.hasNext();) {
                        SitePage iPage= iPageList.next();

                        if (StringUtils.equalsIgnoreCase(PAGE_TITLE, iPage.getTitle())) {
                            // found the right page
                            pageId = iPage.getId();
                            break;
                        }
                    }

                    if (pageId != null){
                        LOG.debug("Page found for site:" + siteId + " with pageId=" + pageId);
                    } else {
                        sitePageEdit = siteEdit.addPage();
                        pageId = sitePageEdit.getId();
                        sitePageEdit.setTitle(PAGE_TITLE);
                        sitePageEdit.setLayout(0); // single column
                        sitePageEdit.setPopup(false);
                        sitePageEdit.addTool(toolManager.getTool(TOOL_ID));
                        siteService.save(siteEdit);
                        LOG.debug("Page added for site:" + siteId);
                        count++;
                    }

                    // get the saved site
                    siteEdit = siteService.getSite(siteId);
                    sitePageEdit = siteEdit.getPage(pageId);
                    siteService.save(siteEdit);
                }
            }

            sendEmail(totalCount, count, "COMPLETED");
        }catch(Exception e){
            LOG.debug("Exception occured while adding my media for existing users : "+ e.getMessage());
            sendEmail(totalCount, count, "FAILED");
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
        text = String.format("Add My Media for Existing Users Job Run status:" + status);
        builder.append(text).append('\n').append('\n');
        text =String.format("Total number of My workspace sites:" + totalusers);
        builder.append(text).append('\n').append('\n');
        text = String.format("Number of My workspace sites updated:" + updatedcount);
        builder.append(text).append('\n');
        final String emailText = builder.toString();
        final String emailSubject = "Status: Adding My Media to user's My Workspace";
        List<String> additionalHeaders = new ArrayList<String>(1);
        String content_type = "Content-Type: text/plain; charset=UTF-8";
        additionalHeaders.add(content_type);

        emailService.send(adminEmail,adminEmail, emailSubject, emailText, adminEmail,null, additionalHeaders);
    }

    public void setSessionManager(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    public void setUserDirectoryService(UserDirectoryService userDirectoryService) {
        this.userDirectoryService = userDirectoryService;
    }

    public void setSiteService(SiteService siteService) {
        this.siteService = siteService;
    }

    public void setEmailService(EmailService emailService) {
        this.emailService = emailService;
    }

    public void setToolManager(ToolManager toolManager) {
        this.toolManager = toolManager;
    }

    public void setAdminEmail(String adminEmail) {
        this.adminEmail = adminEmail;
    }

    public String getAdminEmail() {
        return adminEmail;
    }

}
