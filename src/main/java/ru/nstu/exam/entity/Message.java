package ru.nstu.exam.entity;


import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "message")
@EqualsAndHashCode(callSuper = true)
@Where(clause = "deleted = false")
public class Message extends PersistableEntity {

    @Column(name = "text")
    private String text;

    @Column(name = "send_time", nullable = false)
    private LocalDateTime sendTime;

    @OneToOne
    @JoinColumn(name = "artefact_id")
    private Artefact artefact;

    @ManyToOne
    @JoinColumn(name = "account_id")
    private Account account;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "answer_id")
    private Answer answer;
}
