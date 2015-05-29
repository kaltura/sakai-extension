(function() {
    var ckDialog = window.parent.CKEDITOR.dialog.getCurrent();
    var editor = window.parent.CKEDITOR.currentInstance;

    var insert = function(ckDialog, editor) {
        var ckOk = ckDialog._.buttons['ok'];

        if( ckOk && ckOk !== "null" && ckOk !== "undefined" ) {
            
            var iframeStr = "<iframe " +
            "src=\"" + $("#url").text() + "\" " +
            "height=\"" + $("#height").text() + "\" " +
            "width=\"" + $("#width").text() + "\" >";
            iframeStr += "</iframe>";
            editor.insertHtml(iframeStr);
            
            // when complete, close the iframe dialog
            ckOk.click();
        } else {
            // could not find the Ok button in the dialog
            alert('ckOk is null');
        }
    }
    
    insert(ckDialog, editor);
    
})();
