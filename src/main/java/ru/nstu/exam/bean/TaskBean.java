package ru.nstu.exam.bean;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.nstu.exam.enums.TaskType;

@Data
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TaskBean extends EntityBean {

    private Integer cost;

    private String text;

//    private Artefact artefact;

    private TaskType taskType;

    private ThemeBean theme;
}
