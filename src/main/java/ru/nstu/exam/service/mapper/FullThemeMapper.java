package ru.nstu.exam.service.mapper;

import liquibase.repackaged.org.apache.commons.collections4.CollectionUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.nstu.exam.bean.ThemeBean;
import ru.nstu.exam.bean.full.FullTaskBean;
import ru.nstu.exam.bean.full.FullThemeBean;
import ru.nstu.exam.entity.Theme;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class FullThemeMapper implements Mapper<FullThemeBean, Theme> {

    private final FullTaskMapper taskMapper;

    @Override
    public FullThemeBean map(Theme entity, int level) {
        FullThemeBean bean = new FullThemeBean();
        ThemeBean themeBean = new ThemeBean();
        if (level >= 0) {
            themeBean.setId(entity.getId());
            themeBean.setName(entity.getName());
            if (entity.getDiscipline() != null) {
                themeBean.setDisciplineId(entity.getDiscipline().getId());
            }
            bean.setTheme(themeBean);
        }
        if (level >= 1) {
            List<FullTaskBean> taskBeans = CollectionUtils.emptyIfNull(entity.getTasks())
                    .stream()
                    .map(t -> taskMapper.map(t, level - 1))
                    .collect(Collectors.toList());

            bean.setTasks(taskBeans);
        }
        return bean;
    }
}
