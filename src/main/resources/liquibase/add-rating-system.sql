create table rating_system
(
    id              bigserial primary key,
    deleted         boolean       not null,
    created         timestamp     not null,
    updated         timestamp     not null,
    name            varchar(255)  not null,
    rating_mappings varchar(2048) not null
);

-- Default rating
insert into rating_system
values (0,
        false,
        now(),
        now(),
        'Romanov rating system (-2,0,1,2)',
        '-2,REJECTED;0,APPROVED;1,APPROVED;2,APPROVED;');

alter table exam_rule
    add column rating_system_id bigint not null default 0 references rating_system (id);