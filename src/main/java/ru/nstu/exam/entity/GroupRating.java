package ru.nstu.exam.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.util.List;

/**
 * Отображает факт сдачи группой экзамена по правилам (из учебного плана)
 */
@Data
@Entity
@Table(name = "group_rating")
@EqualsAndHashCode(callSuper = true)
@Where(clause = "deleted = false")
public class GroupRating extends NamedEntity {

    @ManyToOne
    @JoinColumn(name = "discipline_id")
    private Discipline discipline;

    @ManyToOne
    @JoinColumn(name = "group_id")
    private Group group;

    @ManyToOne
    @JoinColumn(name = "exam_rule_id")
    private ExamRule examRule;

    @OneToMany(mappedBy = "groupRating")
    private List<StudentRating> studentRatings;

}
