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
 mvn clean install sakai:deploy -f portal/pom.xml
 
4. Configure Anti-Samy

Anti-Samy validates potentially dangerous scripts and prevents them from being stored in the 
datastore, according to the security policy of Sakai.  This is a Sakai 10 new feature.  You need to
modify your antisamy policy files to allow CKEditor to use additional attributes.

To confirm if you have custom antisamy policy files, look for a file in {tomcat_home}/sakai/antisamy.
If the antisamy folder doesn't exist, or if the folder exists but is empty, you are using default
antisamy policies.

If you are using default policies, you need to copy the default security policy into your 
Tomcat directory structure.  The default Sakai antisamy policy file is "high-security-policy.xml".  
Copy this file from Sakai into your {tomcat_home}/sakai/antisamy folder (you may need to create 
the antisamy folder first:

cd {tomcat_home}/sakai
mkdir antisamy
cd antisamy
cp {sakai_home}/kernel/kernel-impl/src/main/resources/antisamy/high-security-profile.xml .

Once you have an antisamy policy in {tomcat_home}/sakai/antisamy, check its name.  This file is 
named either high-security-profile.xml for high security or low-security-profile.xml for 
less restrictive security.  Refer to Sakai antisamy documentation for more details about the differences.

Once you have a security profile in your {tomcat_home}/sakai/antisamy folder, modify the 
security profile to add two pieces of information 
(either high-security-profile.xml or low-security-profile.xml}:

Add this block into the <common-attributes> block in either file:

<!-- custom attribute to support Kaltura LTI -->
<attribute name="kaltura-lti-url" description="used by the IMG tag inside of CKEditor to support Kaltura LTI media embedding">
    <regexp-list>
        <regexp name="anything" />
    </regexp-list>
</attribute>

Add this block into the <tag name="img" action="validate"> in either file:
<attribute name="kaltura-lti-url"/>

If you prefer to apply a patch rather than manually edit the policy files, 
Sakai-extension includes antisamy patches for both security policy files.
The patches are named antisamy-low.patch and antisamy-high.patch.  These files are located
in {sakai-extension-home}/ckeditor

Once you have made these configuration changes to Sakai, you will need to stop and restart Sakai.
