(function() {
    var ckDialog = window.parent.CKEDITOR.dialog.getCurrent();
    var editor = window.parent.CKEDITOR.instances.body;

    var insert = function(ckDialog, editor) {
        var ckOk = ckDialog._.buttons['ok'];

        if( ckOk && ckOk !== "null" && ckOk !== "undefined" ) {
            // TODO - read the fields out of the iframe dialog, build the media
            // viewer, and insert into the editor
            editor.insertHtml($("#title").text());
            
            // when complete, close the iframe dialog
	    ckOk.click();
        } else {
            // could not find the Ok button in the dialog
	    alert('ckOk is null');
        }
    }
    
    insert(ckDialog, editor);
    
})();
