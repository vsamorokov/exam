package ru.nstu.exam.bean;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.nstu.exam.entity.ArtefactType;

@Data
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ArtefactBean extends EntityBean {
    private Long fileSize;
    private ArtefactType artefactType;
    private String fileName;
}
