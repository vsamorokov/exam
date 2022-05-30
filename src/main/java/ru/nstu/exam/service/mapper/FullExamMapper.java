package ru.nstu.exam.service.mapper;

import liquibase.repackaged.org.apache.commons.collections4.CollectionUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.nstu.exam.bean.ExamBean;
import ru.nstu.exam.bean.full.FullExamBean;
import ru.nstu.exam.bean.full.FullStudentRatingBean;
import ru.nstu.exam.entity.Exam;
import ru.nstu.exam.entity.Group;

import java.util.List;
import java.util.stream.Collectors;

import static ru.nstu.exam.utils.Utils.toMillis;

@Component
@RequiredArgsConstructor
public class FullExamMapper implements Mapper<FullExamBean, Exam> {

    private final FullGroupMapper groupMapper;
    private final FullStudentRatingMapper studentRatingMapper;

    @Override
    public FullExamBean map(Exam entity, int level) {
        FullExamBean fullExamBean = new FullExamBean();
        if (level >= 0) {
            ExamBean examBean = new ExamBean();
            examBean.setId(entity.getId());
            examBean.setName(entity.getName());
            examBean.setStart(toMillis(entity.getStart()));
            examBean.setEnd(toMillis(entity.getEnd()));
            examBean.setState(entity.getState());
            examBean.setOneGroup(entity.isOneGroup());
            examBean.setTeacherId(entity.getTeacher().getId());
            examBean.setDisciplineId(entity.getDiscipline() == null ? null : entity.getDiscipline().getId());
            examBean.setGroupId(entity.getGroup() == null ? null : entity.getGroup().getId());
            fullExamBean.setExam(examBean);
        }
        if (level >= 1) {
            Group group = entity.getGroup();
            if (group != null) {
                fullExamBean.setGroup(groupMapper.map(group, level - 1));
            }

            List<FullStudentRatingBean> studentRatings = CollectionUtils.emptyIfNull(entity.getStudentRatings()).stream()
                    .map(ticket -> studentRatingMapper.map(ticket, level - 1))
                    .collect(Collectors.toList());
            fullExamBean.setTickets(studentRatings);
        }
        return fullExamBean;
    }
}
