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
    <link media="all" href="<c:url value='/css/kaltura_ui.css'/>" rel="stylesheet" type="text/css" />

    <!-- Additional JavaScript -->
    <script src="<c:url value='/js/scripts.js'/>" type="text/javascript"></script>
    <script src="<c:url value='/js/role.js'/>" type="text/javascript"></script>

</head>
<body onload="<%=request.getAttribute("sakai.html.body.onload")%>">
    <a href="main.htm">Back to Main</a>
    <h2>Custom Role Mapping</h2>

    <div class="panel panel-default">
        <div class="panel-heading">Custom roles</div>
        <div class="panel-body">
            <form id="custom-role-form">
                <table id="custom-role-table" class="table">
                    <tr>
                        <th>Sakai Site Role</th>
                        <th>LTI Role</th>
                        <th>Active</th>
                        <th></th>
                    </tr>
                    <tr class="custom-role-table-row">
                        <td class="custom-role-sakai">
                            <span class="custom-role-sakai-text"></span>
                            <select class="custom-role-sakai-select" hidden></select>
                        </td>
                        <td class="custom-role-lti">
                            <span class="custom-role-lti-text"></span>
                            <select class="custom-role-lti-select" hidden>
                                <option value="Learner">Learner</option>
                                <option value="Instructor">Instructor</option>
                            </select>
                        </td>
                        <td class="custom-role-active">
                            <span class="custom-role-active-text"></span>
                            <select class="custom-role-active-select" hidden>
                                <option value="true">true</option>
                                <option value="false">false</option>
                            </select>
                        </td>
                        <td class="custom-role-button">
                            <input type="hidden" class="custom-role-id" />
                            <button class="custom-role-button-edit">Edit</button>
                            <button class="custom-role-button-save" hidden>Save</button>
                            <button class="custom-role-button-cancel" hidden>Cancel</button>
                        </td>
                    </tr>
                </table>
                
            </form>
        </div>
    </div>

</body>
</html>
