<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="org.sakaiproject.portal.util.PortalUtils" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html>
<head>
    <%@ include file="role-head.jsp" %>
</head>
<body onload="<%= request.getAttribute("sakai.html.body.onload") %>">
    <%@ include file="role-body.jsp" %>

    <!-- JavaScript -->
    <%= PortalUtils.includeLatestJQuery("kaltura.admin") %>
    <script type="text/javascript" src="<c:url value='/js/role.js'/><%= PortalUtils.getCDNQuery() %>"></script>
</body>
</html>


