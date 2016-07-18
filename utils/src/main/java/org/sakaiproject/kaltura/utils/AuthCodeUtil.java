/*
 * Copyright ©2016 Kaltura, Inc.
 */
package org.sakaiproject.kaltura.utils;

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

        return new Date(createdDate.getTime() + (codeTtl * 1000L));
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
