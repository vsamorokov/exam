package ru.nstu.exam.bean;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ExamBean extends EntityBean {

    private ExamRuleBean examRule;

    private TeacherBean teacher;

    private DisciplineBean discipline;

    private List<GroupBean> groups;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime startTime; // for first creation
}
