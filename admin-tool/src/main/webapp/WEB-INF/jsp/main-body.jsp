<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<div class="main-header">
    <h3>Kaltura Administration</h3>
</div>
<div class="alerts">
    <div class="alert">
        <span id="alert-close" class="glyphicon"></span>
        <span class="alert-text"></span>
    </div>
</div>
<!-- Role mapping -->
<div class="panel-group content-section" id="accordion">
    <%@include file="role-body.jsp"%>
</div>
<!-- Git versioning -->
<div class="git-versioning-hidden" hidden></div>
<div class="git-versioning-shown"></div>