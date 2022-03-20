package ru.nstu.exam.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.Where;
import org.springframework.data.jpa.domain.AbstractPersistable;

import javax.persistence.*;

@Data
@Entity
@Table(name = "artefact")
@EqualsAndHashCode(callSuper = true)
@Where(clause = "deleted = false")
public class Artefact extends AbstractPersistable<Long> {

    @Column(name = "artefact_id", nullable = false)
    private String artefactId;

    @Column(name = "filesize", nullable = false)
    private Integer filesize;

    @Enumerated(EnumType.STRING)
    @Column(name = "artefact_type", nullable = false)
    private ArtefactType artefactType;
}
