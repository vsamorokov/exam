package ru.nstu.exam.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.Where;
import ru.nstu.exam.enums.ExamPeriodState;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@Table(name = "exam_period")
@EqualsAndHashCode(callSuper = true)
@Where(clause = "deleted = false")
public class ExamPeriod extends PersistableEntity {

    @Column(name="\"start\"")
    private LocalDateTime start;
    @Column(name = "\"end\"")
    private LocalDateTime end;

    @OneToMany(mappedBy = "examPeriod")
    private List<Ticket> tickets;

    @ManyToOne
    @JoinColumn(name = "exam_id")
    private Exam exam;

    @Column(name = "state")
    @Enumerated(EnumType.STRING)
    private ExamPeriodState state;

}
