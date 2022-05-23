package ru.nstu.exam.service.mapper;

import liquibase.repackaged.org.apache.commons.collections4.CollectionUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import ru.nstu.exam.bean.GroupBean;
import ru.nstu.exam.bean.StudentBean;
import ru.nstu.exam.bean.full.FullGroupBean;
import ru.nstu.exam.bean.full.FullGroupRatingBean;
import ru.nstu.exam.entity.Group;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class FullGroupMapper implements Mapper<FullGroupBean, Group> {

    private final StudentMapper studentMapper;
    @Lazy
    private final FullGroupRatingMapper groupRatingMapper;

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
            List<FullGroupRatingBean> groupRatings = CollectionUtils.emptyIfNull(entity.getGroupRatings())
                    .stream()
                    .map(gr -> groupRatingMapper.map(gr, level - 1))
                    .collect(Collectors.toList());
            bean.setGroupRatings(groupRatings);

            List<StudentBean> studentBeans = CollectionUtils.emptyIfNull(entity.getStudents())
                    .stream()
                    .map(studentMapper::map)
                    .collect(Collectors.toList());
            bean.setStudents(studentBeans);
        }
        return bean;
    }
}
