package ru.nstu.exam.service.mapper;

import liquibase.repackaged.org.apache.commons.collections4.CollectionUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.nstu.exam.bean.FullTicketBean;
import ru.nstu.exam.bean.TicketBean;
import ru.nstu.exam.entity.Answer;
import ru.nstu.exam.entity.Student;
import ru.nstu.exam.entity.Ticket;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class FullTicketMapper implements Mapper<FullTicketBean, Ticket> {

    private final FullAnswerMapper answerMapper;
    private final FullStudentMapper studentMapper;

    @Override
    public FullTicketBean map(Ticket entity, int level) {
        FullTicketBean fullTicketBean = new FullTicketBean();

        if (level >= 0) {
            TicketBean ticketBean = new TicketBean();
            ticketBean.setId(entity.getId());
            ticketBean.setExamRating(entity.getExamRating());
            ticketBean.setAllowed(entity.getAllowed());
            ticketBean.setSemesterRating(entity.getSemesterRating());
            ticketBean.setStudentId(entity.getStudent() == null ? null : entity.getStudent().getId());
            ticketBean.setExamPeriodId(entity.getExamPeriod() == null ? null : entity.getExamPeriod().getId());
            fullTicketBean.setTicket(ticketBean);
        }
        if (level >= 1) {
            List<Answer> answers = entity.getAnswers();
            if (CollectionUtils.isNotEmpty(answers)) {
                fullTicketBean.setAnswers(
                        answers.stream()
                                .map(a -> answerMapper.map(a, level - 1))
                                .collect(Collectors.toList())
                );
            }
            Student student = entity.getStudent();
            if (student != null) {
                fullTicketBean.setStudent(studentMapper.map(student, level - 1));
            }
        }
        return fullTicketBean;
    }
}
