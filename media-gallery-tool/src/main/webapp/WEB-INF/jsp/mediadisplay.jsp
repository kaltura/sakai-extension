<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <%@ include file="head.jsp" %>
</head>
<body onload="<%=request.getAttribute("sakai.html.body.onload")%>; submitLtiLaunchForm()">
    ${returndata}

    <%@ include file="body-js.jsp" %>
</body>
</html>