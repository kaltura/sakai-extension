create table KALTURA_LTI_ROLE (
    id number(19,0) not null,
    sakai_role varchar2(255 char) unique not null,
    lti_role varchar2(255 char) not null,
    active bit number(1,0) default 1 not null,
    created_on timestamp not null,
    last_modified timestamp not null,
    primary key (id)
);

create table KALTURA_LTI_AUTH_CODE (
    id number(19,0) not null,
    user_id varchar2(255 char) not null,
    auth_code varchar2(255 char) not null,
    inactivated bit number(1,0) default 0 not null,
    created_on timestamp not null,
    exipiration_date timestamp not null,
    primary key (id),
)

create sequence KALTURA_LTI_ROLE_ID_SEQ;
create sequence KALTURA_LTI_AUTH_CODE_ID_SEQ;