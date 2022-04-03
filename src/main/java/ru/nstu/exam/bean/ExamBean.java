package ru.nstu.exam.bean;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ExamBean extends EntityBean {

    private ExamRuleBean examRule;

    private TeacherBean teacher;

    private DisciplineBean discipline;

    private List<GroupBean> groups;

    private Long startTime; // for first creation
}
