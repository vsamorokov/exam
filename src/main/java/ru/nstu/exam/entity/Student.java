package ru.nstu.exam.entity;


import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.util.List;

@Data
@Entity
@Table(name = "student")
@EqualsAndHashCode(callSuper = true)
@Where(clause = "deleted = false")
public class Student extends PersistableEntity {

    @OneToOne
    @JoinColumn(name = "account_id")
    private Account account;

    @ManyToOne
    @JoinColumn(name = "group_id")
    private Group group;

    @OneToMany(mappedBy = "student")
    private List<Ticket> tickets;
}
