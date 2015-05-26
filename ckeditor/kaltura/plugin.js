/*
 * Copyright 2010 Unicon (R) Licensed under the
 * Educational Community License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may
 * obtain a copy of the License at
 *
 * http://www.osedu.org/licenses/ECL-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS"
 * BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */

//var CKEDITOR = CKEDITOR || {};
var ckeditorId;
(function() {
<<<<<<< HEAD
  var pluginName = 'kaltura';
  CKEDITOR.plugins.add(pluginName,
      {
          init: function(editor) {

              editor.addCommand(pluginName, {
                  exec : function(editor) {
                      ckeditorId = editor.name;

                      var height = 480;
                      var width = 750;
                      CKEDITOR.dialog.addIframe('kaltura',
                          'Kaltura',
                          '/kaltura-admin-tool/ckeditor.htm?userid=' + parent.portal.user.id + '&siteid=' + parent.portal.siteId,
                          width,
                          height,
                          function() {
                              // iframe callback
                          },

                          {
                              onOk : function() {}
                          }
                      );

                      editor.addCommand( 'kaltura', new CKEDITOR.dialogCommand( 'kaltura' ) );
                  }
            });

            editor.ui.addButton(pluginName, {
                label: pluginName,
                command: pluginName,
                icon: this.path + 'images/kaltura.gif'
            });
        }
    );
})();
