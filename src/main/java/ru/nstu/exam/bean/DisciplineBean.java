package ru.nstu.exam.bean;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class DisciplineBean extends EntityBean {
    private String name;
}
