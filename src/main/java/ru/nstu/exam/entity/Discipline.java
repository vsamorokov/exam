package ru.nstu.exam.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.Where;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.List;

@Data
@Entity
@Table(name = "discipline")
@EqualsAndHashCode(callSuper = true)
@Where(clause = "deleted = false")
public class Discipline extends NamedEntity {

    @OneToMany(mappedBy = "discipline")
    private List<ExamRule> examRules;

    @OneToMany(mappedBy = "discipline")
    private List<Theme> themes;

    @OneToMany(mappedBy = "discipline")
    private List<Exam> exams;

    @OneToMany(mappedBy = "discipline")
    private List<GroupRating> groupRatings;
}
