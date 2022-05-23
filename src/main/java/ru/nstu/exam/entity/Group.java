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
@Table(name = "\"group\"")
@EqualsAndHashCode(callSuper = true)
@Where(clause = "deleted = false")
public class Group extends NamedEntity {

    @OneToMany(mappedBy = "group")
    private List<Student> students;

    @OneToMany(mappedBy = "group")
    private List<Exam> exams;

    @OneToMany(mappedBy = "group")
    private List<GroupRating> groupRatings;
}
