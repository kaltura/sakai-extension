
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