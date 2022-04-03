create table message
(
    id             bigserial primary key,
    deleted        boolean   not null,
    created        timestamp not null,
    updated        timestamp not null,
    text           varchar(1024),
    send_time      timestamp not null,
    artefact_id    bigint    null references artefact (id),
    account_id     bigint references account (id),
    answer_id      bigint    null references answer (id),
    exam_period_id bigint references exam_period (id)
)
