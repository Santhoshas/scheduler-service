CREATE TABLE version
(
    prev_version    varchar(11) DEFAULT '',
    current_version varchar(11) not null
) comment 'Database version - used to upgrade DB';

CREATE TABLE QRTZ_CALENDARS
(
    sched_name    varchar(120) not null,
    calendar_name varchar(200) not null,
    calendar      blob         not null,
    PRIMARY KEY (sched_name, calendar_name)
);

CREATE TABLE QRTZ_JOB_DETAILS
(
    sched_name        varchar(120) not null,
    job_name          varchar(200) not null,
    job_group         varchar(200) not null,
    description       varchar(250) DEFAULT null,
    job_class_name    varchar(250) not null,
    is_durable        boolean      not null,
    is_nonconcurrent  boolean      not null,
    is_update_data    boolean      not null,
    requests_recovery boolean      not null,
    job_data          blob         DEFAULT null,
    PRIMARY KEY (sched_name, job_name, job_group)
);

CREATE TABLE QRTZ_TRIGGERS
(
    sched_name     varchar(120) not null,
    trigger_name   varchar(200) not null,
    trigger_group  varchar(200) not null,
    job_name       varchar(200) not null,
    job_group      varchar(200) not null,
    description    varchar(250) DEFAULT null,
    next_FIRE_time long         DEFAULT null,
    prev_FIRE_time long         DEFAULT null,
    priority       integer      DEFAULT null,
    trigger_state  varchar(16)  not null,
    trigger_TYPE   varchar(8)   not null,
    start_time     long         not null,
    end_time       long         DEFAULT null,
    calendar_name  varchar(200) DEFAULT null,
    misfire_instr  SMALLINT     DEFAULT null,
    job_data       blob         DEFAULT null,
    PRIMARY KEY (sched_name, trigger_name, trigger_group),
    CONSTRAINT FK_qrtz_triggerS_qrtz_job_details FOREIGN KEY (sched_name, job_name, job_group) REFERENCES QRTZ_JOB_DETAILS (sched_name, job_name, job_group)
);

CREATE TABLE QRTZ_CRON_TRIGGERS
(
    sched_name      varchar(120) not null,
    trigger_name    varchar(200) not null,
    trigger_group   varchar(200) not null,
    cron_expression varchar(120) not null,
    time_zone_id    varchar(80),
    PRIMARY KEY (sched_name, trigger_name, trigger_group),
    CONSTRAINT FK_qrtz_cron_triggerS_qrtz_triggerS FOREIGN KEY (sched_name, trigger_name, trigger_group) REFERENCES QRTZ_TRIGGERS (sched_name, trigger_name, trigger_group) ON DELETE CASCADE
);

CREATE TABLE QRTZ_FIRED_TRIGGERS
(
    sched_name        varchar(120) not null,
    entry_id          varchar(95)  not null,
    trigger_name      varchar(200) not null,
    trigger_group     varchar(200) not null,
    instance_name     varchar(200) not null,
    fired_time        long         not null,
    sched_time        long         not null,
    priority          integer      not null,
    state             varchar(16)  not null,
    job_name          varchar(200) DEFAULT null,
    job_group         varchar(200) DEFAULT null,
    is_nonconcurrent  boolean      DEFAULT null,
    requests_recovery boolean      DEFAULT null,
    PRIMARY KEY (sched_name, entry_id)
);

CREATE TABLE QRTZ_PAUSED_TRIGGER_GRPS
(
    sched_name    varchar(120) not null,
    trigger_group varchar(200) not null,
    PRIMARY KEY (sched_name, trigger_group)
);

CREATE TABLE QRTZ_SCHEDULER_STATE
(
    sched_name        varchar(120) not null,
    instance_name     varchar(200) not null,
    last_checkin_time long         not null,
    checkin_interval  long         not null,
    PRIMARY KEY (sched_name, instance_name)
);

CREATE TABLE QRTZ_LOCKS
(
    sched_name varchar(120) not null,
    lock_name  varchar(40)  not null,
    PRIMARY KEY (sched_name, lock_name)
);

CREATE TABLE QRTZ_SIMPLE_TRIGGERS
(
    sched_name       varchar(120) not null,
    trigger_name     varchar(200) not null,
    trigger_group    varchar(200) not null,
    repeate_count    long         not null,
    repeate_interval long         not null,
    timeS_triggerED  long         not null,
    PRIMARY KEY (sched_name, trigger_name, trigger_group),
    CONSTRAINT FK_qrtz_simple_triggerS_qrtz_triggerS FOREIGN KEY (sched_name, trigger_name, trigger_group) REFERENCES QRTZ_TRIGGERS (sched_name, trigger_name, trigger_group) ON DELETE CASCADE
);

CREATE TABLE QRTZ_SIMPROP_TRIGGERS
(
    sched_name    varchar(120) not null,
    trigger_name  varchar(200) not null,
    trigger_group varchar(200) not null,
    str_prop_1    varchar(512) DEFAULT null,
    str_prop_2    varchar(512) DEFAULT null,
    str_prop_3    varchar(512) DEFAULT null,
    int_prop_1    integer      DEFAULT null,
    int_prop_2    integer      DEFAULT null,
    long_prop_1   long         DEFAULT null,
    long_prop_2   long         DEFAULT null,
    dec_prop_1    double(13, 4
) DEFAULT null,
    dec_prop_2 double(13,4) DEFAULT null,
    bool_prop_1 boolean DEFAULT null,
    bool_prop_2 boolean DEFAULT null,
	PRIMARY KEY(sched_name, trigger_name, trigger_group ),
	CONSTRAINT FK_qrtz_simprop_triggerS_qrtz_triggerS FOREIGN KEY (sched_name, trigger_name, trigger_group ) REFERENCES QRTZ_TRIGGERS (sched_name, trigger_name, trigger_group ) ON DELETE CASCADE
);

CREATE TABLE QRTZ_BLOB_TRIGGERS
(
    sched_name    varchar(120) not null,
    trigger_name  varchar(200) not null,
    trigger_group varchar(200) not null,
    BLOB_data     blob DEFAULT null
);

INSERT INTO version(current_version)
VALUES ('1.0.0');