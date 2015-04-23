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
package org.sakaiproject.kaltura.models.db;

import java.io.Serializable;
import java.util.Date;

import org.sakaiproject.kaltura.Constants;
import org.sakaiproject.kaltura.utils.common.JsonUtil;

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
    private Boolean active;
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
        this(sakaiRole, ltiRole, true);
    }

    /**
     * Constructor using the Sakai role Id, the LTI role ID, and active mapping
     * 
     * @param sakaiRole the Sakai role ID
     * @param ltiRole the LTI role ID
     * @param active is this mapping active?
     */
    public KalturaLtiRole(String sakaiRole, String ltiRole, Boolean active) {
        this(sakaiRole, ltiRole, active, new Date(), new Date());
    }

    /**
     * Constructor using the Sakai role Id, the LTI role ID, and active mapping
     * 
     * @param sakaiRole the Sakai role ID
     * @param ltiRole the LTI role ID
     * @param active is this mapping active?
     * @param dateCreated the date of creation
     */
    public KalturaLtiRole(String sakaiRole, String ltiRole, Boolean active, Date dateCreated) {
        this(sakaiRole, ltiRole, active, dateCreated, new Date());
    }

    /**
     * Full constructor
     * 
     * @param sakaiRole the Sakai role ID
     * @param ltiRole the LTI role ID
     * @param active is this mapping active?
     * @param dateCreated the date of creation
     * @param dateModified the date of last modification
     */
    public KalturaLtiRole(String sakaiRole, String ltiRole, Boolean active, Date dateCreated, Date dateModified) {
        this.sakaiRole = sakaiRole;
        this.ltiRole = ltiRole;
        this.active = active;
        this.dateCreated = dateCreated;
        this.dateModified = dateModified;
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

    public Boolean isActive() {
        return active;
    }
    public void setActive(Boolean active) {
        this.active = active;
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
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        KalturaLtiRole other = (KalturaLtiRole) obj;
        if (id == null || other.id == null)  {
            return false;
        } else {
            return id.equals(other.id); // use id only if set
        }
    }

}
