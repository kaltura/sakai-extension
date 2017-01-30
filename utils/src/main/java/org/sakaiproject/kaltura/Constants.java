/*
 * Copyright ©2016 Kaltura, Inc.
 */
package org.sakaiproject.kaltura;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Holder for all Java-based string constants
 * 
 * @author Robert Long (rlong @ unicon.net)
 *
 */
public class Constants {

    /*
     * Site properties
     */
    public static final String SITE_REALM_PREFIX = "/site/";
    public static final String GROUP_REALM_PREFIX = "/group/";

    /*
     * Membership enrollment
     */
    public static final String MEMBERSHIP_COURSE_ROLE_STUDENT = "Student";
    public static final String MEMBERSHIP_COURSE_ROLE_INSTRUCTOR = "Instructor";
    public static final String MEMBERSHIP_PROJECT_ROLE_STUDENT = "access";
    public static final String MEMBERSHIP_PROJECT_ROLE_INSTRUCTOR = "maintain";
    public static final List<String> MEMBERSHIP_REALM_PERMISSION_IDS = new ArrayList<String>(2) {
        private static final long serialVersionUID = 1L;

        {
            add("site.visit");
            add("site.upd");
        }
    };

    /*
     * REST configuration
     */
    public static final String REST_DEFAULT_ENCODING = "UTF-8";

    /*
     * Role configuration
     */
    public final static String DEFAULT_LTI_ROLE = "Learner";
    public final static String[] DEFAULT_ROLE_MAPPING = new String[] {
        "Instructor:Instructor",
        "Student:Learner",
        "maintain:Instructor",
        "access:Learner",
        "Teaching Assistant:TeachingAssistant"
    };
    public static final String[] DEFAULT_LTI_ROLES = new String[] {
        "Learner",
        "Instructor",
        "Administrator",
        "TeachingAssistant",
        "ContentDeveloper",
        "Mentor"
    };
    public static final List<String> INVALID_ROLE_IDS = Collections.singletonList("-1");

    /*
     * Authorization code configuration
     */
    public static final int DEFAULT_AUTHORIZATION_CODE_TTL = 60 * 40; // 40 minutes
    public static final String AUTHORIZATION_CODE_KEY = "auth_code";
    public static final String DEFAULT_ANONYMOUS_USER_ID = ".anon";

    /*
     * Kaltura module names
     */
    public static final String MY_MEDIA = "my-media";
    public static final String MEDIA_GALLERY = "course-gallery";

}
