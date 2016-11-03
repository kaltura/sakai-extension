/*
 * Javascript for main.jsp
 */

$(document).ready(function() {

    kaltura.main.displayGitVersioning();

    $(".alerts").click(function() {
        $(this).hide();
    });

    setMainFrameHeight(window.name);
});

kaltura.main = kaltura.main || {

    displayAlert: function (text, isError) {
        var alertsParent = $("[class*='alerts']");
        kaltura.alert.displayAlert(alertsParent, text, isError);
    },

    displayGitVersioning: function() {
        var allowedGitKeys = [
            "branch",
            "remoteOriginUrl",
            "commitIdAbbrev",
            "commitTime",
            "buildVersion"
        ];
        var gitVersioningHiddenElement = $(".git-versioning-hidden");
        var gitVersioningShownElement = $(".git-versioning-shown");
        kaltura.ajax.doGet(kaltura.ajax.gitUrl, function(success, data) {
            if (success) {
                var displayString = "";
                $.each(data, function(key, value) {
                    if ($.inArray(key, allowedGitKeys) > -1) {
                        displayString += key + ": " + value + "<br />";
                    }
                });
                $(gitVersioningHiddenElement).html(displayString);
                $(gitVersioningShownElement).html(data.branch + " | " + data.commitIdAbbrev);
            } else {
                $(gitVersioningHiddenElement).text("Error getting Git versioning data.");
                $(gitVersioningShownElement).html("kaltura-sakai-extension version: N/A");
            }
        });
    }

}
