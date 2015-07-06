var resizeBody = function() {
	var windowHeight = $(window.parent).height();
	var topnavHeight = $("#topnav").outerHeight();
	var portletTitleWrapHeight = $(".portletTitleWrap").outerHeight();
	var contentHeight = $("#content").outerHeight();
	var footerHeight = $("#footer").outerHeight();
	
	var iframeHeight = windowHeight - topnavHeight - portletTitleWrapHeight - footerHeight - contentHeight - 110;
	$("#KalturaLtiContent").css("height", iframeHeight);
};

var submitLtiLaunchForm = function() {
	document.ltiLaunchForm.submit();
};
