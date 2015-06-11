(function() {
    var ckDialog = window.parent.CKEDITOR.dialog.getCurrent();
    var editor = window.parent.CKEDITOR.currentInstance;

    var insert = function(ckDialog, editor) {
        var ckOk = ckDialog._.buttons['ok'];

        if( ckOk && ckOk !== "null" && ckOk !== "undefined" ) {
            var iframeStr = "<iframe " +
            "src=\"/media-gallery-tool/mediadisplay.htm?mediaitemurl=" + encodeURIComponent(mediaItem.url) + "&userid=" + parent.parent.portal.user.id + "&siteid=" + parent.parent.portal.siteId + "\" " +
            "height=\"" + mediaItem.height + "\" " +
            "width=\"" + mediaItem.width + "\" >";
            iframeStr += "</iframe>";
            
            editor.insertHtml(iframeStr);
            
            // when complete, close the iframe dialog
            ckOk.click();
        } else {
            // could not find the Ok button in the dialog
            alert('ckOk is null');
        }
    };
    
    insert(ckDialog, editor);
    
})();
