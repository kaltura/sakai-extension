create table KALTURA_SITE_COPY_BATCH_DETAILS (
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
    primary_key(job_id)
) ENGINE=InnoDB;
