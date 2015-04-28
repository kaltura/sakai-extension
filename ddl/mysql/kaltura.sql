create table KALTURA_LTI_ROLE (
    id bigint not null auto_increment,
    sakai_role varchar(255) not null,
    lti_role varchar(255) not null,
    created_on datetime not null,
    last_modified datetime not null,
    primary key (id)
) ENGINE=InnoDB;

create table KALTURA_LTI_AUTH_CODE (
    id bigint not null auto_increment,
    user_id varchar(255) not null,
    auth_code varchar(255) not null,
    created_on datetime not null,
    exipiration_date datetime not null,
    primary key (id)
) ENGINE=InnoDB;