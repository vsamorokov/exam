package ru.nstu.exam.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.jpa.domain.AbstractPersistable;

import javax.persistence.*;

@Data
@Entity
@Table(name = "artefact")
@EqualsAndHashCode(callSuper = true)
public class Artefact extends AbstractPersistable<Long> {

    @Column(name = "local_name", nullable = false)
    private String localName;

    @Column(name = "file_size", nullable = false)
    private Long fileSize;

    @Enumerated(EnumType.STRING)
    @Column(name = "artefact_type", nullable = false)
    private ArtefactType artefactType;

    @Column(name = "file_name", nullable = false)
    private String fileName;
}
