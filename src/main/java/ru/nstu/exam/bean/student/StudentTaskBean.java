package ru.nstu.exam.bean.student;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.nstu.exam.bean.EntityBean;
import ru.nstu.exam.enums.TaskType;

@Data
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StudentTaskBean extends EntityBean {

    private String text;

    private Long artefactId;

    private TaskType taskType;

    private String themeName;
}
