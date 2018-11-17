/*
 * Copyright ©2016 Kaltura, Inc.
 */
package org.sakaiproject.kaltura.models.dao;

import java.io.Serializable;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.sakaiproject.kaltura.util.AuthCodeUtil;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * This is a Kaltura LTI RESTful API authorization, it represents an authorization code object from the database
 * 
 * @author Robert Long (rlong @ unicon.net)
 */
@Data
@EqualsAndHashCode(of = "id")
@NoArgsConstructor
public class KalturaLtiAuthCode implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String userId;
    private String authCode;
    private Date dateCreated;
    private Date dateExpires;

    public KalturaLtiAuthCode(String userId, String authCode) {
        this(userId, authCode, new Date(), AuthCodeUtil.calculateExpirationDate(new Date()));
    }

    public KalturaLtiAuthCode(String userId, String authCode, Date dateCreated, Date dateExpires) {
        this(null, userId, authCode, dateCreated, dateExpires);
    }

    /**
     * Convenience constructor for partially-created {@link KalturaLtiAuthCode} objects
     * 
     * @param kalturaLtiAuthCode the partial {@link KalturaLtiAuthCode} object
     */
    public KalturaLtiAuthCode(KalturaLtiAuthCode kalturaLtiAuthCode) {
        this(
            kalturaLtiAuthCode.getId(),
            kalturaLtiAuthCode.getUserId(),
            kalturaLtiAuthCode.getAuthCode(),
            kalturaLtiAuthCode.getDateCreated(),
            kalturaLtiAuthCode.getDateExpires()
        );
    }

    /**
     * Full constructor
     * 
     * @param id
     * @param userId
     * @param authCode
     * @param dateCreated
     * @param dateExpires
     */
    public KalturaLtiAuthCode(Long id, String userId, String authCode, Date dateCreated, Date dateExpires) {
        this.id = id;
        this.userId = userId;

        if (StringUtils.isBlank(authCode)) {
            this.authCode = AuthCodeUtil.createNewAuthorizationCode();
        } else {
            this.authCode = authCode;
        }

        if (dateCreated == null) {
            this.dateCreated = new Date();
        } else {
            this.dateCreated = dateCreated;
        }

        if (dateExpires == null) {
            this.dateExpires = AuthCodeUtil.calculateExpirationDate(this.dateCreated);
        } else {
            this.dateExpires = dateExpires;
        }
    }

    public boolean isExpired() {
        return this.dateExpires.before(new Date());
    }

    /**
     * Checks that all fields have a value
     * 
     * @return true if all required fields contain a value
     */
    public boolean isValid() {
        if (StringUtils.isBlank(userId)) {
            return false;
        }
        if (StringUtils.isBlank(authCode)) {
            return false;
        }
        if (dateCreated == null) {
            return false;
        }
        if (dateExpires == null) {
            return false;
        }

        return true;
    }

}
