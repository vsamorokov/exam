package ru.nstu.exam.bean.full;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import ru.nstu.exam.bean.ExamRuleBean;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FullExamRuleBean {
    private ExamRuleBean examRule;
    private FullDisciplineBean discipline;
    private List<FullThemeBean> themes;
}
