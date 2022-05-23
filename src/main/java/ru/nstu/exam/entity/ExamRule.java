package ru.nstu.exam.entity;


import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.util.List;

@Data
@Entity
@Table(name = "exam_rule")
@EqualsAndHashCode(callSuper = true)
@Where(clause = "deleted = false")
public class ExamRule extends NamedEntity {

    @Column(name = "duration", nullable = false)
    private Integer duration; // minutes

    @Column(name = "minimal_semester_rating", nullable = false)
    private Integer minimalSemesterRating;

    @Column(name = "minimal_exam_rating", nullable = false)
    private Integer minimalExamRating; // To pass an exam

    @Column(name = "maximum_exam_rating", nullable = false)
    private Integer maximumExamRating; // 100% of work done

    @Column(name = "single_question_default_rating", nullable = false)
    private Integer singleQuestionDefaultRating;

    @Column(name = "single_exercise_default_rating", nullable = false)
    private Integer singleExerciseDefaultRating;

    @Column(name = "questions_rating_sum", nullable = false)
    private Integer questionsRatingSum;

    @Column(name = "exercises_rating_sum", nullable = false)
    private Integer exercisesRatingSum;

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

    @OneToMany(mappedBy = "examRule")
    private List<GroupRating> groupRatings;
}
