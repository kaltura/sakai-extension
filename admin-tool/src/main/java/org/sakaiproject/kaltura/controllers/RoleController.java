/*
 * Copyright ©2016 Kaltura, Inc.
 */
package org.sakaiproject.kaltura.controllers;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

/**
 * Controller to handle the role.jsp view
 * 
 * @author Robert Long (rlong @ unicon.net)
 *
 */
public class RoleController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(RoleController.class);

    /* (non-Javadoc)
     * @see org.springframework.web.servlet.mvc.AbstractController#handleRequestInternal(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String,Object> model = new HashMap<String,Object>();

        return new ModelAndView("role", model);
    }

}
