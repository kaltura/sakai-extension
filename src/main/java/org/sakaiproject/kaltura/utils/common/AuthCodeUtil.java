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

import java.util.Date;
import java.util.UUID;

import org.sakaiproject.component.cover.ComponentManager;
import org.sakaiproject.component.api.ServerConfigurationService;
import org.sakaiproject.kaltura.Constants;

public class AuthCodeUtil {

    /**
     * Calculates the expiration date of an authorization code
     * Default: 1 minute after creation
     * 
     * @param createdDate the {@link Date} the code was created
     * @return the {@link Date} the code expires
     */
    public static Date calculateExpirationDate(Date createdDate) {
        if (createdDate == null) {
            throw new IllegalArgumentException("Created date cannot be null.");
        }

        ServerConfigurationService serverConfigurationService = (ServerConfigurationService) ComponentManager.get(ServerConfigurationService.class);
        int codeTtl = serverConfigurationService.getInt("kaltura.rest.authorization.ttl", Constants.DEFAULT_AUTHORIZATION_CODE_TTL);

        return new Date(createdDate.getTime() + codeTtl);
    }

    /**
     * Creates a new authorization code, based on a random UUID
     * 
     * @return the {@link UUID} string
     */
    public static String createNewAuthorizationCode() {
        UUID uuid = UUID.randomUUID();

        return uuid.toString();
    }

}
