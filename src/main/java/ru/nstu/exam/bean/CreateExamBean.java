package ru.nstu.exam.bean;

import lombok.Data;

import java.util.List;

@Data
public class CreateExamBean {

    private Long examRuleId;

    private Long disciplineId;

    private List<Long> groupIds;

    private Long startTime;
}
