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
package org.sakaiproject.kaltura.controllers;

import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.kaltura.services.KalturaLTIService;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

/**
 * Controller handles LTI requests to render Kaltura LTI stored media, after
 * media has been added to Kaltura Media Gallery
 * @author mgillian
 *
 */
public class MediaDisplayController extends AbstractController {
    final protected Log log = LogFactory.getLog(getClass());

    private KalturaLTIService kalturaLTIService;
    public void setKalturaLTIService(KalturaLTIService kalturaLTIService) {
        this.kalturaLTIService = kalturaLTIService;
    }

    /* (non-Javadoc)
     * @see org.springframework.web.servlet.mvc.AbstractController#handleRequestInternal(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String,Object> model = new HashMap<String,Object>();
        
        String mediaItemUrl = request.getParameter("mediaitemurl");
        String userId = request.getParameter("userid");
        String siteId = request.getParameter("siteid");
        log.error("request params: mediaitemurl:userid:siteid [" + mediaItemUrl + ":" + userId + ":" + siteId + "]");
        if (StringUtils.isEmpty(mediaItemUrl)) {
        	model.put("returndata",  "NO MEDIA");
        } else {
	        String decodedMediaItemUrl = URLDecoder.decode(mediaItemUrl);
	        String retval[] = kalturaLTIService.launchLTIDisplayRequest(decodedMediaItemUrl, userId, siteId);
	        model.put("returndata", retval[0]);
        }
        return new ModelAndView("mediadisplay", model);
    }

}
