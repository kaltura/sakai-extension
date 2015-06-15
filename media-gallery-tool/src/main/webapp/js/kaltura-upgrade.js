(function() {

    var PI = {
    	BORDER_WIDTH: 20,
    	BORDER_HEIGHT: 20,
    	DOMAIN_NAME: "http://166762-2.qakmstest.dev.kaltura.com",

        onReady: function() {
            $(document).find(".portletMainIframe").load(function() {
                PI.insertLTIFrame(this);
                PI.upgradeStatic(this);
//                PI.upgradeEditor(this);
            });
        },
        
        // converts a <span with an embedded kaltura LTI image to an iframe for LTI rendering
        insertLTIFrame: function(doc) {
        	$(doc).contents().find(".kaltura-lti-media > img").each(function() {
                var height = $(this).height() + PI.BORDER_HEIGHT + 100;
                var width = $(this).width() + PI.BORDER_WIDTH + 100;
                var userId = portal.user.id;
                var siteId = portal.siteId;
                var mediaUrl = $(this).attr("kaltura-lti-url");
                var iframeSource = "/media-gallery-tool/mediadisplay.htm?mediaitemurl=" + encodeURIComponent(mediaUrl) + "&userid=" + userId + "&siteid=" + siteId;
     	        var iframe = $("<iframe height='" + height + "' width='" + width + "' src='" + iframeSource + "'>");

                $(this).parent().parent().append(iframe); 
        	});
            $(doc).contents().find(".kaltura-lti-media").remove();
        },
        
        // converts a <span with an embedded kaltura non-LTI image to an iframe for LTI rendering
        // TODO - errors retrieving media based on media id from kaltura servers
        upgradeStatic: function(doc) {
        	
            $(doc).contents().find("span.kaltura-media > img").each(function() {
                var height = $(this).height() + PI.BORDER_HEIGHT;
                var width = $(this).width() + PI.BORDER_WIDTH;
                var userId = portal.user.id;
                var siteId = portal.siteId;
                
                // TODO - use this to retrieve the entry ID,  currently hard coded
//                var mediaUrl = $(this).attr("src");
//                var entryId = // TODO - pull from mediaUrl
                var entryId = "0_5w1m85uf";
                // exact same code, just replacing the entryId - does not work, 500 error returned on server
                var embeddedSource = "/media-gallery-tool/mediadisplay.htm?mediaitemurl=https%3A%2F%2F166762-2.qakmstest.dev.kaltura.com%2Fbrowseandembed%2Findex%2Fmedia%2Fentryid%2F" + entryId + "%2FshowDescription%2Ffalse%2FshowTitle%2Ffalse%2FshowTags%2Ffalse%2FshowDuration%2Ffalse%2FshowOwner%2Ffalse%2FshowUploadDate%2Ffalse%2FplayerSize%2F400x285%2FplayerSkin%2F29907941%2F&userid=" + userId + "&siteid=" + siteId;
                // image copied from another location, works
//                var embeddedSource = "/media-gallery-tool/mediadisplay.htm?mediaitemurl=https%3A%2F%2F166762-2.qakmstest.dev.kaltura.com%2Fbrowseandembed%2Findex%2Fmedia%2Fentryid%2F0_23tq1lmf%2FshowDescription%2Ffalse%2FshowTitle%2Ffalse%2FshowTags%2Ffalse%2FshowDuration%2Ffalse%2FshowOwner%2Ffalse%2FshowUploadDate%2Ffalse%2FplayerSize%2F400x285%2FplayerSkin%2F29907941%2F&userid=" + userId + "&siteid=" + siteId;
                
                console.log("embeddedSource: [" + embeddedSource + "]");
                var iframe = $("<iframe height='" + height + "' width='" + width + "' src='" + embeddedSource + "'>");
                $(this).parent().parent().append(iframe); 
                
            });
            // TODO - uncomment the line below to remove the original image, currently displays the original
            // image "as-is", next to the image retrieved through LTI call
//            $(doc).contents().find("span.kaltura-media").remove();
                
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
                    $(value).val($(iframe).html());
                });
            });
        }
    }

    $(document).ready(PI.onReady);
})();
