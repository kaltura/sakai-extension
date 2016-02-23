/*
 * Copyright ©2016 Kaltura, Inc.
 */
package org.sakaiproject.kaltura.controllers;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.sakaiproject.kaltura.models.EmbeddedMediaModel;
import org.springframework.web.servlet.ModelAndView;

public class CKEditorCallbackController extends BaseController {

    @Override
    protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {
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
