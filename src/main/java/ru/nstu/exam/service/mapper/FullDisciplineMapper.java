package ru.nstu.exam.service.mapper;

import liquibase.repackaged.org.apache.commons.collections4.CollectionUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.nstu.exam.bean.DisciplineBean;
import ru.nstu.exam.bean.FullDisciplineBean;
import ru.nstu.exam.bean.FullThemeBean;
import ru.nstu.exam.entity.Discipline;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class FullDisciplineMapper implements Mapper<FullDisciplineBean, Discipline> {

    private final FullThemeMapper themeMapper;

    @Override
    public FullDisciplineBean map(Discipline entity, int level) {
        FullDisciplineBean bean = new FullDisciplineBean();
        if (level >= 0) {
            DisciplineBean disciplineBean = new DisciplineBean();
            disciplineBean.setId(entity.getId());
            disciplineBean.setName(entity.getName());
            bean.setDiscipline(disciplineBean);
        }
        if (level >= 1) {
            List<FullThemeBean> themeBeans = CollectionUtils.emptyIfNull(entity.getThemes())
                    .stream()
                    .map(t -> themeMapper.map(t, level - 1))
                    .collect(Collectors.toList());

            bean.setThemes(themeBeans);
        }

        return bean;
    }
}
