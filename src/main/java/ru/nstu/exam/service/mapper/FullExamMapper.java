package ru.nstu.exam.service.mapper;

import liquibase.repackaged.org.apache.commons.collections4.CollectionUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.AbstractPersistable;
import org.springframework.stereotype.Component;
import ru.nstu.exam.bean.ExamBean;
import ru.nstu.exam.bean.FullExamBean;
import ru.nstu.exam.entity.Exam;
import ru.nstu.exam.entity.ExamPeriod;
import ru.nstu.exam.entity.ExamRule;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class FullExamMapper implements Mapper<FullExamBean, Exam> {

    private final FullExamRuleMapper examRuleMapper;
    private final FullExamPeriodMapper examPeriodMapper;

    @Override
    public FullExamBean map(Exam entity, int level) {
        FullExamBean fullExamBean = new FullExamBean();
        if (level >= 0) {
            ExamBean examBean = new ExamBean();
            examBean.setId(entity.getId());
            examBean.setExamRuleId(entity.getExamRule() == null ? null : entity.getExamRule().getId());
            examBean.setDisciplineId(entity.getDiscipline() == null ? null : entity.getDiscipline().getId());
            examBean.setGroupIds(CollectionUtils.isEmpty(entity.getGroups()) ? null :
                    entity.getGroups().stream()
                            .map(AbstractPersistable::getId)
                            .collect(Collectors.toList()));
            fullExamBean.setExam(examBean);
        }
        if (level >= 1) {
            ExamRule examRule = entity.getExamRule();
            if (examRule != null) {
                fullExamBean.setExamRule(examRuleMapper.map(examRule, level - 1));
            }
            List<ExamPeriod> examPeriods = entity.getExamPeriods();
            if (CollectionUtils.isNotEmpty(examPeriods)) {
                fullExamBean.setPeriods(
                        examPeriods.stream()
                                .map(p -> examPeriodMapper.map(p, level - 1))
                                .collect(Collectors.toList())
                );
            }
        }
        return fullExamBean;
    }
}
