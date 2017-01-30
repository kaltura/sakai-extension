var submitLtiLaunchForm = function(isDebug) {
    isDebug = isDebug || "off";
    if (isDebug != "on") {
        if ( $('#ltiLaunchForm').length ) {
            document.ltiLaunchForm.submit();
        }
    }
};
