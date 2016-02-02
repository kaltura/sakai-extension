/*
 * Copyright ©2016 Kaltura, Inc.
 */
package org.sakaiproject.kaltura.utils;

import org.sakaiproject.kaltura.Constants;

public class RoleUtil {

    public static boolean isValidSakaiRoleId(String... sakaiRoleIds) {
        for (String sakaiRoleId : sakaiRoleIds) {
            if (!isValidRoleId(sakaiRoleId)) {
                return false;
            }
        }

        return true;
    }

    public static boolean isValidLtiRoleId(String... ltiRoleIds) {
        for (String ltiRoleId : ltiRoleIds) {
            if (!isValidRoleId(ltiRoleId)) {
                return false;
            }
        }

        return true;
    }

    public static boolean isValidRoleId(String roleId) {
        return !Constants.INVALID_ROLE_IDS.contains(roleId);
    }

}
