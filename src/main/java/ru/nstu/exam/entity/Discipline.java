package ru.nstu.exam.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.util.List;

@Data
@Entity
@Table(name = "discipline")
@EqualsAndHashCode(callSuper = true)
@Where(clause = "deleted = false")
public class Discipline extends PersistableEntity {

    @Column(name = "name", nullable = false)
    private String name;

    @ManyToMany(mappedBy = "disciplines")
    private List<Teacher> teachers;

    @OneToMany(mappedBy = "discipline")
    private List<ExamRule> examRules;

    @OneToMany(mappedBy = "discipline")
    private List<Theme> themes;

    @ManyToMany(mappedBy = "disciplines")
    private List<Group> groups;
}
