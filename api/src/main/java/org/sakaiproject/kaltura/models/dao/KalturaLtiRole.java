/*
 * Copyright ©2016 Kaltura, Inc.
 */
package org.sakaiproject.kaltura.models.dao;

import java.io.Serializable;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.sakaiproject.kaltura.Constants;
import org.sakaiproject.kaltura.util.JsonUtil;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * This is a Kaltura Sakai role to LTI role mapping, it represents a mapping object from the database
 * 
 * @author Robert Long (rlong @ unicon.net)
 */
@Data
@EqualsAndHashCode(of = "id")
@NoArgsConstructor
public class KalturaLtiRole implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String sakaiRole;
    private String ltiRole;
    private Date dateCreated;
    private Date dateModified;

    /**
     * Constructor using the Sakai role ID
     * 
     * @param sakaiRole the Sakai role ID
     */
    public KalturaLtiRole(String sakaiRole) {
        this(sakaiRole, Constants.DEFAULT_LTI_ROLE);
    }

    /**
     * Constructor using the Sakai role Id and the LTI role ID
     * 
     * @param sakaiRole the Sakai role ID
     * @param ltiRole the LTI role ID
     */
    public KalturaLtiRole(String sakaiRole, String ltiRole) {
        this(sakaiRole, ltiRole, new Date(), new Date());
    }

    /**
     * Constructor using the Sakai role Id, the LTI role ID, and active mapping
     * 
     * @param sakaiRole the Sakai role ID
     * @param ltiRole the LTI role ID
     * @param dateCreated the date of creation
     */
    public KalturaLtiRole(String sakaiRole, String ltiRole, Date dateCreated) {
        this(sakaiRole, ltiRole, dateCreated, new Date());
    }

    /**
     * Convenience constructor to ensure a valid {@link KalturaLtiRole} object exists
     * 
     * @param kalturaLtiRole the {@link KalturaLtiRole} object
     */
    public KalturaLtiRole(KalturaLtiRole kalturaLtiRole) {
        this(
            kalturaLtiRole.getSakaiRole(),
            kalturaLtiRole.getLtiRole(),
            kalturaLtiRole.getDateCreated(),
            kalturaLtiRole.getDateModified()
        );
    }

    /**
     * Full constructor
     * 
     * @param sakaiRole the Sakai role ID
     * @param ltiRole the LTI role ID
     * @param dateCreated the date of creation
     * @param dateModified the date of last modification
     */
    public KalturaLtiRole(String sakaiRole, String ltiRole, Date dateCreated, Date dateModified) {
        this.sakaiRole = sakaiRole;

        if (StringUtils.isBlank(ltiRole)) {
            this.ltiRole = Constants.DEFAULT_LTI_ROLE;
        } else {
            this.ltiRole = ltiRole;
        }

        if (dateCreated == null) {
            this.dateCreated = new Date();
        } else {
            this.dateCreated = dateCreated;
        }

        if (dateModified == null) {
            this.dateModified = new Date();
        } else {
            this.dateModified = dateModified;
        }
    }

    /**
     * Override to show this model as a JSON string
     */
    @Override
    public String toString() {
        return JsonUtil.parseToJson(this);
    }

    /**
     * Is this a fully-constructed {@link KalturaLtiRole} object?
     * 
     * @return true, if all required fields are valid
     */
    public boolean isValid() {
        if (StringUtils.isBlank(sakaiRole)) {
            return false;
        }
        if (StringUtils.isBlank(ltiRole)) {
            return false;
        }
        if (dateCreated == null) {
            return false;
        }
        if (dateModified == null) {
            return false;
        }

        return true;
    }

    /**
     * Copies non-empty fields from another {@link KalturaLtiRole} object
     * @param kalturaLtiRole
     */
    public void copy(KalturaLtiRole kalturaLtiRole) {
        if (StringUtils.isNotBlank(kalturaLtiRole.getSakaiRole())) {
            this.sakaiRole = kalturaLtiRole.getSakaiRole();
        }

        if (StringUtils.isNotBlank(kalturaLtiRole.getLtiRole())) {
            this.ltiRole = kalturaLtiRole.getLtiRole();
        }

        if (kalturaLtiRole.getDateCreated() != null) {
            this.dateCreated = kalturaLtiRole.getDateCreated();
        }

        if (dateModified == null) {
            this.dateModified = new Date();
        }
    }

}
