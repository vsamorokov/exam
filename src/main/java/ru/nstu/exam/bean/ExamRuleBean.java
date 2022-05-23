package ru.nstu.exam.bean;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ExamRuleBean extends EntityBean {

    private String name = "";

    private Integer duration = 180; // minutes

    private Integer minimalSemesterRating = 20;

    private Integer minimalExamRating = 30; // To pass an exam

    private Integer maximumExamRating = 60; // 100% of work done

    private Integer singleQuestionDefaultRating = 2;

    private Integer singleExerciseDefaultRating = 10;

    private Integer questionsRatingSum = 10;

    private Integer exercisesRatingSum = 10;

    private Long disciplineId;

    private List<Long> themeIds = new ArrayList<>();
}
