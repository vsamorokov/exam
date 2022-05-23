package ru.nstu.exam.bean.full;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import ru.nstu.exam.bean.DisciplineBean;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FullDisciplineBean {
    private DisciplineBean discipline;
    private List<FullThemeBean> themes;
}
