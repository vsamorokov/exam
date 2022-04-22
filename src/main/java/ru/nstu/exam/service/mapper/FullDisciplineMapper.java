package ru.nstu.exam.service.mapper;

import liquibase.repackaged.org.apache.commons.collections4.CollectionUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.nstu.exam.bean.FullDisciplineBean;
import ru.nstu.exam.bean.FullThemeBean;
import ru.nstu.exam.entity.Discipline;
import ru.nstu.exam.entity.Theme;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class FullDisciplineMapper implements Mapper<FullDisciplineBean, Discipline> {

    private final FullThemeMapper themeMapper;

    @Override
    public FullDisciplineBean map(Discipline entity, int level) {

        FullDisciplineBean bean = new FullDisciplineBean();
        if (level >= 0) {
            bean.setId(entity.getId());
            bean.setName(entity.getName());
        }
        if (level >= 1) {
            List<FullThemeBean> themeBeans = new ArrayList<>(CollectionUtils.emptyIfNull(entity.getThemes()).size());
            for (Theme theme : CollectionUtils.emptyIfNull(entity.getThemes())) {
                themeBeans.add(themeMapper.map(theme, level - 1));
            }
            bean.setThemes(themeBeans);
        }

        return null;
    }
}
