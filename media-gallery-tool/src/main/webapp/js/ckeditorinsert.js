(function() {
    var ckDialog = window.parent.CKEDITOR.dialog.getCurrent();
    var editor = window.parent.CKEDITOR.currentInstance;

    var insert = function(ckDialog, editor) {
        var ckOk = ckDialog._.buttons['ok'];

        if( ckOk && ckOk !== "null" && ckOk !== "undefined" ) {
            var thumbnailUrl = (mediaItem.thumbnailUrl && mediaItem.thumbnailUrl != "") ? mediaItem.thumbnailUrl : "/media-gallery-tool/img/kaltura-logo.png";
            var mediaElement = "<span class=\"kaltura-lti-media\">";
            mediaElement += "<img " +
            "src=\"" + thumbnailUrl + "\" " +
            "title=\"IFrame\" " +
            "kaltura-lti-url=\"" + mediaItem.url + "\" " +
            "height=\"" + mediaItem.height + "\" " +
            "width=\"" + mediaItem.width + "\" />";
            mediaElement += "</span>";
            editor.insertHtml(mediaElement);
            
            // when complete, close the iframe dialog
            ckOk.click();
        } else {
            // could not find the Ok button in the dialog
            alert('ckOk is null');
        }
    };
    
    insert(ckDialog, editor);
    
})();