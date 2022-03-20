package ru.nstu.exam.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.util.List;

@Data
@Entity
@Table(name = "exam")
@EqualsAndHashCode(callSuper = true)
public class Exam extends PersistableEntity {

    @OneToMany(mappedBy = "exam")
    private List<ExamPeriod> examPeriods;

    @ManyToOne
    @JoinColumn(name = "exam_rule_id")
    private ExamRule examRule;

    @ManyToOne
    @JoinColumn(name = "teacher_id")
    private Teacher teacher;

    @ManyToMany
    @JoinTable(
            name="exam_group",
            joinColumns = @JoinColumn(name = "exam_id"),
            inverseJoinColumns = @JoinColumn(name = "group_id")
    )
    private List<Group> groups;
}
