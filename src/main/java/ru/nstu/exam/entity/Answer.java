package ru.nstu.exam.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.util.List;

@Data
@Entity
@Table(name="answer")
@EqualsAndHashCode(callSuper = true)
@Where(clause = "deleted = false")
public class Answer extends PersistableEntity {

    @ManyToOne
    @JoinColumn(name = "task_id")
    private Task task;

    @Column(name = "rating")
    private Integer rating;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ticket_id")
    private Ticket ticket;

    @OneToMany(mappedBy = "answer")
    private List<Message> messages;
}
