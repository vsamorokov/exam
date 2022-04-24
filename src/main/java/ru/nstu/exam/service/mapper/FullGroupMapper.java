package ru.nstu.exam.service.mapper;

import liquibase.repackaged.org.apache.commons.collections4.CollectionUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.nstu.exam.bean.*;
import ru.nstu.exam.entity.Group;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class FullGroupMapper implements Mapper<FullGroupBean, Group> {

    private final FullDisciplineMapper disciplineMapper;
    private final StudentMapper studentMapper;

    @Override
    public FullGroupBean map(Group entity, int level) {
        FullGroupBean bean = new FullGroupBean();
        if (level >= 0) {
            GroupBean groupBean = new GroupBean();
            groupBean.setId(entity.getId());
            groupBean.setName(entity.getName());
            bean.setGroup(groupBean);
        }
        if (level >= 1) {
            List<DisciplineBean> disciplineBeans = CollectionUtils.emptyIfNull(entity.getDisciplines())
                    .stream()
                    .map(disciplineMapper::map)
                    .map(FullDisciplineBean::getDiscipline)
                    .collect(Collectors.toList());
            bean.setDisciplines(disciplineBeans);

            List<StudentBean> studentBeans = CollectionUtils.emptyIfNull(entity.getStudents())
                    .stream()
                    .map(studentMapper::map)
                    .collect(Collectors.toList());
            bean.setStudents(studentBeans);
        }
        return bean;
    }
}
