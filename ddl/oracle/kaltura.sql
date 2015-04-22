create table KALTURA_LTI_ROLE (
    id number(19,0) not null,
    sakai_role varchar2(255 char) unique not null,
    lti_role varchar2(255 char) not null,
    active bit number(1,0) default 0 not null,
    created_on timestamp not null,
    last_modified timestamp not null,
    primary key (id)
);

create sequence KALTURA_LTI_ROLE_ID_SEQ;