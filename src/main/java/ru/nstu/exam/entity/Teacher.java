package ru.nstu.exam.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.util.List;

@Data
@Entity
@Table(name="teacher")
@EqualsAndHashCode(callSuper = true)
@Where(clause = "deleted = false")
public class Teacher extends PersistableEntity {

    @OneToOne
    @JoinColumn(name = "account_id", referencedColumnName = "id")
    private Account account;

    @ManyToMany
    @JoinTable(name="teacher_discipline",
            joinColumns = @JoinColumn(name="teacher_id"),
            inverseJoinColumns = @JoinColumn(name="discipline_id")
    )
    private List<Discipline> disciplines;

    @OneToMany(mappedBy = "teacher")
    private List<Exam> exams;
}
