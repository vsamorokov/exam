package ru.nstu.exam.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.Where;
import ru.nstu.exam.enums.ExamState;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@Table(name = "exam")
@EqualsAndHashCode(callSuper = true)
@Where(clause = "deleted = false")
public class Exam extends NamedEntity {

    @ManyToOne
    @JoinColumn(name = "discipline_id")
    private Discipline discipline;

    @ManyToOne
    @JoinColumn(name = "group_id")
    private Group group;

    @Column(name = "one_group", nullable = false)
    private boolean oneGroup;

    @Column(name = "\"start\"")
    private LocalDateTime start;

    @Column(name = "\"end\"")
    private LocalDateTime end;

    @OneToMany(mappedBy = "exam")
    private List<StudentRating> studentRatings;

    @ManyToOne
    @JoinColumn(name = "teacher_id", nullable = false)
    private Teacher teacher;

    @Column(name = "state", nullable = false)
    @Enumerated(EnumType.STRING)
    private ExamState state;
}
