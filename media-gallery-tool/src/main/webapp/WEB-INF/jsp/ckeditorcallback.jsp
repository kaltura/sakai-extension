<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="org.sakaiproject.portal.util.PortalUtils" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <%@ include file="head.jsp" %>
</head>
<body onload="<%=request.getAttribute("sakai.html.body.onload")%>">
    <script>
        var mediaItem = {
            url: "${mediaitem.url}",
            playerId: "${mediaitem.playerId}",
            size: "${mediaitem.size}",
            width: "${mediaitem.width}",
            height: "${mediaitem.height}",
            returnType: "${mediaitem.returnType}",
            entryId: "${mediaitem.entryId}",
            owner: "${mediaitem.owner}",
            title: "${mediaitem.title}",
            duration: "${mediaitem.duration}",
            description: "${mediaitem.description}",
            createdAt: "${mediaitem.createdAt}",
            tags: "${mediaitem.tags}",
            thumbnailUrl: "${mediaitem.thumbnailUrl}"
        }
    </script>

    <%@ include file="body-js.jsp" %>
    <script src="<c:url value='/js/ckeditorinsert.js'/><%= PortalUtils.getCDNQuery() %>" type="text/javascript"></script>
</body>
</html>
