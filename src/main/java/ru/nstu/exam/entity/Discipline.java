package ru.nstu.exam.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.util.List;

@Data
@Entity
@Table(name = "discipline")
@EqualsAndHashCode(callSuper = true)
public class Discipline extends PersistableEntity {

    @Column(name = "name", nullable = false)
    private String name;

    @ManyToMany(mappedBy = "disciplines")
    private List<Teacher> teachers;

    @OneToMany(mappedBy = "discipline")
    private List<ExamRule> examRules;

    @ManyToMany(mappedBy = "disciplines")
    private List<Group> groups;
}
