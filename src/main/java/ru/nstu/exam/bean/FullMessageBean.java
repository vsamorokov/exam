package ru.nstu.exam.bean;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FullMessageBean {
    private MessageBean message;
    private ArtefactBean artefact;
    private AccountBean account;
}
