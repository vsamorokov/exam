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

    private List<Long> themeIds;

    private Long disciplineId;

    private Integer questionCount;

    private Integer exerciseCount;

    private Integer duration; // minutes

    private Integer minimalRating;

    private Long ratingSystemId;
}
