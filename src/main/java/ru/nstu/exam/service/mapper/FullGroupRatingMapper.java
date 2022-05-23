package ru.nstu.exam.service.mapper;

import liquibase.repackaged.org.apache.commons.collections4.CollectionUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.nstu.exam.bean.GroupRatingBean;
import ru.nstu.exam.bean.full.FullGroupRatingBean;
import ru.nstu.exam.bean.full.FullStudentRatingBean;
import ru.nstu.exam.entity.Discipline;
import ru.nstu.exam.entity.Group;
import ru.nstu.exam.entity.GroupRating;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class FullGroupRatingMapper implements Mapper<FullGroupRatingBean, GroupRating> {

    private final FullStudentRatingMapper studentRatingMapper;
    private final FullGroupMapper groupMapper;
    private final FullDisciplineMapper disciplineMapper;

    @Override
    public FullGroupRatingBean map(GroupRating entity, int level) {
        FullGroupRatingBean fullGroupRatingBean = new FullGroupRatingBean();
        if (level >= 0) {
            GroupRatingBean bean = new GroupRatingBean();
            bean.setId(entity.getId());
            bean.setName(entity.getName());
            bean.setGroupId(entity.getGroup().getId());
            bean.setDisciplineId(entity.getDiscipline().getId());
            bean.setExamRuleId(entity.getExamRule().getId());
            fullGroupRatingBean.setGroupRating(bean);
        }
        if (level >= 1) {
            List<FullStudentRatingBean> studentRatings = CollectionUtils.emptyIfNull(entity.getStudentRatings()).stream()
                    .map(sr -> studentRatingMapper.map(sr, level - 1))
                    .collect(Collectors.toList());
            fullGroupRatingBean.setStudentRatings(studentRatings);

            Group group = entity.getGroup();
            if (group != null) {
                fullGroupRatingBean.setGroup(groupMapper.map(group, level - 1));
            }

            Discipline discipline = entity.getDiscipline();
            if (discipline != null) {
                fullGroupRatingBean.setDiscipline(disciplineMapper.map(discipline, level - 1));
            }
        }
        return fullGroupRatingBean;
    }
}
