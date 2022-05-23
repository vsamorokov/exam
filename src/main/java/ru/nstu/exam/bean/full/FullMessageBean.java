package ru.nstu.exam.bean.full;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import ru.nstu.exam.bean.AccountBean;
import ru.nstu.exam.bean.ArtefactBean;
import ru.nstu.exam.bean.MessageBean;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FullMessageBean {
    private MessageBean message;
    private ArtefactBean artefact;
    private AccountBean account;
}
