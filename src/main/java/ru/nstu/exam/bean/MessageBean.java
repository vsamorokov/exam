package ru.nstu.exam.bean;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MessageBean extends EntityBean {

    private String text;
    private long sendTime;
    //    private Artefact artefact
    private AccountBean account;
}
