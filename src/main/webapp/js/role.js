/*
 * Javascript for role.jsp
 */

$(document).ready(function() {
    var allSakaiRoles = [];
    var allLtiRoles = [];
    var customRoleTableRow = $("#custom-role-table > tbody > .custom-role-table-row");
    toggleStatus(customRoleTableRow, null);
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

        toggleStatus(editRow, null);

        var id = getRowMappingId(editRow);

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

        toggleStatus(saveRow, null);

        var id = getRowMappingId(saveRow);
        if (id == "new") {
            id = "";
        }

        var sakaiRoleTextElement = $(saveRow).find("[class*='custom-role-sakai-text']");
        var sakaiRole = $(sakaiRoleTextElement).text();
        
        var ltiRoleSelectElement = $(saveRow).find("[class*='custom-role-lti-select']");
        var ltiRoleTextElement = $(saveRow).find("[class*='custom-role-lti-text']");
        var ltiRole = $(ltiRoleSelectElement).val();

        var data = [
            {
                "sakaiRole": sakaiRole,
                "ltiRole": ltiRole
            }
        ];

        // save the data row
        kaltura.doPost(data, kaltura.roleUrl + "/" + id, function(success, rv) {
            if (success) {
                $(ltiRoleTextElement).text(ltiRole);
                toggleDataRowComponents(saveRow, false);
            }

            toggleStatus(saveRow, success);
        });

        toggleDataRowComponents(saveRow, false);

        // this is a new mapping, create another new row
        if (id == "new") {
            addNewMappingRow();
        }

        return false;
    });

    // "Cancel" button click
    $("[class*='custom-role-button-cancel']").click(function(event) {
        event.preventDefault();

        var cancelRow = $(this).closest("tr");

        var id = getRowMappingId(cancelRow);

        if (id == "new") {
            $(cancelRow).remove();
            addNewMappingRow();
        } else {
            toggleStatus(cancelRow, null);
            toggleDataRowComponents(cancelRow, false);
        }

        return false;
    });

    // "Delete" button click
    $("[class*='custom-role-button-delete']").click(function(event) {
        event.preventDefault();

        var deleteRow = $(this).closest("tr");

        var id = getRowMappingId(deleteRow);

        // save the data row
        kaltura.doDelete(kaltura.roleUrl + "/delete/" + id, function(success, rv) {
            if (success) {
                var ltiRoleSelectElement = $(deleteRow).find("[class*='custom-role-lti-select']");
                var ltiRole = $(ltiRoleSelectElement).val();
                var ltiRoleTextElement = $(deleteRow).find("[class*='custom-role-lti-text']");

                $(ltiRoleTextElement).text(ltiRole);
                toggleDataRowComponents(deleteRow, false);
            }

            toggleStatus(deleteRow, !success);
            disableDataRow(deleteRow);
        });

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
                var newCustomRoleTableRow = customRoleTableRow.clone(true);

                $(newCustomRoleTableRow).find(".custom-role-sakai-text").text(roleMapping.sakaiRole);
                $(newCustomRoleTableRow).find(".custom-role-lti-text").text(roleMapping.ltiRole);
                $(newCustomRoleTableRow).find(".custom-role-id").val(roleMapping.id);

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
        var id = getRowMappingId(dataRow);

        if (editable) {
            $(ltiRoleTextElement).hide();
            $(ltiRoleSelectElement).show();

            $(dataRow).find("[class*='custom-role-button-edit']").hide();
            $(dataRow).find("[class*='custom-role-button-save']").show();
            $(dataRow).find("[class*='custom-role-button-cancel']").show();
            $(dataRow).find("[class*='custom-role-button-delete']").show();
        } else {
            $(ltiRoleTextElement).show();
            $(ltiRoleSelectElement).hide();

            $(dataRow).find("[class*='custom-role-button-edit']").show();
            $(dataRow).find("[class*='custom-role-button-save']").hide();
            $(dataRow).find("[class*='custom-role-button-cancel']").hide();
        }

        if (id == "new") {
            $(dataRow).find("[class*='custom-role-button-delete']").hide();
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
        var newCustomRoleTableRow = customRoleTableRow.clone(true);
        toggleDataRowComponents(newCustomRoleTableRow, true);

        var sakaiRoleSelectElement = $(newCustomRoleTableRow).find("[class*='custom-role-sakai-select']");
        $(sakaiRoleSelectElement).show();

        var sakaiRoleTextElement = $(newCustomRoleTableRow).find("[class*='custom-role-sakai-text']");
        $(sakaiRoleTextElement).hide();

        populateDropdown(sakaiRoleSelectElement, allSakaiRoles);

        var ltiRoleSelectElement = $(newCustomRoleTableRow).find("[class*='custom-role-lti-select']");

        populateDropdown(ltiRoleSelectElement, allLtiRoles);
        toggleStatus(newCustomRoleTableRow, null);
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

    function toggleStatus(dataRow, success) {
        $(dataRow).find("[class*='custom-role-status-success']").hide();
        $(dataRow).find("[class*='custom-role-status-fail']").hide();

        if (success != null) {
            if (success) {
                $(dataRow).find("[class*='custom-role-status-success']").show();
                $(dataRow).find("[class*='custom-role-status-fail']").hide();
            } else {
                $(dataRow).find("[class*='custom-role-status-success']").hide();
                $(dataRow).find("[class*='custom-role-status-fail']").show();
            }
        }
    }

    function disableDataRow(dataRow) {
        var ltiRoleTextElement = $(dataRow).find("[class*='custom-role-lti-text']");

        $(ltiRoleTextElement).show();

        $(dataRow).find("[class*='custom-role-button-edit']").hide();
        $(dataRow).find("[class*='custom-role-button-save']").hide();
        $(dataRow).find("[class*='custom-role-button-cancel']").hide();
        $(dataRow).find("[class*='custom-role-button-delete']").hide();

        $(dataRow).addClass("danger");
    }

    function getRowMappingId(dataRow) {
        var idElement = $(dataRow).find(".custom-role-id");
        var id = $(idElement).val();

        return id;
    }

});
