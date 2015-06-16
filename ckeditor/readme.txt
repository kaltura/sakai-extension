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

3. Re-deploy the reference project.

mvn clean install sakai:deploy -f reference/pom.xml

4. Configure Anti-Samy

Anti-Samy validates potentially dangerous script code and prevents it from being stored in the 
datastore according to the security policy of Sakai.  This is a Sakai 10 new feature.  There are
at least two ways to address this.

a) if you are using default policies:
* set the default security policy to low enforcement.  Enable low enforcement by 
setting the following property in sakai.properties:

content.cleaner.default.low.security=true

* copy the "antisamy" directory into your web container's sakai directory (same directory where
sakai.properties lives.
Example
cp -R {KALTURA_LIT_ROOT/ckeditor/antisamy {CATALINA_HOME}/sakai

The override low-security-policy.xml adds a new attribute, kaltura-lti-url, to the img tag.  This 
allows the CKEditor to save the new media item inserted by the Kaltura LTI integration.

b) if you are using custom policies:
* open the file antisamy/low-security-policy.xml and search for "kaltura-lti-url".  You will
find two references.  One is an attribute definition, and the second add the attribute to the img tag.
You need to copy both configurations into your custom policy file (either low-security-policy.xml
 or high-security-policy.xml).
 
You can also apply antisamy.patch (in this directory) to your custom policy file to make these changes for you
 
Once you have made these configuration changes to Sakai, you will need to stop and restart Sakai.
