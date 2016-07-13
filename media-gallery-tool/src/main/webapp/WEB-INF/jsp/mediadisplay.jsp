<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />

    <link media="all" href="/library/skin/tool_base.css" rel="stylesheet" type="text/css" />

    <script src="/library/js/headscripts.js" type="text/javascript"></script>

    <!-- Twitter Bootstrap -->
    <script src="<c:url value='/bootstrap/js/bootstrap.min.js'/>" type="text/javascript"></script>
    <link media="all" href="<c:url value='/bootstrap/css/bootstrap.min.css'/>" rel="stylesheet" type="text/css" />
    <link media="all" href="<c:url value='/bootstrap/css/bootstrap-theme.min.css'/>" rel="stylesheet" type="text/css" />

    <!-- Additional CSS -->
    <link media="all" href="<c:url value='/css/kaltura_ui.css'/>" rel="stylesheet" type="text/css" />

    <!-- Additional JavaScript -->
    <script src="/media-gallery-tool/js/kaltura-lti-display.js" type="text/javascript"></script>
</head>
<body onload=" <%=request.getAttribute("sakai.html.body.onload")%>; submitLtiLaunchForm()">
    ${returndata}
</body>
</html>
