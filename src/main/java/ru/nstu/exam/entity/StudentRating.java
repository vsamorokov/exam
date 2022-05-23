package ru.nstu.exam.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.Where;
import ru.nstu.exam.enums.StudentRatingState;

import javax.persistence.*;
import java.util.List;

@Data
@Entity
@Table(name = "student_rating")
@EqualsAndHashCode(callSuper = true)
@Where(clause = "deleted = false")
public class StudentRating extends PersistableEntity {

    @OneToMany(mappedBy = "studentRating")
    private List<Answer> answers;

    @Column(name = "semester_rating")
    private Integer semesterRating;

    @Column(name = "question_rating")
    private Integer questionRating;

    @Column(name = "exercise_rating")
    private Integer exerciseRating;

    @ManyToOne
    @JoinColumn(name = "exam_id")
    private Exam exam;

    @ManyToOne
    @JoinColumn(name = "student_id")
    private Student student;

    @ManyToOne
    @JoinColumn(name = "group_rating_id")
    private GroupRating groupRating;

    @Enumerated(EnumType.STRING)
    @Column(name = "state", nullable = false)
    private StudentRatingState studentRatingState;
}

