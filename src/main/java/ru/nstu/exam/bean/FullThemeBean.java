package ru.nstu.exam.bean;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FullThemeBean extends ThemeBean {
    private List<FullTaskBean> tasks;
}
