-- update tool id to kaltura.media
UPDATE SAKAI_SITE_TOOL SET REGISTRATION = 'kaltura.media' WHERE REGISTRATION = 'sakai.kaltura';
-- update the tool name from 'Kaltura My Media' to 'My Media'
UPDATE SAKAI_SITE_TOOL SET TITLE = 'My Media' WHERE REGISTRATION = 'kaltura.my.media';

-- remove the following tables if they exist
DROP TABLE KALTURA_SITE_COPY_BATCH;
DROP TABLE KALTURA_SITE_COPY_JOB;
DROP TABLE KALTURA_SITE_COPY_BATCH_DETAILS;