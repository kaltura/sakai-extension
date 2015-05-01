/*
 * Javascript for role.jsp
 */

$(document).ready(function() {
    var allSakaiRoles = [];
    var allLtiRoles = [];
    var sakaiLtiRoleMap = []; // ["sakaiRole:ltiRole", ...]
    var customRoleTableRow = $("#custom-role-table > tbody > .custom-role-table-row");
    toggleStatus(getCurrentElement($("#custom-role-table > tbody > .custom-role-table-row")));
    $("#custom-role-table > tbody > .custom-role-table-row").remove();

    // pre-load the data rows
    $.when(getExistingSakaiRoles(), getAllLtiRoles()).done(processRoleMappingData());

    /**
     * EVENT LISTENERS
     */

    /* "Edit" button click */
    $("[class*='custom-role-button-edit']").click(function(event) {
        event.preventDefault();

        var dataTable = $(this).closest("#custom-role-table");
        $.each(dataTable.find(".custom-role-table-row"), function(index, dataRow) {
            var element = getCurrentElement(dataRow);
            if (element.isNew()) {
                populateDropdown(element.sakaiSelect, allSakaiRoles, null);
                populateDropdown(element.ltiSelect, allLtiRoles, null);
            } else {
                toggleDataRowComponents(element, false);
            }
        });

        var element = getCurrentElement($(this).closest("tr"));

        toggleStatus(element, null);

        populateDropdown(element.sakaiSelect, allSakaiRoles, element.sakaiRole);

        populateDropdown(element.ltiSelect, allLtiRoles, element.ltiRole);
        filterLtiRoles(element);
        $.each($(element.ltiSelect).find("option"), function(index, option) {
            if (this.text == element.ltiRole) {
                this.setAttribute('selected','selected');
            }
        });

        toggleDataRowComponents(element, true);

        return false;
    });

    /* "Save" button click */
    $("[class*='custom-role-button-save'").click(function(event) {
        event.preventDefault();

        var element = getCurrentElement($(this).closest("tr"));

        toggleStatus(element, null);

        if (element.isNew()) {
            element.setId("");
            addNewMappingRow();
        }

        element.newSakaiRole = $(element.sakaiSelect).val();
        element.newLtiRole = $(element.ltiSelect).val();

        var data = [
            {
                "sakaiRole": element.newSakaiRole,
                "ltiRole": element.newLtiRole
            }
        ];

        // save the data row
        kaltura.doPost(data, kaltura.roleUrl + "/" + element.id, function(success, rv) {
            if (success) {
                var roleMapping = rv[0];
                element.setId(roleMapping.id);

                updateSakaiLtiRoleMap(buildRoleMapString(element.newSakaiRole, element.newLtiRole), buildRoleMapString(element.sakaiRole, element.ltiRole));

                $(element.ltiText).text(element.newLtiRole);
                $(element.sakaiText).text(element.newSakaiRole);
                toggleDataRowComponents(element, false);
            }

            toggleStatus(element, success);
        });

        toggleDataRowComponents(element, false);

        return false;
    });

    // "Cancel" button click
    $("[class*='custom-role-button-cancel']").click(function(event) {
        event.preventDefault();

        var element = getCurrentElement($(this).closest("tr"));

        if (element.isNew()) {
            populateDropdown(element.sakaiSelect, allSakaiRoles, null);
            populateDropdown(element.ltiSelect, allLtiRoles, null);
        } else {
            toggleStatus(element, null);
            toggleDataRowComponents(element, false);
        }

        return false;
    });

    // "Delete" button click
    $("[class*='custom-role-button-delete']").click(function(event) {
        event.preventDefault();

        var element = getCurrentElement($(this).closest("tr"));

        // save the data row
        kaltura.doDelete(kaltura.roleUrl + "/delete/" + element.id, function(success, rv) {
            if (success) {
                toggleDataRowComponents(element, false);
            }

            toggleStatus(element, !success);
            disableDataRow(element);
        });

        return false;
    });

    $("[class*='custom-role-lti-select']").focus(function(event) {
        var element = getCurrentElement($(this).closest("tr"));
        filterLtiRoles(element);
    });

    $("[class*='custom-role-sakai-select']").focus(function(event) {
        var element = getCurrentElement($(this).closest("tr"));
        filterLtiRoles(element);
    });

    $("[class*='custom-role-sakai-select']").change(function(event) {
        var element = getCurrentElement($(this).closest("tr"));
        filterLtiRoles(element);
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

                var element = getCurrentElement(newCustomRoleTableRow);

                $(element.sakaiText).text(roleMapping.sakaiRole);
                $(element.ltiText).text(roleMapping.ltiRole);
                element.setId(roleMapping.id);

                appendRow(element);

                updateSakaiLtiRoleMap(buildRoleMapString(roleMapping.sakaiRole, roleMapping.ltiRole), null);
            });

            addNewMappingRow();

            deferred.resolve();
        });

        return deferred.promise();
    }

    /* Show/hide a data row's components */
    function toggleDataRowComponents(element, editable) {
        if (editable) {
            $(element.sakaiText).hide();
            $(element.sakaiSelect).show();
            $(element.ltiText).hide();
            $(element.ltiSelect).show();

            $(element.buttonEdit).hide();
            $(element.buttonSave).show();
            $(element.buttonCancel).show();
            $(element.buttonDelete).show();
        } else {
            $(element.sakaiText).show();
            $(element.sakaiSelect).hide();
            $(element.ltiText).show();
            $(element.ltiSelect).hide();

            $(element.buttonEdit).show();
            $(element.buttonSave).hide();
            $(element.buttonCancel).hide();
            $(element.buttonDelete).hide();
        }

        if (element.isNew()) {
            $(element.buttonDelete).hide();
        }
    }

    /* Add a data row to the table */
    function appendRow(element) {
        $("#custom-role-table").append(
            "<tr class='custom-role-table-row'>" + $(element.row).html() + "</tr>"
        );
    }

    /* Add a new mapping data row to the table */
    function addNewMappingRow() {
        // add new mapping row
        var newCustomRoleTableRow = customRoleTableRow.clone(true);
        var element = getCurrentElement(newCustomRoleTableRow);

        toggleDataRowComponents(element, true);

        $(element.sakaiSelect).show();

        $(element.sakaiText).hide();

        populateDropdown(element.sakaiSelect, allSakaiRoles, null);
        populateDropdown(element.ltiSelect, allLtiRoles, null);
        toggleStatus(element, null);
        appendRow(element);
    }

    /* Populates a select dropdown, where the value and text are the same */
    function populateDropdown(selectElement, dataArray, defaultValue) {
        $(selectElement).html("");
        $(selectElement).append($("<option/>", {value: null, text: "-- Select --"}));
        $.each(dataArray, function(index, data) {
            $(selectElement).append($("<option/>", {value: data, text: data}));
        });
        $.each($(selectElement).find("option"), function(index, option) {
            if (this.value == defaultValue) {
                this.setAttribute('selected','selected');
            }
        });
    }

    function toggleStatus(element, success) {
        $(element.statusSuccess).hide();
        $(element.statusFail).hide();

        if (success != null) {
            if (success) {
                $(element.statusSuccess).show();
                $(element.statusFail).hide();
            } else {
                $(element.statusSuccess).hide();
                $(element.statusFail).show();
            }
        }
    }

    function disableDataRow(element) {
        $(element.ltiText).show();

        $(element.buttonEdit).hide();
        $(element.buttonSave).hide();
        $(element.buttonCancel).hide();
        $(element.buttonDelete).hide();

        $(element.row).addClass("danger");
    }

    function filterLtiRoles(element) {
        populateDropdown(element.ltiSelect, allLtiRoles, $(element.ltiSelect).val());

        var existingLtiRoles = [];
        $.each(sakaiLtiRoleMap, function(index, map) {
            var arr = map.split(":");
            if (arr[0] == element.sakaiRole) {
                existingLtiRoles.push(arr[1]);
            }
        });

        $.each($(element.ltiSelect).find("option"), function(index, option) {
            if ($.inArray(this.value, existingLtiRoles) > -1) {
                if (this.value != element.ltiRole) {
                    this.remove();
                }
            }
        });
    }

    function updateSakaiLtiRoleMap(add, remove) {
        if (add) {
            sakaiLtiRoleMap.push(add);
        }

        if (remove) {
            sakaiLtiRoleMap.splice($.inArray(remove, sakaiLtiRoleMap), 1);
        }
    }

    function buildRoleMapString(sakaiRole, ltiRole) {
        return sakaiRole + ":" + ltiRole;
    } 

    function getCurrentElement(dataRow) {
        var currentElement = {};

        currentElement.row = $(dataRow);
        currentElement.id = currentElement.row.find(".custom-role-id").val();
        currentElement.roleId = currentElement.row.find(".custom-role-id");
        currentElement.sakaiSelect = currentElement.row.find("[class*='custom-role-sakai-select']");
        currentElement.sakaiText = currentElement.row.find("[class*='custom-role-sakai-text']");
        currentElement.sakaiRole = currentElement.row.find("[class*='custom-role-sakai-text']").text();
        currentElement.newSakaiRole = null;
        currentElement.ltiSelect = currentElement.row.find("[class*='custom-role-lti-select']");
        currentElement.ltiText = currentElement.row.find("[class*='custom-role-lti-text']");
        currentElement.ltiRole = currentElement.row.find("[class*='custom-role-lti-text']").text();
        currentElement.newLtiRole = null;
        currentElement.buttonEdit = currentElement.row.find("[class*='custom-role-button-edit']");
        currentElement.buttonSave = currentElement.row.find("[class*='custom-role-button-save']");
        currentElement.buttonCancel = currentElement.row.find("[class*='custom-role-button-cancel']");
        currentElement.buttonDelete = currentElement.row.find("[class*='custom-role-button-delete']");
        currentElement.statusSuccess = currentElement.row.find("[class*='custom-role-status-success']");
        currentElement.statusFail = currentElement.row.find("[class*='custom-role-status-fail']");
        currentElement.isNew = function() {
            return (currentElement.id == "new");
        };
        currentElement.setId = function(newId) {
            currentElement.id = newId;
            $(currentElement.roleId).val(newId);
        }

        return currentElement;
    }

});
