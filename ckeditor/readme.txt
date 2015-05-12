These steps describe how to modify Sakai so the CK editor will work with the Kaltura-LTI Media Gallery.

1. Copy the "kaltura" directory into 
{SAKAI_ROOT}/reference/library/src/webapp/editor/ckextraplugins
Example:
cp -R {KALTURA_LTI_ROOT}/ckeditor/kaltura {SAKAI_ROOT}/reference/library/src/webapp/editor/ckextraplugins

2. Edit {SAKAI_ROOT}/reference/library/src/webapp/editor/ckeditor.launch.js
at about line 89, inside the definition of "toolbar_Full:", add 'kaltura' before the two references to 'Image' 
(these are the buttons in the toolbar):

    is:
        (sakai.editor.enableResourceSearch
            ? ['ResourceSearch','Image','Flash','Table','HorizontalRule','Smiley','SpecialChar','PageBreak']
            : ['Image','Flash','Table','HorizontalRule','Smiley','SpecialChar','PageBreak']),

    changes to:
        (sakai.editor.enableResourceSearch
            ? ['ResourceSearch','kaltura','Image','Flash','Table','HorizontalRule','Smiley','SpecialChar','PageBreak']
            : ['kaltura','Image','Flash','Table','HorizontalRule','Smiley','SpecialChar','PageBreak']),

at about line 171, add a new reference to the plugin:

    is:
		(function() { 
		   CKEDITOR.plugins.addExternal('movieplayer',basePath+'movieplayer/', 'plugin.js'); 
		   CKEDITOR.plugins.addExternal('wordcount',basePath+'wordcount/', 'plugin.js'); 
		   CKEDITOR.plugins.addExternal('fmath_formula',basePath+'fmath_formula/', 'plugin.js'); 
		   CKEDITOR.plugins.addExternal('audiorecorder',basePath+'audiorecorder/', 'plugin.js'); 
    
    changes to:
		(function() { 
		   CKEDITOR.plugins.addExternal('movieplayer',basePath+'movieplayer/', 'plugin.js'); 
		   CKEDITOR.plugins.addExternal('wordcount',basePath+'wordcount/', 'plugin.js'); 
		   CKEDITOR.plugins.addExternal('fmath_formula',basePath+'fmath_formula/', 'plugin.js'); 
		   CKEDITOR.plugins.addExternal('audiorecorder',basePath+'audiorecorder/', 'plugin.js'); 
		   CKEDITOR.plugins.addExternal('kaltura',basePath+'kaltura/', 'plugin.js'); 

at about line 190, add the following additional line:

    is:
        ckconfig.extraPlugins+="movieplayer,wordcount,fmath_formula";

    changes to:
        ckconfig.extraPlugins+="movieplayer,wordcount,fmath_formula";
        ckconfig.extraPlugins+=",kaltura";

3. Add the following script to {SAKAI_ROOT}/portal/portal-render-engine-impl/pack/src/webapp/vm/defaultskin/includeStandardHead.vm (around line 50):

<script type="text/javascript" language="JavaScript" src="/kaltura/javascript/kaltura-display.js"></script>

4. Add the following script to {SAKAI_ROOT}/portal/portal-render-engine-impl/pack/src/webapp/vm/neoskin/includeStandardHead.vm (around line 49):

<script type="text/javascript" language="JavaScript" src="/kaltura/javascript/kaltura-display.js"></script>

5. Re-deploy the reference and portal projects.

mvn clean install sakai:deploy -f reference/pom.xml
mvn clean install sakai:deploy -f portal/pom.xml

