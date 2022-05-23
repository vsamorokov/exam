package ru.nstu.exam.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.Where;
import ru.nstu.exam.enums.AnswerState;

import javax.persistence.*;
import java.util.List;

@Data
@Entity
@Table(name = "answer")
@EqualsAndHashCode(callSuper = true)
@Where(clause = "deleted = false")
public class Answer extends PersistableEntity {

    @ManyToOne
    @JoinColumn(name = "task_id")
    private Task task;

    @Column(name = "rating", nullable = false)
    private Integer rating = 0;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_rating_id")
    private StudentRating studentRating;

    @OneToMany(mappedBy = "answer")
    private List<Message> messages;

    @Column(name = "number")
    private Integer number;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private AnswerState state;
}
