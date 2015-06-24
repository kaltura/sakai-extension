/*
 * Javascript for role.jsp
 */

var allSakaiRoles;
var allLtiRoles;
var sakaiLtiRoleMap; // ["sakaiRole:ltiRole", ...]
var customRoleTableRow;
var customRoleAddRow;

$(document).ready(function() {

    allSakaiRoles = [];
    allLtiRoles = [];
    sakaiLtiRoleMap = []; // ["sakaiRole:ltiRole", ...]

    /* Edit existing roles */
    customRoleTableRow = $("#custom-role-table > tbody > .custom-role-table-row");
    kaltura.role.getCurrentElement($("#custom-role-table > tbody > .custom-role-table-row")).setStatus(null);
    $("#custom-role-table > tbody > .custom-role-table-row").remove();

    /* Add new role */
    customRoleAddRow = $("#custom-role-table > tbody > .custom-role-add-table-row");
    $(".custom-role-add-table-row").remove();

    // pre-load the data rows
    $.when(kaltura.role.getExistingSakaiRoles(), kaltura.role.getAllLtiRoles()).done(kaltura.role.processRoleMappingData());

    /**
     * EVENT LISTENERS
     */

    /* "Edit" button click */
    $("#custom-role-table").on("click", "[class*='custom-role-button-edit']", function(event) {
        event.preventDefault();

        kaltura.role.resetEditRows(this);

        var element = kaltura.role.getCurrentElement($(this).closest("tr"));

        element.setStatus(null);

        kaltura.role.populateDropdown(element.sakaiSelect, allSakaiRoles, element.sakaiRole);

        kaltura.role.populateDropdown(element.ltiSelect, allLtiRoles, element.ltiRole);
        kaltura.role.filterLtiRoles(element);
        $.each($(element.ltiSelect).find("option"), function(index, option) {
            if (this.text == element.ltiRole) {
                this.setAttribute("selected","selected");
            }
        });

        kaltura.role.toggleDataRowComponents(element, true);

        return false;
    });

    /* "Save" button click */
    $("#custom-role-table").on("click", "[class*='custom-role-button-save']", function(event) {
        event.preventDefault();

        var element = kaltura.role.getCurrentElement($(this).closest("tr"));

        element.setStatus(null);

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
        kaltura.ajax.doPost(data, kaltura.ajax.roleUrl + "/" + element.id, function(success, rv) {
            if (success) {
                var roleMapping = rv[0];
                element.setId(roleMapping.id);

                kaltura.role.updateSakaiLtiRoleMap(kaltura.role.buildRoleMapString(element.newSakaiRole, element.newLtiRole), kaltura.role.buildRoleMapString(element.sakaiRole, element.ltiRole));

                $(element.ltiText).text(element.newLtiRole);
                $(element.sakaiText).text(element.newSakaiRole);
                kaltura.role.toggleDataRowComponents(element, false);

                element.setStatus(success);

                kaltura.main.displayAlert("The role mapping was saved successfully.", false);
            } else {
                kaltura.main.displayAlert("There was an error saving the role mapping. Please try again.", true);
            }

        });

        return false;
    });

    /* "Cancel" button click */
    $("#custom-role-table").on("click", "[class*='custom-role-button-cancel']", function(event) {
        event.preventDefault();

        var element = kaltura.role.getCurrentElement($(this).closest("tr"));

        if (element.isNewRole()) {
            $(element.row).remove();
            kaltura.role.appendNewAddRoleButtonRow();
        } else {
            element.setStatus(null);
            kaltura.role.toggleDataRowComponents(element, false);
        }

        return false;
    });

    /* "Delete" button click */
    $("#custom-role-table").on("click", "[class*='custom-role-button-delete']", function(event) {
        event.preventDefault();

        var element = kaltura.role.getCurrentElement($(this).closest("tr"));

        // save the data row
        kaltura.ajax.doDelete(kaltura.ajax.roleUrl + "/delete/" + element.id, function(success, rv) {
            if (success) {
                kaltura.role.toggleDataRowComponents(element, false);

                kaltura.role.updateSakaiLtiRoleMap(null, kaltura.role.buildRoleMapString(element.sakaiRole, element.ltiRole));

                element.setStatus(!success);
                kaltura.role.disableDataRow(element);

                kaltura.main.displayAlert("The role mapping was deleted successfully.", false);
            } else {
                kaltura.main.displayAlert("There was an error saving the role mapping. Please try again.", true);
            }
        });

        return false;
    });

    /* "Add" button click */
    $("#custom-role-table").on("click", "[class*='custom-role-button-add']", function(event) {
        event.preventDefault();

        kaltura.role.resetEditRows(this);
        kaltura.role.toggleAddNewRow(this, false);

        setMainFrameHeight(window.name);

        return false;
    });

    $("#custom-role-table").on("focus", "[class*='custom-role-lti-select']", function(event) {
        var element = kaltura.role.getCurrentElement($(this).closest("tr"));
        element.newSakaiRole = $(element.sakaiSelect).val();
        kaltura.role.filterLtiRoles(element);
    });

    $("#custom-role-table").on("focus", "[class*='custom-role-sakai-select']", function(event) {
        var element = kaltura.role.getCurrentElement($(this).closest("tr"));
        kaltura.role.filterLtiRoles(element);
    });

    $("#custom-role-table").on("change", "[class*='custom-role-sakai-select']", function(event) {
        var element = kaltura.role.getCurrentElement($(this).closest("tr"));
        kaltura.role.toggleSaveButton(element);
        kaltura.role.filterLtiRoles(element);
    });

    $("#custom-role-table").on("change", "[class*='custom-role-lti-select']", function(event) {
        var element = kaltura.role.getCurrentElement($(this).closest("tr"));
        kaltura.role.toggleSaveButton(element);
        kaltura.role.filterLtiRoles(element);
    });

});

kaltura.role = kaltura.role || {

    /**
     * ajax services
     */

    /* GET request for existing Sakai roles defined */
    getExistingSakaiRoles: function () {
        var deferred = $.Deferred();

        kaltura.ajax.doGet(kaltura.ajax.roleUrl + "/sakai", function(success, data) {
            if (success) {
                allSakaiRoles = [];

                $.each(data, function(index, sakaiRole) {
                    allSakaiRoles.push(sakaiRole);
                });
            } else {
                kaltura.main.displayAlert("There was an error retrieving all Sakai roles.", true);
            }

            deferred.resolve();
        });

        return deferred.promise();
    },

    /* GET request for existing LTI roles */
    getAllLtiRoles: function () {
        var deferred = $.Deferred();

        kaltura.ajax.doGet(kaltura.ajax.roleUrl + "/lti", function(success, data) {
            if (success) {
                allLtiRoles = [];

                $.each(data, function(index, ltiRole) {
                    allLtiRoles.push(ltiRole);
                });
            } else {
                kaltura.main.displayAlert("There was an error retrieving all LTI roles.", true);
            }

            deferred.resolve();
        });

        return deferred.promise();
    },

    /* GET request for processing existing role data */
    processRoleMappingData: function () {
        var deferred = $.Deferred();

        kaltura.ajax.doGet(kaltura.ajax.roleUrl, function(success, data) {
            if (success) {
                // update the data table rows
                $.each(data, function(index, roleMapping) {
                    var newCustomRoleTableRow = customRoleTableRow.clone(true);

                    var element = kaltura.role.getCurrentElement(newCustomRoleTableRow);

                    $(element.sakaiText).text(roleMapping.sakaiRole);
                    $(element.ltiText).text(roleMapping.ltiRole);
                    element.setId(roleMapping.id);

                    kaltura.role.appendRow(element);

                    kaltura.role.updateSakaiLtiRoleMap(kaltura.role.buildRoleMapString(roleMapping.sakaiRole, roleMapping.ltiRole), null);
                });

                kaltura.role.appendNewAddRoleButtonRow();
            } else {
                kaltura.main.displayAlert("There was an error getting the role mappings.", true);
            }
            deferred.resolve();
        });

        return deferred.promise();
    },

    /**
     * Hide / show components
     */

    /* Show/hide a data row's components */
    toggleDataRowComponents: function (element, editable) {

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
                kaltura.role.appendNewAddRoleButtonRow();
            }
            
            $(element.buttonDelete).hide();
        }
    },

    /* Disables a role row upon deletion */
    disableDataRow: function (element) {
        $(element.ltiText).show();

        $(element.buttonEdit).remove();
        $(element.buttonSave).remove();
        $(element.buttonCancel).remove();
        $(element.buttonDelete).remove();

        $(element.row).addClass("danger");
        $(element.row).addClass("deleted");
    },

    /* Resets all of the rows that may have been set for editing */
    resetEditRows: function (currentRow) {
        var dataTable = $(currentRow).closest("#custom-role-table");
        $.each(dataTable.find(".custom-role-table-row"), function(index, dataRow) {
            var element = kaltura.role.getCurrentElement(dataRow);
            if (!element.isDeleted()) {
                if (element.isNewRole()) {
                    kaltura.role.toggleAddNewRow(dataRow, true);
                } else {
                    kaltura.role.toggleDataRowComponents(element, false);
                }
            }
        });
    },

    /* Adds a button to create new row  for adding a role */
    toggleAddNewRow: function (element, showButton) {
        if (showButton) {
            $(element).remove();
            kaltura.role.appendNewAddRoleButtonRow();
        } else {
            $(element).closest("tr").remove();
            kaltura.role.appendNewMappingRow();
        }
    },

    /* Disables the Save button if there are not the appropriate amount of selections */
    toggleSaveButton: function (element) {
        if ($(element.sakaiSelect).val() == "-1" || $(element.ltiSelect).val() == "-1") {
            $(element.buttonSave).prop("disabled", true);
        } else {
            $(element.buttonSave).prop("disabled", false);
        }
    },

    /**
     * Appending HTML
     */

    /* Add a data row to the table */
    appendRow: function (element) {
        $("#custom-role-table").append(
            "<tr class='custom-role-table-row'>" + $(element.row).html() + "</tr>"
        );
    },

    /* Append a new mapping data row to the table */
    appendNewMappingRow: function () {
        var newCustomRoleTableRow = customRoleTableRow.clone(true);
        var element = kaltura.role.getCurrentElement(newCustomRoleTableRow);

        kaltura.role.toggleDataRowComponents(element, true);

        $(element.sakaiSelect).show();

        $(element.sakaiText).hide();

        kaltura.role.populateDropdown(element.sakaiSelect, allSakaiRoles, null);
        kaltura.role.populateDropdown(element.ltiSelect, allLtiRoles, null);
        element.setStatus(null);
        kaltura.role.toggleSaveButton(element);
        kaltura.role.appendRow(element);
    },

    /* Append a new Add new role button */
    appendNewAddRoleButtonRow: function () {
        var newCustomRoleAddTableRow = customRoleAddRow.clone(true);
        $("#custom-role-table").append(
            "<tr class='custom-role-add-table-row'>" + $(newCustomRoleAddTableRow).html() + "</tr>"
        );
    },

    /**
     * Data manipulation services
     */

    /* Populates a select dropdown, where the value and text are the same */
    populateDropdown: function (selectElement, dataArray, defaultValue) {
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
    },

    /* Populates the available LTI roles based on the Sakai role */
    filterLtiRoles: function (element) {
        kaltura.role.populateDropdown(element.ltiSelect, allLtiRoles, $(element.ltiSelect).val());

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
    },

    /* Updates the mapping to keep track of the mapped roles */
    updateSakaiLtiRoleMap: function (add, remove) {
        if (add) {
            sakaiLtiRoleMap.push(add);
        }

        if (remove) {
            sakaiLtiRoleMap.splice($.inArray(remove, sakaiLtiRoleMap), 1);
        }
    },

    /* Builds the string to store in the sakaiLtiRoleMap array */
    buildRoleMapString: function (sakaiRole, ltiRole) {
        return sakaiRole + ":" + ltiRole;
    },

    /* Creates a model of the element to be created, deleted, or updated */
    getCurrentElement: function (dataRow) {
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
        currentElement.status = currentElement.row.find("[class*='custom-role-status']");
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
        currentElement.setStatus = function(isSuccess) {
            var icon = currentElement.status.find("[class*='glyphicon']");
            icon.removeClass("glyphicon-ok").removeClass("glyphicon-remove");
            currentElement.status.removeClass("alert-error").removeClass("alert-success");
            currentElement.status.hide();
            if (isSuccess != null) {
                if (isSuccess) {
                    currentElement.status.addClass("alert-success");
                    icon.addClass("glyphicon-ok");
                } else {
                    currentElement.status.addClass("alert-error");
                    icon.addClass("glyphicon-remove");
                }
                currentElement.status.show();
            }
        }

        return currentElement;
    }

}
