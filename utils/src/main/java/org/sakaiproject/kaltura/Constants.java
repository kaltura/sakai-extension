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
package org.sakaiproject.kaltura;

import java.util.ArrayList;
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
    public static final List<String> INVALID_ROLE_IDS = new ArrayList<String>(1) {
        private static final long serialVersionUID = 1L;

        {
            add("-1");
        }
    };

    /*
     * Authorization code configuration
     */
    public static final int DEFAULT_AUTHORIZATION_CODE_TTL = 60000; // 1 minute
    public static final String AUTHORIZATION_OVERRIDE_CODE = "c48cb080-852b-11e4-80c2-0002a5d5c51b";
    public static final String AUTHORIZATION_CODE_KEY = "auth_code";

    /*
     * Kaltura module names
     */
    public static final String MY_MEDIA = "my-media";
    public static final String MEDIA_GALLERY = "course-gallery";

}
