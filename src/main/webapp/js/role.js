/*
 * Javascript for role.jsp
 */

$(document).ready(function() {
    var allSakaiRoles = [];
    var availableSakaiRoles = [];
    var customRoleTableRow = $("#custom-role-table > tbody > .custom-role-table-row");
    $("#custom-role-table > tbody > .custom-role-table-row").remove();

    // TODO these need to be synchronous
    getExistingSakaiRoles();
    processRoleMappingData();

    // "Edit" button click handler
    $(".custom-role-button-edit").click(function(event) {
        event.preventDefault();

        var editRow = $(this).closest("tr");

        var idElement = $(editRow).find(".custom-role-id");
        var id = $(idElement).val();

        var sakaiRoleTextElement = $(editRow).find(".custom-role-sakai > .custom-role-sakai-text");
        var sakaiRole = $(sakaiRoleTextElement).text();
        
        
        var ltiRoleElement = $(editRow).find(".custom-role-lti > .custom-role-lti-text");
        var ltiRole = $(ltiRoleElement).text();
        var ltiRoleSelectElement = $(editRow).find(".custom-role-lti > .custom-role-lti-select");
        $.each($(ltiRoleSelectElement).find("option"), function(index, option) {
            if (this.text == ltiRole) {
                this.setAttribute('selected','selected');
            }
        });

        var activeElement = $(editRow).find(".custom-role-active > .custom-role-active-text");
        var active = $(activeElement).text();
        var activeSelectElement = $(editRow).find(".custom-role-active > .custom-role-active-select");
        $.each($(activeSelectElement).find("option"), function(index, option) {
            if (this.text == active) {
                this.setAttribute('selected','selected');
            }
        });

        toggleDataRowComponents(editRow, true);

        return false;
    });

    $(".custom-role-button-save").click(function(event) {
        event.preventDefault();

        var saveRow = $(this).closest("tr");

        var idElement = $(saveRow).find(".custom-role-id");
        var id = $(idElement).val();

        var sakaiRoleSelectElement = $(saveRow).find(".custom-role-sakai > .custom-role-sakai-select");
        var sakaiRole = $(sakaiRoleSelectElement).val();
        
        var ltiRoleSelectElement = $(saveRow).find(".custom-role-lti > .custom-role-lti-select");
        var ltiRole = $(ltiRoleSelectElement).val();

        var activeSelectElement = $(saveRow).find(".custom-role-active > .custom-role-active-select");
        var active = $(activeSelectElement).val();

        var data = [
            {
                "sakaiRole": sakaiRole,
                "ltiRole": ltiRole,
                "active": "true" == active ? true : false
            }
        ];

        kaltura.doPost(data, kaltura.roleUrl + "/" + id, function(success, rv) {
            toggleDataRowComponents(saveRow, false);
        });

        return false;
    });

    // "Cancel" button click handler
    $(".custom-role-button-cancel").click(function(event) {
        event.preventDefault();

        var cancelRow = $(this).closest("tr");

        toggleDataRowComponents(cancelRow, false);

        return false;
    });

    /* GET request for existing Sakai roles defined */
    function getExistingSakaiRoles() {
        kaltura.doGet(kaltura.roleUrl + "/sakai", function(success, data) {
            allSakaiRoles = [];

            $.each(data, function(index, sakaiRole) {
                allSakaiRoles.push(sakaiRole);
            });

            availableSakaiRoles = allSakaiRoles;
        });
    }

    /* GET request for role data */
    function processRoleMappingData() {
        kaltura.doGet(kaltura.roleUrl, function(success, data) {

            // remove existing roles from the available list
            $.each(data, function(index, roleMapping) {
                availableSakaiRoles = jQuery.grep(allSakaiRoles, function(role) {
                    return role !== roleMapping.sakaiRole;
                });
            });

            // update the data table rows
            $.each(data, function(index, roleMapping) {
                var newCustomRoleTableRow = customRoleTableRow;

                $(newCustomRoleTableRow).find(".custom-role-sakai > .custom-role-sakai-text").text(roleMapping.sakaiRole);
                $(newCustomRoleTableRow).find(".custom-role-lti > .custom-role-lti-text").text(roleMapping.ltiRole);
                $(newCustomRoleTableRow).find(".custom-role-active > .custom-role-active-text").text(roleMapping.active);
                $(newCustomRoleTableRow).find(".custom-role-button > .custom-role-id").val(roleMapping.id);

                appendRow(newCustomRoleTableRow);
            });

            addNewMappingRow();
        });
    }

    function toggleDataRowComponents(dataRow, editable) {
        var ltiRoleTextElement = $(dataRow).find(".custom-role-lti > .custom-role-lti-text");
        var activeTextElement = $(dataRow).find(".custom-role-active > .custom-role-active-text");

        var ltiRoleSelectElement = $(dataRow).find(".custom-role-lti > .custom-role-lti-select");
        var activeSelectElement = $(dataRow).find(".custom-role-active > .custom-role-active-select");

        if (editable) {
            $(ltiRoleTextElement).hide();
            $(activeTextElement).hide();

            $(ltiRoleSelectElement).show();
            $(activeSelectElement).show();

            $(dataRow).find(".custom-role-button-edit").hide();
            $(dataRow).find(".custom-role-button-save").show();
            $(dataRow).find(".custom-role-button-cancel").show();
        } else {
            $(ltiRoleTextElement).show();
            $(activeTextElement).show();

            $(ltiRoleSelectElement).hide();
            $(activeSelectElement).hide();

            $(dataRow).find(".custom-role-button-edit").show();
            $(dataRow).find(".custom-role-button-save").hide();
            $(dataRow).find(".custom-role-button-cancel").hide();
        }
    }

    function appendRow(row) {
        $("#custom-role-table").append(
            "<tr class='custom-role-table-row'>" + $(row).html() + "</tr>"
        );
    }

    function addNewMappingRow() {
        // add new mapping row
        var newCustomRoleTableRow = customRoleTableRow;
        toggleDataRowComponents(newCustomRoleTableRow, true);

        var sakaiRoleSelectElement = $(newCustomRoleTableRow).find(".custom-role-sakai-select");
        $(sakaiRoleSelectElement).show();
        var sakaiRoleTextElement = $(newCustomRoleTableRow).find(".custom-role-sakai-text");
        $(sakaiRoleTextElement).hide();

        $.each(availableSakaiRoles, function(index, availableSakaiRole) {
            $(sakaiRoleSelectElement).append($("<option/>", {value: availableSakaiRole, text: availableSakaiRole}));
        });

        $("#custom-role-table").append(
            "<tr class='custom-role-table-row'>" + $(newCustomRoleTableRow).html() + "</tr>"
        );
    }

});
