<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="org.sakaiproject.portal.util.PortalUtils" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!-- JavaScript -->
<%= PortalUtils.includeLatestJQuery("kaltura.my.media") %>
<script type="text/javascript" src="<%= PortalUtils.getScriptPath() %>headscripts.js<%= PortalUtils.getCDNQuery() %>"></script>
<script type="text/javascript" src="<c:url value='/js/kaltura-lti-display.js'/><%= PortalUtils.getCDNQuery() %>"></script>
