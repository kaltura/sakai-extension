var resizeBody = function() {
    var windowHeight = $(window.parent).height() ? $(window.parent).height() : 0;
    var topnavHeight = $("#topnav").outerHeight() ? $("#topnav").outerHeight() : 0;
    var portletTitleWrapHeight = $(".portletTitleWrap").outerHeight() ? $(".portletTitleWrap").outerHeight() : 0;
    var contentHeight = $("#content").outerHeight() ? $("#content").outerHeight() : 0;
    var footerHeight = $("#footer").outerHeight() ? $("#footer").outerHeight() : 0;

    var iframeHeight = windowHeight - topnavHeight - portletTitleWrapHeight - footerHeight - contentHeight - 110;
    $("#KalturaLtiContent").css("height", iframeHeight);
};

var submitLtiLaunchForm = function(isDebug) {
    isDebug = isDebug || "off";
    if (isDebug != "on") {
        document.ltiLaunchForm.submit();
    }
};
