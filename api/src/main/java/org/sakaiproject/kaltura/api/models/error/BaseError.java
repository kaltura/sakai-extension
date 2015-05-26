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
package org.sakaiproject.kaltura.api.models.error;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.sakaiproject.kaltura.utils.JsonUtil;

import com.google.gson.annotations.Expose;


/**
 * The model for errors processing
 * 
 * @author Robert Long (rlong @ unicon.net)
 */
public class BaseError {

    @Expose
    protected List<String> errors;

    public BaseError() {
        errors = new ArrayList<String>();
    }

    public List<String> getErrors() {
        return errors;
    }

    public void setErrors(List<String> errors) {
        this.errors = errors;
    }

    /**
     * Update the given list with the given value
     * 
     * @param list the list to update
     * @param value the value to add to the list
     */
    protected void update(List<String> list, String value) {
        if (StringUtils.isNotBlank(value)) {
            list.add(value);
        }
    }

    /**
     * Update the errors listing with an error
     * 
     * @param error the Error message object (may be null)
     */
    public void updateErrorList(Error error) {
        if (error != null) {
            updateErrorList(error.getText(), error.getAction(), error.getIdentifier());
        }
    }

    /**
     * Update the user processing errors listing with an error
     * 
     * @param text the error message text
     * @param action the action being performed (may be null)
     * @param identifier the identifier of the object that had the error (may be null)
     */
    public void updateErrorList(String text, String action, String identifier) {
    }

    /**
     * Add a list of errors to this error object
     * 
     * @param errors the errors list
     */
    public void updateErrorList(List<String> errors) {
        if (errors != null) {
            this.errors.addAll(errors);
        }
    }

    /**
     * Are all the lists in this model empty?
     * 
     * @return true, if all lists are empty
     */
    public boolean isEmpty() {
        return this.errors.isEmpty();
    }

    /**
     * Override to show this model as a JSON string
     */
    @Override
    public String toString() {
        return JsonUtil.parseToJson(this);
    }

}
