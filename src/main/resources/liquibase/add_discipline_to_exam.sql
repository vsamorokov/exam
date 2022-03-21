alter table exam
    add column discipline_id bigint references discipline (id);

alter table exam_period
    add column state varchar(255);