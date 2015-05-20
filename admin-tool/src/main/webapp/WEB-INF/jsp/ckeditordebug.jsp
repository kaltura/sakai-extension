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
    <script src="<c:url value='/js/main.js'/>" type="text/javascript"></script>
</head>
<body onload="<%=request.getAttribute("sakai.html.body.onload")%>">
<form method="post" action="/kaltura-lti/ckeditorcallback.htm">

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
</body>
</html>
