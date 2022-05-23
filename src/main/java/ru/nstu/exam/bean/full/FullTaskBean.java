package ru.nstu.exam.bean.full;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import ru.nstu.exam.bean.ArtefactBean;
import ru.nstu.exam.bean.TaskBean;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FullTaskBean {
    private TaskBean task;
    private ArtefactBean artefact;
}
