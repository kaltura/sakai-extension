<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <%@ include file="head.jsp" %>
</head>
<body onload="<%=request.getAttribute("sakai.html.body.onload")%>; submitLtiLaunchForm('${isDebug}')">
    ${returndata}
    <div id="KalturaLtiContent" />

    <%@ include file="body-js.jsp" %>
</body>
</html>