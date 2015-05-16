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
package org.sakaiproject.kaltura.utils.common;

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
        return Constants.INVALID_ROLE_IDS.contains(roleId);
    }

}
