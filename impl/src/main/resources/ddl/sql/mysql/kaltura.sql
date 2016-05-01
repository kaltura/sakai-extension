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
    expiration_date datetime not null,
    primary key (id)
) ENGINE=InnoDB;

create table KALTURA_SITE_COPY_BATCH (
    batch_id bigint not null auto_increment,
    source_site_id varchar(255) not null,
    target_site_id varchar(255) not null,
    status varchar(99) not null,
    attempts int(11),
    created_on datetime not null,
    primary key(batch_id)
) ENGINE=InnoDB;

create table KALTURA_SITE_COPY_JOB (
    job_id bigint not null auto_increment,
    batch_id bigint not null,
    kaltura_job_id bigint not null,
    status varchar(99) not null,
    attempts int(11),
    created_on datetime not null,
    primary key(job_id)
) ENGINE=InnoDB;
