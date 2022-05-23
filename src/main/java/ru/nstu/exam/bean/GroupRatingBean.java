package ru.nstu.exam.bean;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GroupRatingBean extends EntityBean {
    private String name = "";
    private Long disciplineId;
    private Long groupId;
    private Long examRuleId;
}
