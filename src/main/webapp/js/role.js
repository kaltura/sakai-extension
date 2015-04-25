/*
 * Javascript for main.jsp
 */

$(document).ready(function() {
    var sakaiRoles = ["Instructor", "maintain", "Student", "access", "Teaching Assistant"];
    var customRoleTableRow = $("#custom-role-table > tbody > .custom-role-table-row");
    $("#custom-role-table > tbody > .custom-role-table-row").remove();

    /* GET request for role data */
    doGet(roleUrl, function(success, data) {

        $.each(data, function(index, roleMapping) {
            var newCustomRoleTableRow = customRoleTableRow;

            $(newCustomRoleTableRow).find(".custom-role-sakai > .custom-role-sakai-select").html("");
            $.each(sakaiRoles, function(index, sakaiRole) {
                $(newCustomRoleTableRow).find(".custom-role-sakai > .custom-role-sakai-select").append($("<option/>", {value: sakaiRole, text: sakaiRole}));
            });
            $(newCustomRoleTableRow).find(".custom-role-sakai > .custom-role-sakai-text").text(roleMapping.sakaiRole);
            $(newCustomRoleTableRow).find(".custom-role-lti > .custom-role-lti-text").text(roleMapping.ltiRole);
            $(newCustomRoleTableRow).find(".custom-role-active > .custom-role-active-text").text(roleMapping.active);

            $("#custom-role-table").append(
                "<tr class='custom-role-table-row'>" + $(newCustomRoleTableRow).html() + "</tr>"
            );
        });
    });

    $(".custom-role-edit").click(function(event) {
        event.preventDefault();

        var editRow = $(this).closest("tr");
        var sakaiRoleTextElement = $(editRow).find(".custom-role-sakai > .custom-role-sakai-text");
        var sakaiRole = $(sakaiRoleTextElement).text();
        var sakaiRoleSelectElement = $(editRow).find(".custom-role-sakai > .custom-role-sakai-select");
        $(sakaiRoleSelectElement).show();
        $.each($(sakaiRoleSelectElement).find("option"), function(index, option) {
            if (this.text == active) {
                this.setAttribute('selected','selected');
            }
        });
        $(sakaiRoleTextElement).hide();
        
        var ltiRoleElement = $(editRow).find(".custom-role-lti > .custom-role-lti-text");
        var ltiRole = $(ltiRoleElement).text();
        var ltiRoleSelectElement = $(editRow).find(".custom-role-lti > .custom-role-lti-select");
        $(ltiRoleSelectElement).show();
        $.each($(ltiRoleSelectElement).find("option"), function(index, option) {
            if (this.text == ltiRole) {
                this.setAttribute('selected','selected');
            }
        });
        $(ltiRoleElement).hide();

        var activeElement = $(editRow).find(".custom-role-active > .custom-role-active-text");
        var active = $(activeElement).text();
        var activeSelectElement = $(editRow).find(".custom-role-active > .custom-role-active-select");
        $(activeSelectElement).show();
        $.each($(activeSelectElement).find("option"), function(index, option) {
            if (this.text == active) {
                this.setAttribute('selected','selected');
            }
        });
        $(activeElement).hide();

        $(this).hide();
        $(editRow).find(".custom-role-save").show();
        $(editRow).find(".custom-role-cancel").show();

        console.log("sakai: " + sakaiRole + ", lti: " + ltiRole + ", active: " + active);

        return false;
    })
});
