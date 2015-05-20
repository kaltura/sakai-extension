/*
 * Javascript for role.jsp
 */

$(document).ready(function() {
    var allSakaiRoles = [];
    var allLtiRoles = [];
    var sakaiLtiRoleMap = []; // ["sakaiRole:ltiRole", ...]

    /* Edit existing roles */
    var customRoleTableRow = $("#custom-role-table > tbody > .custom-role-table-row");
    toggleStatus(getCurrentElement($("#custom-role-table > tbody > .custom-role-table-row")));
    $("#custom-role-table > tbody > .custom-role-table-row").remove();

    /* Add new role */
    var customRoleAddRow = $("#custom-role-table > tbody > .custom-role-add-table-row");
    $(".custom-role-add-table-row").remove();

    // pre-load the data rows
    $.when(getExistingSakaiRoles(), getAllLtiRoles()).done(processRoleMappingData());

    /**
     * EVENT LISTENERS
     */

    /* "Edit" button click */
    $("#custom-role-table").on("click", "[class*='custom-role-button-edit']", function(event) {
        event.preventDefault();

        resetEditRows(this);

        var element = getCurrentElement($(this).closest("tr"));

        toggleStatus(element, null);

        populateDropdown(element.sakaiSelect, allSakaiRoles, element.sakaiRole);

        populateDropdown(element.ltiSelect, allLtiRoles, element.ltiRole);
        filterLtiRoles(element);
        $.each($(element.ltiSelect).find("option"), function(index, option) {
            if (this.text == element.ltiRole) {
                this.setAttribute("selected","selected");
            }
        });

        toggleDataRowComponents(element, true);

        return false;
    });

    /* "Save" button click */
    $("#custom-role-table").on("click", "[class*='custom-role-button-save']", function(event) {
        event.preventDefault();

        var element = getCurrentElement($(this).closest("tr"));

        toggleStatus(element, null);

        if (element.isNewRole()) {
            element.setId("");
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

        //toggleDataRowComponents(element, false);

        return false;
    });

    /* "Cancel" button click */
    $("#custom-role-table").on("click", "[class*='custom-role-button-cancel']", function(event) {
        event.preventDefault();

        var element = getCurrentElement($(this).closest("tr"));

        if (element.isNewRole()) {
            $(element.row).remove();
            appendNewAddRoleButtonRow();
        } else {
            toggleStatus(element, null);
            toggleDataRowComponents(element, false);
        }

        return false;
    });

    /* "Delete" button click */
    $("#custom-role-table").on("click", "[class*='custom-role-button-delete']", function(event) {
        event.preventDefault();

        var element = getCurrentElement($(this).closest("tr"));

        // save the data row
        kaltura.doDelete(kaltura.roleUrl + "/delete/" + element.id, function(success, rv) {
            if (success) {
                toggleDataRowComponents(element, false);

                updateSakaiLtiRoleMap(null, buildRoleMapString(element.sakaiRole, element.ltiRole));
            }

            toggleStatus(element, !success);
            disableDataRow(element);
        });

        return false;
    });

    /* "Add" button click */
    $("#custom-role-table").on("click", "[class*='custom-role-button-add']", function(event) {
        event.preventDefault();

        resetEditRows(this);
        toggleAddNewRow(this, false);

        return false;
    });

    $("#custom-role-table").on("focus", "[class*='custom-role-lti-select']", function(event) {
        var element = getCurrentElement($(this).closest("tr"));
        element.newSakaiRole = $(element.sakaiSelect).val();
        filterLtiRoles(element);
    });

    $("#custom-role-table").on("focus", "[class*='custom-role-sakai-select']", function(event) {
        var element = getCurrentElement($(this).closest("tr"));
        filterLtiRoles(element);
    });

    $("#custom-role-table").on("change", "[class*='custom-role-sakai-select']", function(event) {
        var element = getCurrentElement($(this).closest("tr"));
        toggleSaveButton(element);
        filterLtiRoles(element);
    });

    $("#custom-role-table").on("change", "[class*='custom-role-lti-select']", function(event) {
        var element = getCurrentElement($(this).closest("tr"));
        toggleSaveButton(element);
        filterLtiRoles(element);
    });

    /**
     * ajax services
     */

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

    /* GET request for processing existing role data */
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

            appendNewAddRoleButtonRow();

            deferred.resolve();
        });

        return deferred.promise();
    }

    /**
     * Hide / show components
     */

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

        if (element.isNewRole()) {
            if (!editable) {
                appendNewAddRoleButtonRow();
            }
            
            $(element.buttonDelete).hide();
        }
    }

    /* Toggle the status icons */
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

    /* Disables a role row upon deletion */
    function disableDataRow(element) {
        $(element.ltiText).show();

        $(element.buttonEdit).remove();
        $(element.buttonSave).remove();
        $(element.buttonCancel).remove();
        $(element.buttonDelete).remove();

        $(element.row).addClass("danger");
        $(element.row).addClass("deleted");
    }

    /* Resets all of the rows that may have been set for editing */
    function resetEditRows(currentRow) {
        var dataTable = $(currentRow).closest("#custom-role-table");
        $.each(dataTable.find(".custom-role-table-row"), function(index, dataRow) {
            var element = getCurrentElement(dataRow);
            if (!element.isDeleted()) {
                if (element.isNewRole()) {
                    toggleAddNewRow(dataRow, true);
                } else {
                    toggleDataRowComponents(element, false);
                }
            }
        });
    }

    /* Adds a button to create new row  for adding a role */
    function toggleAddNewRow(element, showButton) {
        if (showButton) {
            $(element).remove();
            appendNewAddRoleButtonRow();
        } else {
            $(element).closest("tr").remove();
            appendNewMappingRow();
        }
    }

    /* Disables the Save button if there are not the appropriate amount of selections */
    function toggleSaveButton(element) {
        if ($(element.sakaiSelect).val() == "-1" || $(element.ltiSelect).val() == "-1") {
            $(element.buttonSave).prop("disabled", true);
        } else {
            $(element.buttonSave).prop("disabled", false);
        }
    }

    /**
     * Appending HTML
     */

    /* Add a data row to the table */
    function appendRow(element) {
        $("#custom-role-table").append(
            "<tr class='custom-role-table-row'>" + $(element.row).html() + "</tr>"
        );
    }

    /* Append a new mapping data row to the table */
    function appendNewMappingRow() {
        var newCustomRoleTableRow = customRoleTableRow.clone(true);
        var element = getCurrentElement(newCustomRoleTableRow);

        toggleDataRowComponents(element, true);

        $(element.sakaiSelect).show();

        $(element.sakaiText).hide();

        populateDropdown(element.sakaiSelect, allSakaiRoles, null);
        populateDropdown(element.ltiSelect, allLtiRoles, null);
        toggleStatus(element, null);
        toggleSaveButton(element);
        appendRow(element);
    }

    /* Append a new Add new role button */
    function appendNewAddRoleButtonRow() {
        var newCustomRoleAddTableRow = customRoleAddRow.clone(true);
        $("#custom-role-table").append(
            "<tr class='custom-role-add-table-row'>" + $(newCustomRoleAddTableRow).html() + "</tr>"
        );
    }

    /**
     * Data manipulation services
     */

    /* Populates a select dropdown, where the value and text are the same */
    function populateDropdown(selectElement, dataArray, defaultValue) {
        $(selectElement).html("");
        $(selectElement).append($("<option/>", {value: "-1", text: "-- Select --"}));
        $.each(dataArray, function(index, data) {
            $(selectElement).append($("<option/>", {value: data, text: data}));
        });
        $.each($(selectElement).find("option"), function(index, option) {
            if (this.value == defaultValue) {
                this.setAttribute('selected','selected');
            }
        });
    }

    /* Populates the available LTI roles based on the Sakai role */
    function filterLtiRoles(element) {
        populateDropdown(element.ltiSelect, allLtiRoles, $(element.ltiSelect).val());

        var existingLtiRoles = [];
        $.each(sakaiLtiRoleMap, function(index, map) {
            var arr = map.split(":");
            if (arr[0] == element.newSakaiRole) {
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

    /* Updates the mapping to keep track of the mapped roles */
    function updateSakaiLtiRoleMap(add, remove) {
        if (add) {
            sakaiLtiRoleMap.push(add);
        }

        if (remove) {
            sakaiLtiRoleMap.splice($.inArray(remove, sakaiLtiRoleMap), 1);
        }
    }

    /* Builds the string to store in the sakaiLtiRoleMap array */
    function buildRoleMapString(sakaiRole, ltiRole) {
        return sakaiRole + ":" + ltiRole;
    }

    /* Creates a model of the element to be created, deleted, or updated */
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
        currentElement.newRole = currentElement.id == "new";
        currentElement.isNewRole = function() {
            return currentElement.newRole;
        };
        currentElement.setId = function(newId) {
            currentElement.id = newId;
            $(currentElement.roleId).val(newId);
        };
        currentElement.isDeleted = function() {
            return $(currentElement.row).hasClass("deleted");
        };

        return currentElement;
    }

});
