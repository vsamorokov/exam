package ru.nstu.exam.entity;


import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.List;

@Data
@Entity
@Table(name = "theme")
@EqualsAndHashCode(callSuper = true)
public class Theme extends PersistableEntity {

    @Column(name = "name")
    private String name;

    @OneToMany(mappedBy = "theme")
    private List<Task> tasks;
}
