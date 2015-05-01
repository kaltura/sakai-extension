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
    <script src="<c:url value='/bootstrap/js/bootstrap.min.js'/>" type="text/javascript"></script>
    <link media="all" href="<c:url value='/bootstrap/css/bootstrap.min.css'/>" rel="stylesheet" type="text/css" />
    <link media="all" href="<c:url value='/bootstrap/css/bootstrap-theme.min.css'/>" rel="stylesheet" type="text/css" />

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
                        <th></th>
                    </tr>
                    <tr class="custom-role-table-row">
                        <td class="custom-role-sakai">
                            <span class="custom-role-sakai-text"></span>
                            <select class="custom-role-sakai-select form-control collapse"></select>
                        </td>
                        <td class="custom-role-lti">
                            <span class="custom-role-lti-text"></span>
                            <select class="custom-role-lti-select form-control collapse"></select>
                        </td>
                        <td class="custom-role-button">
                            <input type="hidden" class="custom-role-id" value="new" />
                            <div>
                                <button class="custom-role-button-edit btn btn-primary">
                                    <span class="glyphicon glyphicon-pencil"></span> Edit
                                </button>
                                <button class="custom-role-button-save btn btn-primary collapse">Save</button>
                                <button class="custom-role-button-cancel btn btn-danger collapse">Cancel</button>
                                <button class="custom-role-button-delete btn btn-danger collapse">
                                    <span class="glyphicon glyphicon-remove"></span> Delete
                                </button>
                            </div>
                            <div class="custom-role-status-success alert alert-success">
                                <a href="#" class="close" data-dismiss="alert"><span class="glyphicon glyphicon-ok"></span></a>
                            </div>
                            <div class="custom-role-status-fail alert alert-error">
                                <a href="#" class="close" data-dismiss="alert"><span class="glyphicon glyphicon-remove"></span></a>
                            </div>
                        </td>
                    </tr>
                </table>
                
            </form>
        </div>
    </div>

</body>
</html>