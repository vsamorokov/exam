package ru.nstu.exam.bean;

import lombok.Data;

import java.util.List;

@Data
public class CreateTeacherBean {
    private AccountBean account;
    private List<Long> disciplineIds;
}
