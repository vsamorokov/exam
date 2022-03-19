package ru.nstu.exam.entity;


import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.util.List;

@Data
@Entity
@Table(name = "exam_rule")
@EqualsAndHashCode(callSuper = true)
public class ExamRule extends PersistableEntity {

    @ManyToMany
    @JoinTable(
            name = "exam_rule_theme",
            joinColumns = @JoinColumn(name = "exam_rule_id"),
            inverseJoinColumns = @JoinColumn(name = "theme_id")
    )
    private List<Theme> themes;

    @ManyToOne
    @JoinColumn(name = "discipline_id")
    private Discipline discipline;

    @Column(name = "question_count")
    private Integer questionCount;

    @Column(name = "exercise_count")
    private Integer exerciseCount;

    @Column(name = "duration", nullable = false)
    private Integer duration; // minutes
}
