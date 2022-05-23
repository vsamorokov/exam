package ru.nstu.exam.entity;


import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

@Data
@MappedSuperclass
@EqualsAndHashCode(callSuper = true)
public class NamedEntity extends PersistableEntity {

    @Column(name = "name", nullable = false)
    private String name;

}
