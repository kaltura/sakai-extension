create table KALTURA_LTI_ROLE (
    id number(19,0) not null,
    sakai_role varchar2(255 char) not null,
    lti_role varchar2(255 char) not null,
    created_on timestamp not null,
    last_modified timestamp not null,
    primary key (id)
);

create table KALTURA_LTI_AUTH_CODE (
    id number(19,0) not null,
    user_id varchar2(255 char) not null,
    auth_code varchar2(255 char) not null,
    created_on timestamp not null,
    expiration_date timestamp not null,
    primary key (id)
);

create table KALTURA_SITE_COPY_BATCH (
    batch_id number(19,0) not null,
    source_site_id varchar(255) not null,
    target_site_id varchar(255) not null,
    status varchar(99) not null,
    attempts number(11),
    created_on timestamp not null,
    primary key (batch_id)
);

create table KALTURA_SITE_COPY_JOB (
    job_id number(19,0) not null,
    batch_id number(19,0) not null,
    kaltura_job_id number(19,0) not null,
    status varchar(99) not null,
    attempts number(11),
    created_on timestamp not null,
    primary key (job_id)
); 

create sequence KALTURA_LTI_ROLE_ID_SEQ;
create sequence KALTURA_LTI_AUTH_CODE_ID_SEQ;
create sequence KALTURA_SITE_COPY_BATCH_ID_SEQ;
create sequence KALTURA_SITe_COPY_JOB_ID_SEQ;
