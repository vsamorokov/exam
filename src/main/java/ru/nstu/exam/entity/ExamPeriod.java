package ru.nstu.exam.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@Table(name = "exam_period")
@EqualsAndHashCode(callSuper = true)
public class ExamPeriod extends PersistableEntity {

    private LocalDateTime start;
    private LocalDateTime end;

    @OneToMany(mappedBy = "examPeriod")
    private List<Ticket> tickets;

    @ManyToOne
    @JoinColumn(name = "exam_id")
    private Exam exam;

}
