(function() {

    var PI = {
        BORDER_WIDTH: 0,
        BORDER_HEIGHT: 0,
        DOMAIN_NAME: "http://166762-2.qakmstest.dev.kaltura.com",

        onReady: function() {
            $(document).find(".portletMainIframe").load(function() {
                PI.insertLTIFrame(this);
                PI.upgradeStatic(this);
                // PI.upgradeEditor(this);
            });
        },

        // converts a <span with an embedded kaltura LTI image to an iframe for LTI rendering
        insertLTIFrame: function(doc) {
            $(doc).contents().find(".kaltura-lti-media > img").each(function() {
                var height = $(this).height() + PI.BORDER_HEIGHT;
                var width = $(this).width() + PI.BORDER_WIDTH;
                var userId = portal.user.id;
                var siteId = portal.siteId;
                var mediaUrl = $(this).attr("kaltura-lti-url");
                var iframeSource = "/media-gallery-tool/mediadisplay.htm?mediaitemurl=" + encodeURIComponent(mediaUrl) + "&userid=" + userId + "&siteid=" + siteId;
                var iframe = $("<iframe height='" + height + "' width='" + width + "' src='" + iframeSource + "' allowfullscreen webkitallowfullscreen mozAllowFullScreen />");

                iframe.css("border", "none");

                $(this).parent().parent().append(iframe); 
            });
            $(doc).contents().find(".kaltura-lti-media").remove();

            $(doc).attr("allowfullscreen", "");
            $(doc).attr("webkitallowfullscreen", "");
            $(doc).attr("mozAllowFullScreen", "");
        },

        // converts a <span with an embedded kaltura non-LTI image to an iframe for LTI rendering
        upgradeStatic: function(doc) {

            $(doc).contents().find("span.kaltura-media > img").each(function() {
                //set the iframe size to default to 400x285
                var height = 285 + PI.BORDER_HEIGHT; 
                var width = 400 + PI.BORDER_WIDTH;
                var userId = portal.user.id;
                var siteId = portal.siteId;

                var mediaUrl = $(this).attr("src");
                console.log("mediaUrl: [" + mediaUrl + "]");

                var entryIds = mediaUrl.match(/.*\/entry_id\/([0-9]_[0-9A-Za-z]{8})\/.*/);
                var entryId = "";
                if (entryIds.length > 1) {
                    entryId = entryIds[1];
                }
                console.log("entryId: [" + entryId + "]");
                var embeddedSource = "/media-gallery-tool/mediadisplaystatic.htm?entryid=" + entryId + "&userid=" + userId + "&siteid=" + siteId;;

                console.log("embeddedSource: [" + embeddedSource + "]");
                var iframe = $("<iframe height='" + height + "' width='" + width + "' src='" + embeddedSource + "'>");

                iframe.css("border", "none");

                $(this).parent().parent().append(iframe); 
            });
            // TODO - uncomment the line below to remove the original image, currently displays the original
            // image "as-is", next to the image retrieved through LTI call
            $(doc).contents().find("span.kaltura-media").remove();
        },

        // converts a <span with an embedded kaltura non-LTI image to the same lti div image format for rendering within CKEditor
        // TODO - errors retrieving media based on media id from kaltura servers
        upgradeEditor: function(doc) {
            $(doc).contents().find("textarea#body").each(function(index, value) {
                var innerHtml = $(value).val();

                // TODO - as-is this code replaces the entire contents of the textarea with only the iframe, need to
                // respect the other content.
                $(innerHtml).find("span.kaltura-media > img").each(function() {
                    var source = $(this).attr("src");
                    var height = 100;
                    var width = 100;
                    var iframe = $("<p><iframe height='" + height + "' width='" + width + "' src='" + source + "' /></p>");

                    iframe.css("border", "none");

                    $(value).val($(iframe).html());
                });
            });
        }
    }

    $(document).ready(PI.onReady);
})();
