package ru.nstu.exam.bean;

import lombok.Data;

import java.util.List;

@Data
public class CreateExamRuleBean {

    private String name;

    private List<Long> themeIds;

    private Long disciplineId;

    private Integer questionCount;

    private Integer exerciseCount;

    private Integer duration; // minutes

    private Integer minimalRating;

    private Long ratingSystemId;
}
