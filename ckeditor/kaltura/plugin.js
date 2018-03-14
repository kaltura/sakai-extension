/*
 * Copyright ©2016 Kaltura, Inc.
 */

//var CKEDITOR = CKEDITOR || {};
var ckeditorId;
(function() {
    var pluginName = 'kaltura';
    CKEDITOR.plugins.add(pluginName,
        {
            init: function(editor) {

                ckeditorId = editor.name;
                var height = 480;
                var width = 770;
                var siteId = "";
                if (typeof(parent.portal) != "undefined") {
                    siteId = parent.portal.siteId;
                }
                else if (typeof(parent.parent.portal) != "undefined") {
                    siteId = parent.parent.portal.siteId;
                }

                CKEDITOR.dialog.addIframe(pluginName,
                    'Kaltura',
                    '/media-gallery-tool/ckeditor.htm?siteid=' + siteId,
                    width,
                    height,
                    function() {
                        var ckDialog = CKEDITOR.dialog.getCurrent();
                        if (ckDialog) {
                            document.getElementById(ckDialog.getButton('ok').domId).style.display='none';
                            document.getElementById(ckDialog.getButton('cancel').domId).style.display='none';
                        }
                    },
                    {
                        onOk : function() {}
                    }
                );
                editor.addCommand(pluginName, new CKEDITOR.dialogCommand( 'kaltura' ) );

                editor.ui.addButton(pluginName, {
                    label: pluginName,
                    command: pluginName,
                    icon: this.path + 'images/kaltura.png'
                });
            }
        }
    );

})();
