package ru.nstu.exam.bean.full;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import ru.nstu.exam.bean.ThemeBean;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FullThemeBean {
    private ThemeBean theme;
    private List<FullTaskBean> tasks;
}
