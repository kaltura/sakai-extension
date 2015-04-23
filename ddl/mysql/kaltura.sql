create table KALTURA_LTI_ROLE (
    id bigint not null auto_increment,
    sakai_role varchar(255) not null,
    lti_role varchar(255) not null,
    active bit not null default true,
    created_on datetime not null,
    last_modified datetime not null,
    primary key (id),
    unique (sakai_role)
) ENGINE=InnoDB;

create table KALTURA_LTI_AUTH_CODE (
    id bigint not null auto_increment,
    user_id varchar(255) not null,
    auth_code varchar(255) not null,
    inactivated bit not null default false,
    created_on datetime not null,
    exipiration_date datetime not null,
    primary key (id),
) ENGINE=InnoDB;