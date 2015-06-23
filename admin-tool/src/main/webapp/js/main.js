/*
 * Javascript for main.jsp
 */

$(document).ready(function() {

    kaltura.main.displayGitVersioning();

    $(".alerts").click(function() {
        $(this).hide();
    });

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
        var gitVersioningElement = $(".git-versioning");
        kaltura.ajax.doGet(kaltura.ajax.gitUrl, function(success, data) {
            if (success) {
                var displayString = "";
                $.each(data, function(key, value) {
                    if ($.inArray(key, allowedGitKeys) > -1) {
                        displayString += key + ": " + value + "<br />";
                    }
                });
                $(gitVersioningElement).html(displayString);
            } else {
                $(gitVersioningElement).text("Error getting Git versioning data.");
            }
        });
    }

}
