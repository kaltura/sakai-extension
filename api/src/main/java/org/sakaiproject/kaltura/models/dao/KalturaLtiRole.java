/*
 * Copyright ©2016 Kaltura, Inc.
 */
package org.sakaiproject.kaltura.models.dao;

import java.io.Serializable;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.sakaiproject.kaltura.Constants;
import org.sakaiproject.kaltura.utils.JsonUtil;

import com.google.gson.annotations.Expose;

/**
 * This is a Kaltura Sakai role to LTI role mapping, it represents a mapping object from the database
 * 
 * @author Robert Long (rlong @ unicon.net)
 */
public class KalturaLtiRole implements Serializable {

    private static final long serialVersionUID = 1L;

    @Expose
    private Long id;
    @Expose
    private String sakaiRole;
    @Expose
    private String ltiRole;
    @Expose
    private Date dateCreated;
    @Expose
    private Date dateModified;

    /**
     * Default constructor
     */
    public KalturaLtiRole(){};

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

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public String getSakaiRole() {
        return sakaiRole;
    }
    public void setSakaiRole(String sakaiRole) {
        this.sakaiRole = sakaiRole;
    }

    public String getLtiRole() {
        return ltiRole;
    }
    public void setLtiRole(String ltiRole) {
        this.ltiRole = ltiRole;
    }

    public Date getDateCreated() {
        return dateCreated;
    }
    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public Date getDateModified() {
        return dateModified;
    }
    public void setDateModified(Date dateModified) {
        this.dateModified = dateModified;
    }

    /**
     * Override to show this model as a JSON string
     */
    @Override
    public String toString() {
        return JsonUtil.parseToJson(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof KalturaLtiRole))
            return false;
        if (obj == this)
            return true;

        KalturaLtiRole other = (KalturaLtiRole) obj;
        if (this.id == null || other.id == null)  {
            return false;
        } else {
            return this.id.equals(other.id); // use id only if set
        }
    }

    @Override
    public int hashCode() {
        return id.intValue();
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
