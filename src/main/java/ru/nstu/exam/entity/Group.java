package ru.nstu.exam.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.util.List;

@Data
@Entity
@Table(name = "\"group\"")
@EqualsAndHashCode(callSuper = true)
public class Group extends PersistableEntity {

    @Column(name = "name")
    private String name;

    @OneToMany(mappedBy = "group")
    private List<Student> students;

    @ManyToMany(mappedBy = "groups")
    private List<Exam> exams;

    @ManyToMany
    @JoinTable(name="group_discipline",
            joinColumns = @JoinColumn(name="group_id"),
            inverseJoinColumns = @JoinColumn(name="discipline_id")
    )
    private List<Discipline> disciplines;
}
