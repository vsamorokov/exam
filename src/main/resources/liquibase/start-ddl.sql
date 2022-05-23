create sequence HIBERNATE_SEQUENCE
    minvalue 1
    maxvalue 9999999999999999
    start with 1
    increment by 1
    cache 20;

create table account
(
    id       bigserial primary key,
    deleted  boolean       not null,
    created  timestamp     not null,
    updated  timestamp     not null,
    username varchar(1024) not null,
    password varchar(255)  not null,
    roles    varchar(512)  not null,
    name     varchar(512)  not null,
    surname  varchar(512)  not null
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
    local_name    varchar(1024) not null,
    file_size     int8          not null,
    artefact_type varchar(1024) not null,
    file_name     varchar(1024) not null
);

create table discipline
(
    id      bigserial primary key,
    deleted boolean       not null,
    created timestamp     not null,
    updated timestamp     not null,
    name    varchar(1024) not null
);

create table theme
(
    id            bigserial primary key,
    deleted       boolean                           not null,
    created       timestamp                         not null,
    updated       timestamp                         not null,
    name          varchar(2048)                     not null,
    discipline_id bigint references discipline (id) not null
);

create table task
(
    id          bigserial primary key,
    deleted     boolean                      not null,
    created     timestamp                    not null,
    updated     timestamp                    not null,
    text        varchar(2048),
    artefact_id bigint                       not null,
    task_type   varchar(255)                 not null,
    theme_id    bigint references theme (id) not null
);

create table "group"
(
    id      bigserial primary key,
    deleted boolean      not null,
    created timestamp    not null,
    updated timestamp    not null,
    name varchar(512) not null
);

create table exam_rule
(
    id                             bigserial primary key,
    deleted                        boolean                           not null,
    created                        timestamp                         not null,
    updated                        timestamp                         not null,
    discipline_id                  bigint references discipline (id) not null,
    name                           varchar(1024)                     not null,
    duration                       int                               not null,
    minimal_semester_rating        int                               not null,
    minimal_exam_rating            int                               not null,
    maximum_exam_rating            int                               not null,
    single_question_default_rating int                               not null,
    single_exercise_default_rating int                               not null,
    questions_rating_sum           int                               not null,
    exercises_rating_sum           int                               not null
);

create table exam_rule_theme
(
    id           bigserial primary key,
    exam_rule_id bigint references exam_rule (id) not null,
    theme_id     bigint references theme (id)     not null
);

create table exam
(
    id            bigserial primary key,
    deleted       boolean                           not null,
    created       timestamp                         not null,
    updated       timestamp                         not null,
    name          varchar(1024)                     not null,
    discipline_id bigint references discipline (id) null,
    group_id      bigint references "group" (id)    null,
    one_group     boolean                           not null,
    start         timestamp                         null,
    "end"         timestamp                         null,
    state         varchar(255)                      not null
);

create table student
(
    id         bigserial primary key,
    deleted    boolean                        not null,
    created    timestamp                      not null,
    updated    timestamp                      not null,
    account_id bigint references account (id) not null,
    group_id   bigint references "group" (id) not null,
    status     varchar(255)                   not null

);

create table group_rating
(
    id            bigserial primary key,
    deleted       boolean                           not null,
    created       timestamp                         not null,
    updated       timestamp                         not null,
    name          varchar(512)                      not null,
    discipline_id bigint references discipline (id) not null,
    group_id      bigint references "group" (id)    not null,
    exam_rule_id  bigint references exam_rule (id)  not null
);

create table student_rating
(
    id              bigserial primary key,
    deleted         boolean                             not null,
    created         timestamp                           not null,
    updated         timestamp                           not null,
    semester_rating int,
    question_rating int,
    exercise_rating int,
    exam_id         bigint references exam (id)         not null,
    student_id      bigint references student (id)      not null,
    group_rating_id bigint references group_rating (id) not null,
    state           varchar(255)                        not null
);

create table answer
(
    id                bigserial primary key,
    deleted           boolean                               not null,
    created           timestamp                             not null,
    updated           timestamp                             not null,
    task_id           bigint references task (id)           not null,
    rating            int                                   not null,
    student_rating_id bigint references student_rating (id) not null,
    number            int                                   not null,
    status            varchar(255)                          not null
);

create table message
(
    id          bigserial primary key,
    deleted     boolean   not null,
    created     timestamp not null,
    updated     timestamp not null,
    text        varchar(2048),
    send_time   timestamp not null,
    artefact_id bigint    null references artefact (id),
    account_id  bigint references account (id),
    answer_id   bigint    null references answer (id)
);