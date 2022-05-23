package ru.nstu.exam.service.mapper;

import liquibase.repackaged.org.apache.commons.collections4.CollectionUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import ru.nstu.exam.bean.full.FullStudentBean;
import ru.nstu.exam.bean.full.FullStudentRatingBean;
import ru.nstu.exam.entity.Group;
import ru.nstu.exam.entity.Student;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class FullStudentMapper implements Mapper<FullStudentBean, Student> {

    private final StudentMapper studentMapper;
    private final FullGroupMapper fullGroupMapper;
    @Lazy
    private final FullStudentRatingMapper studentRatingMapper;

    @Override
    public FullStudentBean map(Student entity, int level) {
        FullStudentBean fullStudentBean = new FullStudentBean();
        if (level >= 0) {
            fullStudentBean.setStudent(studentMapper.map(entity, level));
        }
        if (level >= 1) {
            Group group = entity.getGroup();
            if (group != null) {
                fullStudentBean.setGroup(fullGroupMapper.map(group));
            }
            List<FullStudentRatingBean> studentRatings = CollectionUtils.emptyIfNull(entity.getStudentRatings()).stream()
                    .map(sr -> studentRatingMapper.map(sr, level - 1))
                    .collect(Collectors.toList());
            fullStudentBean.setStudentRatings(studentRatings);
        }
        return fullStudentBean;
    }
}
