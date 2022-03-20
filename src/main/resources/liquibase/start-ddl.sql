create sequence HIBERNATE_SEQUENCE
    minvalue 1
    maxvalue 9999999999999999
    start with 1
    increment by 1
    cache 20;

create table account
(
    id       bigserial primary key,
    deleted  boolean      not null,
    created  timestamp    not null,
    updated  timestamp    not null,
    username varchar(255) not null,
    password varchar(255) not null,
    roles    varchar(255) not null,
    name     varchar(255) not null,
    surname  varchar(255) not null
);

create
    index username_idx on account (username);

create table access_token
(
    id         bigserial primary key,
    token      varchar(128)                   not null,
    account_id bigint references account (id) not null,
    issue_time timestamp                      not null
);

create
    index token_idx on access_token (token);


create table teacher
(
    id         bigserial primary key,
    deleted    boolean                        not null,
    created    timestamp                      not null,
    updated    timestamp                      not null,
    account_id bigint references account (id) not null
);

create table artefact
(
    id            bigserial primary key,
    artefact_id   varchar(255) not null,
    filesize      int          not null,
    artefact_type varchar(255) not null
);

create table discipline
(
    id      bigserial primary key,
    deleted boolean      not null,
    created timestamp    not null,
    updated timestamp    not null,
    name    varchar(255) not null
);

create table theme
(
    id            bigserial primary key,
    deleted       boolean                           not null,
    created       timestamp                         not null,
    updated       timestamp                         not null,
    name          varchar(255)                      not null,
    discipline_id bigint references discipline (id) not null
);

create table task
(
    id          bigserial primary key,
    deleted     boolean                      not null,
    created     timestamp                    not null,
    updated     timestamp                    not null,
    cost        int                          not null,
    text        varchar(2048),
    artefact_id bigint                       not null,
    task_type   varchar(255)                 not null,
    theme_id    bigint references theme (id) not null
);

create table teacher_discipline
(
    id            bigserial primary key,
    teacher_id    bigint references teacher (id)    not null,
    discipline_id bigint references discipline (id) not null
);

create table "group"
(
    id      bigserial primary key,
    deleted boolean      not null,
    created timestamp    not null,
    updated timestamp    not null,
    name    varchar(255) not null
);

create table group_discipline
(
    id            bigserial primary key,
    group_id      bigint references "group" (id)    not null,
    discipline_id bigint references discipline (id) not null
);

create table exam_rule
(
    id             bigserial primary key,
    deleted        boolean                           not null,
    created        timestamp                         not null,
    updated        timestamp                         not null,
    discipline_id  bigint references discipline (id) not null,
    question_count int,
    exercise_count int,
    duration       int                               not null
);

create table exam_rule_theme
(
    id           bigserial primary key,
    exam_rule_id bigint references exam_rule (id) not null,
    theme_id     bigint references theme (id)     not null
);

create table exam
(
    id           bigserial primary key,
    deleted      boolean                          not null,
    created      timestamp                        not null,
    updated      timestamp                        not null,
    exam_rule_id bigint references exam_rule (id) not null,
    teacher_id   bigint references teacher (id)   not null
);

create table exam_group
(
    id       bigserial primary key,
    exam_id  bigint references exam (id)    not null,
    group_id bigint references "group" (id) not null
);

create table exam_period
(
    id      bigserial primary key,
    deleted boolean                     not null,
    created timestamp                   not null,
    updated timestamp                   not null,
    start   timestamp                   not null,
    "end"   timestamp                   not null,
    exam_id bigint references exam (id) not null
);

create table student
(
    id         bigserial primary key,
    deleted    boolean                        not null,
    created    timestamp                      not null,
    updated    timestamp                      not null,
    account_id bigint references account (id) not null,
    group_id   bigint references "group" (id) not null
);

create table ticket
(
    id              bigserial primary key,
    deleted         boolean                            not null,
    created         timestamp                          not null,
    updated         timestamp                          not null,
    semester_rating int,
    exam_rating     int,
    allowed         boolean,
    exam_period_id  bigint references exam_period (id) not null,
    student_id      bigint references student (id)     not null
);

create table answer
(
    id        bigserial primary key,
    deleted   boolean                       not null,
    created   timestamp                     not null,
    updated   timestamp                     not null,
    task_id   bigint references task (id)   not null,
    rating    int,
    ticket_id bigint references ticket (id) not null
);

