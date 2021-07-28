/*
 * Copyright ©2016 Kaltura, Inc.
 */
package org.sakaiproject.kaltura.controllers;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringEscapeUtils;
import org.sakaiproject.kaltura.models.EmbeddedMediaModel;
import org.springframework.web.servlet.ModelAndView;

public class CKEditorCallbackController extends BaseController {

    @Override
    protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {
        EmbeddedMediaModel model = this.populateModel(request);

        return new ModelAndView("ckeditorcallback", "mediaitem", model);
    }

    private EmbeddedMediaModel populateModel(HttpServletRequest request) {
        String url = StringEscapeUtils.escapeJavaScript(request.getParameter("url"));
        String playerId = StringEscapeUtils.escapeJavaScript(request.getParameter("playerId"));
        String size = StringEscapeUtils.escapeJavaScript(request.getParameter("size"));
        String width = StringEscapeUtils.escapeJavaScript(request.getParameter("width"));
        String height = StringEscapeUtils.escapeJavaScript(request.getParameter("height"));
        String returnType = StringEscapeUtils.escapeJavaScript(request.getParameter("return_type"));
        String entryId = StringEscapeUtils.escapeJavaScript(request.getParameter("entry_id"));
        String owner = StringEscapeUtils.escapeJavaScript(request.getParameter("owner"));
        String title = StringEscapeUtils.escapeJavaScript(request.getParameter("title"));
        String duration = StringEscapeUtils.escapeJavaScript(request.getParameter("duration"));
        String description = StringEscapeUtils.escapeJavaScript(request.getParameter("description"));
        String createdAt = StringEscapeUtils.escapeJavaScript(request.getParameter("createdAt"));
        String tags = StringEscapeUtils.escapeJavaScript(request.getParameter("tags"));
        String thumbnailUrl = StringEscapeUtils.escapeJavaScript(request.getParameter("thumbnailUrl"));

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
