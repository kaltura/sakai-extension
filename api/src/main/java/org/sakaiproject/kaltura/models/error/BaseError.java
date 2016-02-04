/*
 * Copyright ©2016 Kaltura, Inc.
 */
package org.sakaiproject.kaltura.models.error;

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
