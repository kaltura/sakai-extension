/*
 * Copyright ©2016 Kaltura, Inc.
 */
package org.sakaiproject.kaltura.models.error;

/**
 * Error statement model
 * 
 * @author Robert Long (rlong @ unicon.net)
 *
 */
public class Error {

    private String text;
    private String action;
    private String identifier;

    public Error(String text, String action, String identifier) {
        this.text = text;
        this.action = action;
        this.identifier = identifier;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

}
