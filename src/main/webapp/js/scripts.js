$(document).ready(function(){
    // don't cache ajax calls
    $.ajaxSetup({
        cache: false
    });

    /*
     * URLs
     */
    var baseUrl = "/direct/kaltura/";
    var userUrl = baseUrl + "user";
    var roleUrl = baseUrl + "role";

    /*
     * On load listeners
     */

    /* GET request for user data */
    doGet(userUrl, function(success, data) {
        $("#user-data-json").append(JSON.stringify(data));
        $("#user-data-name").append(data.displayName);
        $("#user-data-id").append(data.id);
        $("#user-data-eid").append(data.eid);
        $("#user-data-email").append(data.email);
        $("#user-data-type").append(data.type);

        $.each(data.userSiteRoles, function(index, userSiteRole) {
            $("#user-role-table").append(
                "<tr>" +
                    "<td>" + userSiteRole.siteId + "</td>" +
                    "<td>" + userSiteRole.siteRole + "</td>" +
                    "<td>" + userSiteRole.ltiRole + "</td>" + 
                "</tr>"
            );
        });
    });

    /* GET request for role data */
    doGet(roleUrl, function(success, data) {
        $("#role-data-json").append(JSON.stringify(data));

        $.each(data, function(index, roleMapping) {
            $("#role-table").append(
                "<tr>" +
                    "<td>" + roleMapping.sakaiRoleId + "</td>" +
                    "<td>" + roleMapping.ltiRoleId + "</td>" + 
                "</tr>"
            );
        });
    });

    /* POST ajax request */
    function doPost(data, url, callback) {
        var request = $.ajax({
            type: "POST",
            url:  url,
            data: {"data": JSON.stringify(data)},
            cache: false,
            async: false
        });

        request.success(function(data, status, jqXHR) {
            callback(true, data);
        });

        request.fail(function(jqXHR, textStatus, errorThrown) {
            callback(false, errorThrown);
        });
    }

    /* GET ajax request */
    function doGet(url, callback) {
        var request = $.ajax({
            type: "GET",
            url:  url,
            cache: false,
            async: false
        });

        request.success(function(data, status, jqXHR) {
            callback(true, data);
        });

        request.fail(function(jqXHR, textStatus, errorThrown) {
            callback(false, errorThrown);
        });
    }

});