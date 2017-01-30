<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <%@ include file="main-head.jsp" %>
    <%@ include file="role-head.jsp" %>
</head>
<body onload="<%= request.getAttribute("sakai.html.body.onload") %>">
    <%@ include file="main-body.jsp" %>

    <!-- JavaScript -->
    <%= PortalUtils.includeLatestJQuery("kaltura.admin") %>
    <script type="text/javascript" src="<%= PortalUtils.getScriptPath() %>headscripts.js<%= PortalUtils.getCDNQuery() %>"></script>
    <script type="text/javascript" src="<c:url value='/js/scripts.js'/><%= PortalUtils.getCDNQuery() %>"></script>
    <script type="text/javascript" src="<c:url value='/js/main.js'/><%= PortalUtils.getCDNQuery() %>"></script>
    <script type="text/javascript" src="<c:url value='/js/role.js'/><%= PortalUtils.getCDNQuery() %>"></script>
</body>
</html>
