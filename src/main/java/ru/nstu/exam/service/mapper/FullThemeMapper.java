package ru.nstu.exam.service.mapper;

import liquibase.repackaged.org.apache.commons.collections4.CollectionUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.nstu.exam.bean.FullTaskBean;
import ru.nstu.exam.bean.FullThemeBean;
import ru.nstu.exam.entity.Task;
import ru.nstu.exam.entity.Theme;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Component
@RequiredArgsConstructor
public class FullThemeMapper implements Mapper<FullThemeBean, Theme> {

    private final FullTaskMapper taskMapper;

    @Override
    public FullThemeBean map(Theme entity, int level) {
        FullThemeBean themeBean = new FullThemeBean();
        if (level >= 0) {
            themeBean.setId(entity.getId());
            themeBean.setName(themeBean.getName());
            themeBean.setDisciplineId(entity.getDiscipline().getId());
        }
        if (level >= 1) {
            Collection<Task> tasks = CollectionUtils.emptyIfNull(entity.getTasks());
            List<FullTaskBean> taskBeans = new ArrayList<>(tasks.size());
            for (Task task : tasks) {
                taskBeans.add(taskMapper.map(task, level - 1));
            }
            themeBean.setTasks(taskBeans);
        }
        return themeBean;
    }
}
