package ru.nstu.exam.bean;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ExamRuleBean extends EntityBean {

    private String name;

    private List<ThemeBean> themes;

    private DisciplineBean discipline;

    private Integer questionCount;

    private Integer exerciseCount;

    private Integer duration; // minutes

    private Integer minimalRating; // minutes
}
