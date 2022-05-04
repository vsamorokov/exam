package ru.nstu.exam.bean;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FullExamRuleBean {
    private ExamRuleBean examRule;
    private FullDisciplineBean discipline;
    private List<FullThemeBean> themes;
}
