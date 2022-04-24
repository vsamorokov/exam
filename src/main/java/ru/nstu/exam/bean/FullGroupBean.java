package ru.nstu.exam.bean;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FullGroupBean {
    private GroupBean group;
    private List<StudentBean> students;
    private List<DisciplineBean> disciplines;
}
