package ru.nstu.exam.bean.student;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.nstu.exam.bean.ExamBean;
import ru.nstu.exam.bean.ExamRuleBean;
import ru.nstu.exam.bean.StudentRatingBean;
import ru.nstu.exam.bean.TeacherBean;

@Data
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StudentExamInfoBean extends StudentRatingBean {
    private ExamBean exam;
    private ExamRuleBean examRule;
    private TeacherBean teacher;
}
