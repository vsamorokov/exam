package ru.nstu.exam.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.nstu.exam.enums.TaskType;

import javax.persistence.*;

@Data
@Entity
@Table(name = "task")
@EqualsAndHashCode(callSuper = true)
public class Task extends PersistableEntity {

    @Column(name = "cost", nullable = false)
    private Integer cost;

    @Column(name = "text", length = 2048)
    private String text;

    @OneToOne
    @JoinColumn(name = "artefact_id", referencedColumnName = "id")
    private Artefact artefact;

    @Enumerated(EnumType.STRING)
    @Column(name = "task_type", nullable = false)
    private TaskType taskType;

    @ManyToOne
    @JoinColumn(name = "theme_id")
    private Theme theme;
}