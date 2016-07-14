/*
 * Copyright ©2016 Kaltura, Inc.
 */
package org.sakaiproject.kaltura.controllers;

import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.servlet.ModelAndView;

/**
 * Controller handles LTI requests to render Kaltura LTI stored media, after
 * media has been added to Kaltura Media Gallery
 *
 */
public class MediaDisplayController extends BaseController {
    final private Log log = LogFactory.getLog(getClass());

    /* (non-Javadoc)
     * @see org.springframework.web.servlet.mvc.AbstractController#handleRequestInternal(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {
        ControllerRequestParameters parameters = processRequestParameters(request);
        log.debug(parameters.toString());

        Map<String,Object> model = new HashMap<String,Object>();

        if (StringUtils.isBlank(parameters.getMediaItemUrl()) || StringUtils.equals("undefined", parameters.getMediaItemUrl())) {
            model.put("returndata", "<span data-toggle=\"tooltip\" title=\"kaltura-lti-url attribute missing, check antisamy configuration\">NO MEDIA</span>");
        } else {
            String decodedMediaItemUrl = URLDecoder.decode(parameters.getMediaItemUrl());
            String returnData[] = kalturaLTIService.launchLTIDisplayRequest(decodedMediaItemUrl, parameters.getUserId(), parameters.getSiteId());
            model.put("returndata", returnData[0]);
        }

        return new ModelAndView("mediadisplay", model);
    }

}
