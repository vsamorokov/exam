alter table artefact
    rename artefact_id to local_name;

alter table artefact
    rename filesize to file_size;

alter table artefact
    add column file_name varchar(255) not null default '';
