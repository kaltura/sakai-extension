# Kaltura CKEditor Plugin

These steps describe how to add the kaltura ckeditor plugin to a Sakai installation.

1. Add the kaltura ckeditor plugin
  * copy the "kaltura" directory to the sakai installation at $CATALINA_BASE/webapps/library/editor/ckextraplugins/  
    Example: `cp -R ckeditor/kaltura $CATALINA_BASE/webapps/library/editor/ckextraplugins/`
1. Next we will edit the sakai ckeditor.launch.js to enable the kaltura plugin   
  - Open the file $CATALINA_BASE/webapps/library/editor/ckeditor.launch.js  
  - Locate the property ckconfig.toolbar_Full  
  - Add the kaltura button to the ckeditor bar (most installations will add it before the Image and Movie buttons)  
    for example locate the following line:  
    `['ContentItem', 'AudioRecorder','ResourceSearch','Image','Movie','Table','HorizontalRule','Smiley','SpecialChar']`  
    and change it to:  
    `['ContentItem', 'AudioRecorder','ResourceSearch','kaltura','Image','Movie','Table','HorizontalRule','Smiley','SpecialChar']`  
  - Add the kaltura plugin to the ckeditor config  
    locate the line:
    `ckconfig.extraPlugins+="sakaipreview,image2,audiorecorder,contentitem,movieplayer,wordcount,notification,autosave";`  
    then change it by adding the following line before and after so that in the end it will look like:  
    ```
    CKEDITOR.plugins.addExternal('kaltura',basePath+'kaltura/', 'plugin.js');
    ckconfig.extraPlugins+="sakaipreview,image2,audiorecorder,contentitem,movieplayer,wordcount,notification,autosave";  
    ckconfig.extraPlugins+=",kaltura";
    ```
  - One thing to note is that if you wish to enable the kaltura plugin for small screen devices you will need to also
    add the kaltura button the 'Basic' tool bar.
1. Configure Anti-Samy  
    **This step is not required in Sakai 12 as these policy changes have already been added to Sakai.**  
    Anti-Samy validates potentially dangerous scripts and prevents them from being stored in the 
    datastore, according to the security policy of Sakai.  This is a Sakai 10 new feature.  You need to
    modify your antisamy policy files to allow CKEditor to use additional attributes.  
    To confirm if you have custom antisamy policy files, look for a file in {tomcat_home}/sakai/antisamy.
    If the antisamy folder doesn't exist, or if the folder exists but is empty, you are using default
    antisamy policies.  
    If you are using default policies, you need to copy the default security policy into your 
    Tomcat directory structure.  The default Sakai antisamy policy file is "high-security-policy.xml".  
    Copy this file from Sakai into your $CATALINA_BASE/sakai/antisamy folder (you may need to create 
    the antisamy folder first:
    ```
    cd {tomcat_home}/sakai
    mkdir antisamy
    cd antisamy
    cp {sakai_home}/kernel/kernel-impl/src/main/resources/antisamy/high-security-profile.xml .
    ```
    Once you have an antisamy policy in {tomcat_home}/sakai/antisamy, check its name.  This file is
    named either high-security-profile.xml for high security or low-security-profile.xml for 
    less restrictive security.  Refer to Sakai antisamy documentation for more details about the differences.
    
    Once you have a security profile in your {tomcat_home}/sakai/antisamy folder, modify the 
    security profile to add two pieces of information 
    (either high-security-profile.xml or low-security-profile.xml}:
    
    Add this block into the <common-attributes> block in either file:
    ```
    <!-- custom attribute to support Kaltura LTI -->
    <attribute name="kaltura-lti-url" description="used by the IMG tag inside of CKEditor to support Kaltura LTI media embedding">
        <regexp-list>
            <regexp name="anything" />
        </regexp-list>
    </attribute>
    ```
    Add this block into the <tag name="img" action="validate"> in either file:  
    `<attribute name="kaltura-lti-url"/>`
    
    If you prefer to apply a patch rather than manually edit the policy files, 
    Sakai-extension includes antisamy patches for both security policy files.
    The patches are named antisamy-low.patch and antisamy-high.patch.  These files are located
    in {sakai-extension-home}/ckeditor
    
    Once you have made these configuration changes to Sakai, you will need to stop and restart Sakai.
