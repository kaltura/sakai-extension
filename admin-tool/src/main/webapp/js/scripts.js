var kaltura = kaltura || {};

// don't cache ajax calls
$.ajaxSetup({
    cache: false
});

kaltura = {

    /*
     * URLs
     */
    baseUrl: "/direct/kaltura/",
    userUrl: "/direct/kaltura/user",
    roleUrl: "/direct/kaltura/role",

    /* POST ajax request */
    doPost: function (data, url, callback) {
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
    },

    /* POST ajax request */
    doPut: function (data, url, callback) {
        var request = $.ajax({
            type: "PUT",
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
    },

    /* GET ajax request */
    doGet: function (url, callback) {
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
    },

    /* GET ajax request */
    doDelete: function (url, callback) {
        var request = $.ajax({
            type: "POST",
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

}
