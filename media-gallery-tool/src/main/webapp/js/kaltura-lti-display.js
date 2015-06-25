var resizeBody = function() {
	var windowHeight = window.parent.$(window.parent).height();
	var topnavHeight = window.parent.$("#topnav").outerHeight();
	var portletTitleWrapHeight = window.parent.$(".portletTitleWrap").outerHeight();
	var contentHeight = window.parent.$("#content").outerHeight();
	var footerHeight = window.parent.$("#footer").outerHeight();
	
	var iframeHeight = windowHeight - topnavHeight - portletTitleWrapHeight - footerHeight - contentHeight - 110;
	$("#KalturaLtiContent").css("height", iframeHeight);
};

var submitLtiLaunchForm = function() {
	document.ltiLaunchForm.submit();
};
