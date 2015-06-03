/*
 * Javascript for main.jsp
 */

$(document).ready(function() {

    $(".alerts").click(function() {
        $(this).hide();
    });

})

kaltura.main = kaltura.main || {

    displayAlert: function (text, isError) {
        var alertsParent = $("[class*='alerts']");
        kaltura.displayAlert(alertsParent, text, isError);
    }

}