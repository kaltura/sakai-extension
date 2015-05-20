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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.kaltura.dao.models.EmbeddedMediaModel;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

public class CKEditorCallbackController extends AbstractController {
    final protected Log log = LogFactory.getLog(getClass());
    
    @Override
    protected ModelAndView handleRequestInternal(HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        EmbeddedMediaModel model = this.populateModel(request);
        return new ModelAndView("ckeditorcallback", "mediaitem", model);
    }

    private EmbeddedMediaModel populateModel(HttpServletRequest request) {
        String url = request.getParameter("url");
        String playerId = request.getParameter("playerId");
        String size = request.getParameter("size");
        String width = request.getParameter("width");
        String height = request.getParameter("height");
        String returnType = request.getParameter("return_type");
        String entryId = request.getParameter("entry_id");
        String owner = request.getParameter("owner");
        String title = request.getParameter("title");
        String duration = request.getParameter("duration");
        String description = request.getParameter("description");
        String createdAt = request.getParameter("createdAt");
        String tags = request.getParameter("tags");
        String thumbnailUrl = request.getParameter("thumbnailUrl");
        
        EmbeddedMediaModel model = new EmbeddedMediaModel();
        model.setCreatedAt(createdAt);
        model.setDescription(description);
        model.setDuration(duration);
        model.setEntryId(entryId);
        model.setHeight(height);
        model.setOwner(owner);
        model.setPlayerId(playerId);
        model.setReturnType(returnType);
        model.setSize(size);
        model.setTags(tags);
        model.setThumbnailUrl(thumbnailUrl);
        model.setTitle(title);
        model.setUrl(url);
        model.setWidth(width);
        
        return model;
    }
}
