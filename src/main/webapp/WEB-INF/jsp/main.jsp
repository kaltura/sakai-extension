 <?xml version="1.0" encoding="UTF-8" ?>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />

    <link media="all" href="/library/skin/tool_base.css" rel="stylesheet" type="text/css" />

    <script src="/library/js/headscripts.js" type="text/javascript"></script>
    <script src="/library/js/jquery/jquery-1.9.1.min.js" type="text/javascript"></script>

    <!-- Twitter Bootstrap -->
    <script src="<c:url value='/js/bootstrap.min.js'/>" type="text/javascript"></script>
    <link media="all" href="<c:url value='/css/bootstrap.min.css'/>" rel="stylesheet" type="text/css" />

    <!-- Additional CSS -->
    <link media="all" href="<c:url value='/css/main.css'/>" rel="stylesheet" type="text/css" />

    <!-- Additional JavaScript -->
    <script src="<c:url value='/js/scripts.js'/>" type="text/javascript"></script>
</head>
<body onload="<%=request.getAttribute("sakai.html.body.onload")%>">
    <h2>This is where the new Kaltura Sakai LTI tool shall reside... Stay tuned!</h2>

    <div class="panel panel-default">
        <div class="panel-heading">User</div>
        <div class="panel-body">
            <table id="user-table" class="table">
                <tr>
                    <th>Name</th>
                    <th>ID</th>
                    <th>EID</th>
                    <th>Email</th>
                    <th>Type</th>
                </tr>
                <tr>
                    <td><span id="user-data-name"></span></td>
                    <td><span id="user-data-id"></span></td>
                    <td><span id="user-data-eid"></span></td>
                    <td><span id="user-data-email"></span></td>
                    <td><span id="user-data-type"></span></td>
                </tr>
            </table>
            <div>JSON: <span id="user-data-json"></span></div>
        </div>
    </div>
    <div class="panel panel-default">
        <div class="panel-heading">User's Roles</div>
        <div class="panel-body">
            <table id="user-role-table" class="table">
                <tr>
                    <th>Sakai site</th>
                    <th>Sakai site role</th>
                    <th>LTI role</th>
                </tr>
            </table>
        </div>
    </div>
    <div class="panel panel-default">
        <div class="panel-heading">Role Maps</div>
        <div class="panel-body">
            <table id="role-table" class="table">
                <tr>
                    <th>Sakai role</th>
                    <th>LTI role</th>
                </tr>
            </table>
            <div>JSON: <span id="role-data-json"></span></div>
        </div>
    </div>
</body>
</html>
