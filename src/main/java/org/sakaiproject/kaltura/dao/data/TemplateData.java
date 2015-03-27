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
package org.sakaiproject.kaltura.dao.data;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.kaltura.dao.Database;
import org.sakaiproject.kaltura.dao.sql.TemplateSql;

/**
 * @author Robert Long (rlong @ unicon.net)
 */
public class TemplateData extends Database {

    private final Log log = LogFactory.getLog(TemplateData.class);

    public List<String> template() {
        PreparedStatement preparedStatement = null;
        List<String> rv = new ArrayList<String>();

        try {
            // TODO get SQL statement
            String query = TemplateSql.getTemplateSql();

            preparedStatement = createPreparedStatement(preparedStatement, query);
            // set vars
            preparedStatement.setString(1, "");

            ResultSet resultSet = executeQueryPreparedStatement(preparedStatement);

            while (resultSet.next()) {
                rv.add(resultSet.getString(1));
            }
        } catch (Exception e) {
            log.error("Error getting data: " + e, e);
        } finally {
            closePreparedStatement(preparedStatement);
        }

        return rv;
    }

}
