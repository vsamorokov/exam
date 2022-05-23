package ru.nstu.exam.entity;


import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.util.List;

@Data
@Entity
@Table(name = "theme")
@EqualsAndHashCode(callSuper = true)
@Where(clause = "deleted = false")
public class Theme extends NamedEntity {

    @OneToMany(mappedBy = "theme")
    private List<Task> tasks;

    @ManyToMany(mappedBy = "themes")
    private List<ExamRule> examRules;

    @ManyToOne
    @JoinColumn(name = "discipline_id")
    private Discipline discipline;
}
