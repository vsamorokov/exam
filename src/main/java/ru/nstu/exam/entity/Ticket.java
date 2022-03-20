package ru.nstu.exam.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.util.List;

@Data
@Entity
@Table(name = "ticket")
@EqualsAndHashCode(callSuper = true)
@Where(clause = "deleted = false")
public class Ticket extends PersistableEntity {

    @OneToMany(mappedBy = "ticket")
    private List<Answer> answers;

    @Column(name = "semester_rating")
    private Integer semesterRating;

    @Column(name = "exam_rating")
    private Integer examRating;

    @Column(name = "allowed")
    private Boolean allowed;

    @ManyToOne
    @JoinColumn(name = "exam_period_id")
    private ExamPeriod examPeriod;

    @ManyToOne
    @JoinColumn(name = "student_id")
    private Student student;

}

