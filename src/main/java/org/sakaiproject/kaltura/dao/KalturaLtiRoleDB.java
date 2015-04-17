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
package org.sakaiproject.kaltura.dao;

import java.io.Serializable;
import java.util.Date;

/**
 * This is a Kaltura Sakai role to LTI role mapping, it represents a mapping object
 * 
 * @author Robert Long (rlong @ unicon.net)
 */
public class KalturaLtiRoleDB implements Serializable {

    private static final long serialVersionUID = 1L;

    private final static String DEFAULT_LTI_ROLE = "Learner";

    private Long id;
    private String sakaiRole;
    private String ltiRole;
    private Boolean active;
    private Date dateCreated;
    private Date lastModified;

    public KalturaLtiRoleDB(Long id, String sakaiRole) {
        this(id, sakaiRole, DEFAULT_LTI_ROLE);
    }

    public KalturaLtiRoleDB(Long id, String sakaiRole, String ltiRole) {
        this(id, sakaiRole, ltiRole, true);
    }

    public KalturaLtiRoleDB(Long id, String sakaiRole, String ltiRole, Boolean active) {
        super();
        this.id = id;
        this.sakaiRole = sakaiRole;
        this.ltiRole = ltiRole;
        this.active = active;
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

    public Date getLastModified() {
        return lastModified;
    }
    public void setLastModified(Date lastModified) {
        this.lastModified = lastModified;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "{KalturaLtiRole:"
                + ":id=" + id
                + ":sakai_role:=" + sakaiRole
                + ":lti_role=" + ltiRole
                + ":active=" + active
                + ":date_created=" + dateCreated
                + ":last_modified_date=" + lastModified
                + "}";
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
        KalturaLtiRoleDB other = (KalturaLtiRoleDB) obj;
        if (id == null || other.id == null)  {
            return false;
        } else {
            return id.equals(other.id); // use id only if set
        }
    }

}
