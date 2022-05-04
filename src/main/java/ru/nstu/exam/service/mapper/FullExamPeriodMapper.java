package ru.nstu.exam.service.mapper;

import liquibase.repackaged.org.apache.commons.collections4.CollectionUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.nstu.exam.bean.ExamPeriodBean;
import ru.nstu.exam.bean.FullExamPeriodBean;
import ru.nstu.exam.entity.ExamPeriod;
import ru.nstu.exam.entity.Ticket;

import java.util.List;
import java.util.stream.Collectors;

import static ru.nstu.exam.utils.Utils.toMillis;

@Component
@RequiredArgsConstructor
public class FullExamPeriodMapper implements Mapper<FullExamPeriodBean, ExamPeriod> {

    private final FullTicketMapper ticketMapper;

    @Override
    public FullExamPeriodBean map(ExamPeriod entity, int level) {

        FullExamPeriodBean fullExamPeriodBean = new FullExamPeriodBean();

        if (level >= 0) {
            ExamPeriodBean examPeriodBean = new ExamPeriodBean();
            examPeriodBean.setId(entity.getId());
            examPeriodBean.setStart(toMillis(entity.getStart()));
            examPeriodBean.setEnd(toMillis(entity.getEnd()));
            examPeriodBean.setExamId(entity.getExam() == null ? null : entity.getExam().getId());
            examPeriodBean.setState(entity.getState());
            fullExamPeriodBean.setExamPeriod(examPeriodBean);
        }
        if (level >= 1) {
            List<Ticket> tickets = entity.getTickets();
            if (CollectionUtils.isNotEmpty(tickets)) {
                fullExamPeriodBean.setTickets(
                        tickets.stream()
                                .map(t -> ticketMapper.map(t, level - 1))
                                .collect(Collectors.toList())
                );
            }
        }
        return fullExamPeriodBean;
    }
}