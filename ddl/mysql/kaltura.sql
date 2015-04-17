create table KALTURA_LTI_ROLE (
    id bigint not null auto_increment,
    sakai_role varchar(255) not null,
    lti_role varchar(255) not null,
    active bit not null default true,
    created_on datetime not null,
    last_modified datetime not null,
    primary key (id)
) ENGINE=InnoDB;