alter table exam
    add column teacher_id bigint references teacher (id);

update exam
set teacher_id = (select max(teacher.id) from teacher);

alter table exam
    alter column teacher_id set not null;