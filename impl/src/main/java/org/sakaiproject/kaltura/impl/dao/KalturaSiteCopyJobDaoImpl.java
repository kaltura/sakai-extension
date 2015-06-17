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
package org.sakaiproject.kaltura.impl.dao;

import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.sakaiproject.genericdao.api.search.Search;
import org.sakaiproject.genericdao.api.search.Order;
import org.sakaiproject.genericdao.api.search.Restriction;
import org.sakaiproject.genericdao.hibernate.HibernateGeneralGenericDao;
import org.sakaiproject.kaltura.api.dao.KalturaSiteCopyJobDao;
import org.sakaiproject.kaltura.models.dao.KalturaSiteCopyJob;


/**
 * DAO Interface for Kaltura custom role mappings
 * 
 * @author Robert Long (rlong @ unicon.net)
 */
public class KalturaSiteCopyJobDaoImpl extends HibernateGeneralGenericDao implements KalturaSiteCopyJobDao {

    private final Log log = LogFactory.getLog(KalturaSiteCopyJobDaoImpl.class);

    public void init() {
    }

    public void destroy(){
    }


    /**
     * Get the entire list of role mappings
     * 
     * @return list of {@link KalturaLtiRole} objects
     */
    public KalturaSiteCopyJob checkWorkQueue(String status){
        Order order = new Order("attempts");
        Restriction restriction = new Restriction("status",status);
        Search search = new Search(restriction, order);

        KalturaSiteCopyJob kalturaSiteCopyJob = findOneBySearch(KalturaSiteCopyJob.class, search);

        return kalturaSiteCopyJob;
    }

    /**
     * Get the role mapping associated with the role mapping ID
     * 
     * @param roleMappingId the role mapping ID
     * @return the {@link KalturaLtiRole} object
     */
    public KalturaSiteCopyJob getSiteCopyJob(Long jobId){
        if (jobId==null) {
            throw new IllegalArgumentException("Job ID cannot be blank.");
        }
        Search search = new Search("jobId", jobId.longValue());
        KalturaSiteCopyJob kalturaSiteCopyJob = findOneBySearch(KalturaSiteCopyJob.class, search);

        return kalturaSiteCopyJob;
    }

    public List<KalturaSiteCopyJob> getAllJobs(Long batchId){
        if(batchId==null){
            throw new IllegalArgumentException("Batch ID cannot be blank.");
        }
        Search search = new Search("batchId", batchId.longValue());
        List<KalturaSiteCopyJob> kalturaJobs = findBySearch(KalturaSiteCopyJob.class, search);

        return kalturaJobs;
    }

    /**
     * Add/update a new role mapping
     * 
     * @param kalturaLtiRole the {@link KalturaLtiRole} object to add
     * @return true, if added/updated successfully
     */
    public void save(KalturaSiteCopyJob kalturaSiteCopyJob){
        if (!kalturaSiteCopyJob.isValid()) {
            kalturaSiteCopyJob = new KalturaSiteCopyJob(kalturaSiteCopyJob);
        }

        commit(kalturaSiteCopyJob, false);

    }

    /**
     * Commit the transaction
     * 
     * @param kalturaLtiRole the {@link KalturaLtiRole} object
     * @param delete is this a delete operation?
     */
    public void commit(KalturaSiteCopyJob kalturaSiteCopyJob, boolean delete){
        getHibernateTemplate().flush();

        Session session = getSessionFactory().openSession();
        Transaction transaction = session.getTransaction();

        try {
            transaction = session.beginTransaction();

            if (delete) {
                session.delete(kalturaSiteCopyJob);
            } else {
                session.saveOrUpdate(kalturaSiteCopyJob);
            }
            transaction.commit();
        } catch ( Exception e) {
            if (delete) {
                log.error("Kaltura :: deleteKalturaSiteCopyJob : An error occurred deleting the kaltura site copy job details: " + kalturaSiteCopyJob.toString() + ", error: " + e, e);
            } else {
                log.error("Kaltura :: addKalturaSiteCopyJob : An error occurred persisting the kaltura site copy job details: " + kalturaSiteCopyJob.toString() + ", error: " + e, e);
            }

            if (transaction != null) {
                transaction.rollback();
            }
        } finally {
            session.close();
        }


    }    
}
