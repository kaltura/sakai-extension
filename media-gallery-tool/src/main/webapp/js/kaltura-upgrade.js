// kaltura-upgrade.js expects jQuery to already be loaded, in the case that its not will attempt load jQuery
(function() {
    if (typeof jQuery == 'undefined') {
        function getScript(url, success) {
            var script = document.createElement('script');
            script.src = url;

            var head = document.getElementsByTagName('head')[0],
                done = false;

            // Attach handlers for all browsers
            script.onload = script.onreadystatechange = function () {

                if (!done && (!this.readyState || this.readyState == 'loaded' || this.readyState == 'complete')) {
                    done = true;
                    // callback function provided as param
                    success();
                    script.onload = script.onreadystatechange = null;
                };
            };
            head.appendChild(script);
            head.appendChild(document.createComment("jQuery loaded by kaltura-upgrade.js"));
        };

        getScript('//code.jquery.com/jquery-1.11.3.min.js', function () {
            if (typeof jQuery == 'undefined') {
                // failsafe - still somehow failed...
                console.log("Kaltura defers processing this page...")
            } else {
                // jQuery loaded!
                processKalturaLtiMedia();
            }
        });
    } else { // jQuery was already loaded
        processKalturaLtiMedia();
    };
})();

function processKalturaLtiMedia() {

    var PI = {
        /**
         * Default sizes for media iframe - 400x285 pixels
         */
        BORDER_WIDTH: 400,
        BORDER_HEIGHT: 285,
        MEDIA_DISPLAY_URL: '/media-gallery-tool/mediadisplay.htm',
        MEDIA_DISPLAY_STATIC_URL: '/media-gallery-tool/mediadisplaystatic.htm',

        onReady: function() {
            if (typeof portal == 'undefined') {
                var path = window.location.pathname;
                console.log("Sakai context is missing, searching path: " + path);
                var segments = path.split('/');
                if (segments[1] == 'access' && segments[2] == 'content') {
                    // viewing sakai resources
                    if (segments[3] == 'group') {
                        // viewing site
                        PI.processDocument(undefined, segments[4]);
                    } else if (segments[3] == 'user') {
                        // viewing user
                        PI.processDocument(undefined, '~' + segments[4]);
                    } else {
                        console.log("Unknown path = " + segments[4] + ", cannot process page");
                    }
                } else {
                    console.log("Unknown path, cannot process page");
                }
            } else {
                // in a sakai context
                console.log("Sakai context found");
                PI.processDocument(portal.user.id, portal.siteId);
            }
        },

        processDocument: function(userId, siteId) {
            // process tools in iframe, non-inlined
            $(document).find(".portletMainIframe").load(function() {
                PI.insertLTIFrame(this, userId, siteId);
                PI.upgradeStatic(this, userId, siteId);
            });
            // process current document, inlined
            PI.insertLTIFrame(document, userId, siteId);
            PI.upgradeStatic(document, userId, siteId);
        },

        createIFrame: function(media, source) {
            var heightBefore = $(media).attr('height');
            var widthBefore = $(media).attr('width');
            var heightAfter = heightBefore ? heightBefore : PI.BORDER_HEIGHT;
            var widthAfter = widthBefore ? widthBefore : PI.BORDER_WIDTH;
            console.log("createIFrame:: height: " + heightAfter + ", width: " + widthAfter);

            var src = source(media);
            console.log("createIFrame:: src: " + src);
            var iframe = $("<iframe height='" + heightAfter + "' width='" + widthAfter + "' src='" + src + "' allowfullscreen webkitallowfullscreen mozAllowFullScreen />");
            iframe.css("border", "none");
            return iframe;
        },

        // converts a <span with an embedded kaltura LTI image to an iframe for LTI rendering
        insertLTIFrame: function(doc, userId, siteId) {
            try {
                $(doc).contents().find(".kaltura-lti-media > img").each(function () {

                    var iframe = PI.createIFrame($(this), function(media) {
                        var mediaUrl = $(media).attr("kaltura-lti-url");
                        return PI.MEDIA_DISPLAY_URL + "?mediaitemurl=" + encodeURIComponent(mediaUrl) + "&siteid=" + siteId + "&userid=" + userId;
                    });

                    $(this).parent().parent().append(iframe);
                });
                $(doc).contents().find(".kaltura-lti-media").remove();
            } catch(exception) {
                // a SecurityException will be thrown if processing an iframe violating the same-origin policy
                console.log("insertLTIFrame exception:: " + exception)
            }

            $(doc).attr("allowfullscreen", "");
            $(doc).attr("webkitallowfullscreen", "");
            $(doc).attr("mozAllowFullScreen", "");
        },

        // converts a <span with an embedded kaltura non-LTI image to an iframe for LTI rendering
        upgradeStatic: function(doc, userId, siteId) {
            try {
                $(doc).contents().find("span.kaltura-media > img").each(function () {
                    //set the iframe size to default values
                    var iframe = PI.createIFrame($(this), function(media) {
                        var mediaUrl = $(media).attr("src");
                        var entryIds = mediaUrl.match(/.*\/entry_id\/([0-9]_[0-9A-Za-z]{8})\/.*/);
                        var entryId = "";
                        if (entryIds.length > 1) {
                            entryId = entryIds[1];
                        }
                        return PI.MEDIA_DISPLAY_STATIC_URL + "?entryid=" + entryId + "&siteid=" + siteId + "&userid=" + userId;
                    });

                    $(this).parent().parent().append(iframe);
                });

                $(doc).contents().find("span.kaltura-media").remove();
            } catch(exception) {
                // a SecurityException will be thrown if processing an iframe violating the same-origin policy
                console.log("upgradeStatic exception:: " + exception)
            }

        },

        // converts a <span with an embedded kaltura non-LTI image to the same lti div image format for rendering within CKEditor
        // TODO - errors retrieving media based on media id from kaltura servers
        upgradeEditor: function(doc) {
            try {
                $(doc).contents().find("textarea#body").each(function (index, value) {
                    var innerHtml = $(value).val();

                    // TODO - as-is this code replaces the entire contents of the textarea with only the iframe, need to respect the other content.
                    $(innerHtml).find("span.kaltura-media > img").each(function () {
                        $(this).attr('height', 100);
                        $(this).attr('width', 100);

                        var iframe = PI.createIFrame($(this), function(media) {
                           return  $(media).attr("src");
                        });

                        $(value).val($(iframe).html());
                    });
                });
            } catch(exception) {
                // a SecurityException will be thrown if processing an iframe violating the same-origin policy
                console.log("upgradeEditor exception:: " + exception)
            }
        }
    }

    $(document).ready(PI.onReady);
};

window.addEventListener('message', function(e) {
    try {
        var message = JSON.parse(e.data);
        if ( message.subject == 'kaltura.frameResize' ) {
            var id = message.windowid;
            var height = message.height;
            document.getElementById(id).height = height;
            console.log('Received kaltura.frameResize for ' + id + ' with height=' + height);
        }
    } catch (error) {
        console.log('kaltura.frameResize for ' + id + ' failed with height=' + height);
        console.log(e.data);
    }
});