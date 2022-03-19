package ru.nstu.exam.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;

@Data
@Entity
@Table(name="answer")
@EqualsAndHashCode(callSuper = true)
public class Answer extends PersistableEntity {

    @ManyToOne
    @JoinColumn(name = "task_id")
    private Task task;

    @Column(name = "rating")
    private Integer rating;

    @ManyToOne
    @JoinColumn(name = "ticket_id")
    private Ticket ticket;

    // TODO: add list of messages

}
