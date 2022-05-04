package ru.nstu.exam.service.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.nstu.exam.bean.FullStudentBean;
import ru.nstu.exam.entity.Group;
import ru.nstu.exam.entity.Student;

@Component
@RequiredArgsConstructor
public class FullStudentMapper implements Mapper<FullStudentBean, Student> {

    private final StudentMapper studentMapper;
    private final FullGroupMapper fullGroupMapper;

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
        }
        return fullStudentBean;
    }
}
