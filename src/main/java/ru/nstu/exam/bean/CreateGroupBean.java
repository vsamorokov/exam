package ru.nstu.exam.bean;

import lombok.Data;

import java.util.List;

@Data
public class CreateGroupBean {
    private String name;
    private List<Long> disciplineIds;
}
