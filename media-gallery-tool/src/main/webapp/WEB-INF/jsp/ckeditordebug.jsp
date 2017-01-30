<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <%@ include file="head.jsp" %>
</head>
<body onload="<%=request.getAttribute("sakai.html.body.onload")%>">
    <form method="post" action="/media-gallery-tool/ckeditorcallback.htm">
        <div class="form-group">
            <label for="url">URL</label>
            <input type="text" name="url" />
        </div>
        <div class="form-group">
            <label for="playerId">Player ID</label>
            <input type="text" name="playerId" />
        </div>
        <div class="form-group">
            <label for="size">Size</label>
            <input type="text" name="size" />
        </div>
        <div class="form-group">
            <label for="width">Width</label>
            <input type="text" name="width" />
        </div>
        <div class="form-group">
            <label for="height">Height</label>
            <input type="text" name="height" />
        </div>
        <div class="form-group">
            <label for="returnType">Return Type</label>
            <input type="text" name="returnType" />
        </div>
        <div class="form-group">
            <label for="entryId">Entry ID</label>
            <input type="text" name="entryId" />
        </div>
        <div class="form-group">
            <label for="owner">Owner</label>
            <input type="text" name="owner" />
        </div>
        <div class="form-group">
            <label for="title">Title</label>
            <input type="text" name="title" />
        </div>
        <div class="form-group">
            <label for="duration">Duration</label>
            <input type="text" name="duration" />
        </div>
        <div class="form-group">
            <label for="description">Description</label>
            <input type="text" name="description" />
        </div>
        <div class="form-group">
            <label for="createdAt">Created At</label>
            <input type="text" name="createdAt" />
        </div>
        <div class="form-group">
            <label for="tags">Tags</label>
            <input type="text" name="tags" />
        </div>
        <div class="form-group">
            <label for="thumbnailUrl">Thumbnail URL</label>
            <input type="text" name="thumbnailUrl" />
        </div>

        <input type="submit" />
    </form>

    <%@ include file="body-js.jsp" %>
</body>
</html>
