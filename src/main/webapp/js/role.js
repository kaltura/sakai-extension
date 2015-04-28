/*
 * Javascript for role.jsp
 */

$(document).ready(function() {
    var allSakaiRoles = [];
    var allLtiRoles = [];
    var customRoleTableRow = $("#custom-role-table > tbody > .custom-role-table-row");
    $("#custom-role-table > tbody > .custom-role-table-row").remove();

    // pre-load the data rows
    $.when(getExistingSakaiRoles(), getAllLtiRoles()).done(processRoleMappingData());

    /**
     * EVENT LISTENERS
     */

    /* "Edit" button click */
    $("[class*='custom-role-button-edit']").click(function(event) {
        event.preventDefault();

        var editRow = $(this).closest("tr");

        var idElement = $(editRow).find(".custom-role-id");
        var id = $(idElement).val();

        var sakaiRoleTextElement = $(editRow).find("[class*='custom-role-sakai-text']");
        var sakaiRole = $(sakaiRoleTextElement).text();
        
        var ltiRoleElement = $(editRow).find("[class*='custom-role-lti-text']");
        var ltiRole = $(ltiRoleElement).text();
        var ltiRoleSelectElement = $(editRow).find("[class*='custom-role-lti-select']");
        populateDropdown(ltiRoleSelectElement, allLtiRoles);
        $.each($(ltiRoleSelectElement).find("option"), function(index, option) {
            if (this.text == ltiRole) {
                this.setAttribute('selected','selected');
            }
        });

        toggleDataRowComponents(editRow, true);

        return false;
    });

    /* "Save" button click */
    $("[class*='custom-role-button-save'").click(function(event) {
        event.preventDefault();

        var saveRow = $(this).closest("tr");

        var idElement = $(saveRow).find(".custom-role-id");
        var id = $(idElement).val();

        var sakaiRoleSelectElement = $(saveRow).find("[class*='custom-role-sakai-select']");
        var sakaiRole = $(sakaiRoleSelectElement).val();
        
        var ltiRoleSelectElement = $(saveRow).find("[class*='custom-role-lti-select']");
        var ltiRole = $(ltiRoleSelectElement).val();

        var data = [
            {
                "sakaiRole": sakaiRole,
                "ltiRoles": ltiRole
            }
        ];

        // save the data row
        kaltura.doPost(data, kaltura.roleUrl + "/" + id, function(success, rv) {
            if (success) {
                toggleDataRowComponents(saveRow, false);
                $(saveRow).find("[class*='custom-role-status-success;]").show();
            } else {
                $(saveRow).find("[class*='custom-role-status-fail']").show();
            }
        });

        toggleDataRowComponents(saveRow, false);

        // this is a new mapping, create another new row
        if (id == null || id == "") {
            addNewMappingRow();
        }

        return false;
    });

    // "Cancel" button click
    $("[class*='custom-role-button-cancel']").click(function(event) {
        event.preventDefault();

        var cancelRow = $(this).closest("tr");

        toggleDataRowComponents(cancelRow, false);

        return false;
    });

    /* GET request for existing Sakai roles defined */
    function getExistingSakaiRoles() {
        var deferred = $.Deferred();

        kaltura.doGet(kaltura.roleUrl + "/sakai", function(success, data) {
            allSakaiRoles = [];

            $.each(data, function(index, sakaiRole) {
                allSakaiRoles.push(sakaiRole);
            });

            deferred.resolve();
        });

        return deferred.promise();
    }

    /* GET request for existing LTI roles */
    function getAllLtiRoles() {
        var deferred = $.Deferred();

        kaltura.doGet(kaltura.roleUrl + "/lti", function(success, data) {
            allLtiRoles = [];

            $.each(data, function(index, ltiRole) {
                allLtiRoles.push(ltiRole);
            });

            deferred.resolve();
        });

        return deferred.promise();
    }

    /* GET request for role data */
    function processRoleMappingData() {
        var deferred = $.Deferred();

        kaltura.doGet(kaltura.roleUrl, function(success, data) {

            // update the data table rows
            $.each(data, function(index, roleMapping) {
                var newCustomRoleTableRow = customRoleTableRow;

                $(newCustomRoleTableRow).find(".custom-role-sakai > .custom-role-sakai-text").text(roleMapping.sakaiRole);
                $(newCustomRoleTableRow).find(".custom-role-lti > .custom-role-lti-text").text(roleMapping.ltiRole);
                $(newCustomRoleTableRow).find(".custom-role-button > .custom-role-id").val(roleMapping.id);

                appendRow(newCustomRoleTableRow);
            });

            addNewMappingRow();

            deferred.resolve();
        });

        return deferred.promise();
    }

    /* Show/hide a data row's components */
    function toggleDataRowComponents(dataRow, editable) {
        var ltiRoleSelectElement = $(dataRow).find("[class*='custom-role-lti-select']");
        var ltiRoleTextElement = $(dataRow).find("[class*='custom-role-lti-text']");

        if (editable) {
            $(ltiRoleTextElement).hide();
            $(ltiRoleSelectElement).show();

            $(dataRow).find("[class*='custom-role-button-edit']").hide();
            $(dataRow).find("[class*='custom-role-button-save']").show();
            $(dataRow).find("[class*='custom-role-button-cancel']").show();
        } else {
            $(ltiRoleTextElement).show();
            $(ltiRoleSelectElement).hide();

            $(dataRow).find("[class*='custom-role-button-edit']").show();
            $(dataRow).find("[class*='custom-role-button-save']").hide();
            $(dataRow).find("[class*='custom-role-button-cancel']").hide();
        }
    }

    /* Add a data row to the table */
    function appendRow(row) {
        $("#custom-role-table").append(
            "<tr class='custom-role-table-row'>" + $(row).html() + "</tr>"
        );
    }

    /* Add a new mapping data row to the table */
    function addNewMappingRow() {
        // add new mapping row
        var newCustomRoleTableRow = customRoleTableRow;
        toggleDataRowComponents(newCustomRoleTableRow, true);

        var sakaiRoleSelectElement = $(newCustomRoleTableRow).find("[class*='custom-role-sakai-select']");
        $(sakaiRoleSelectElement).show();

        var sakaiRoleTextElement = $(newCustomRoleTableRow).find("[class*='custom-role-sakai-text']");
        $(sakaiRoleTextElement).hide();

        populateDropdown(sakaiRoleSelectElement, allSakaiRoles);

        var ltiRoleSelectElement = $(newCustomRoleTableRow).find("[class*='custom-role-lti-select']");

        populateDropdown(ltiRoleSelectElement, allLtiRoles);

        appendRow(newCustomRoleTableRow);
    }

    /* Populates a select dropdown, where the value and text are the same */
    function populateDropdown(selectElement, dataArray) {
        $(selectElement).html("");
        $(selectElement).append($("<option/>", {value: null, text: "-- Select --"}));
        $.each(dataArray, function(index, data) {
            $(selectElement).append($("<option/>", {value: data, text: data}));
        });
    }

});
