package ru.nstu.exam.service;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import ru.nstu.exam.bean.ExamPeriodBean;
import ru.nstu.exam.bean.TicketBean;
import ru.nstu.exam.entity.*;
import ru.nstu.exam.repository.TicketRepository;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TicketService extends BasePersistentService<Ticket, TicketBean, TicketRepository> {
    private final AnswerService answerService;
    private final StudentService studentService;
    private final ExamService examService;

    public TicketService(TicketRepository repository, AnswerService answerService, @Lazy StudentService studentService, @Lazy ExamService examService) {
        super(repository);
        this.answerService = answerService;
        this.studentService = studentService;
        this.examService = examService;
    }

    public void generateTickets(ExamRule examRule, ExamPeriod examPeriod, List<Group> groups) {
        for (Group group : groups) {
            for (Student student : group.getStudents()) {
                generateTicket(examRule, examPeriod, student);
            }
        }
    }

    public void generateTicket(ExamRule examRule, ExamPeriod examPeriod, Student student) {
        Ticket ticket = new Ticket();
        ticket.setAllowed(false);
        ticket.setSemesterRating(0);
        ticket.setExamRating(0);
        ticket.setStudent(student);
        ticket.setExamPeriod(examPeriod);
        ticket = save(ticket);
        answerService.generateAnswers(ticket, examRule.getThemes());
    }

    public List<TicketBean> getUnPassed(Exam exam) {
        return mapToBeans(exam.getExamPeriods().stream()
                .map(ExamPeriod::getTickets)
                .flatMap(Collection::stream)
                .filter(t -> new Integer(0).equals(t.getExamRating()))
                .collect(Collectors.toList()));
    }

    public List<TicketBean> getStudentTickets(Student student) {
        List<Ticket> tickets = getRepository().findAllByStudent(student);
        return mapToBeans(tickets);
    }

    @Override
    protected TicketBean map(Ticket entity) {
        TicketBean ticketBean = new TicketBean();
        ticketBean.setId(entity.getId());
        ticketBean.setAllowed(entity.getAllowed());
        ticketBean.setExamRating(entity.getExamRating());
        ticketBean.setSemesterRating(entity.getSemesterRating());
        ticketBean.setStudent(studentService.map(entity.getStudent()));

        ExamPeriodBean examPeriodBean = new ExamPeriodBean();
        examPeriodBean.setId(entity.getExamPeriod().getId());
        examPeriodBean.setStart(entity.getExamPeriod().getStart());
        examPeriodBean.setEnd(entity.getExamPeriod().getEnd());
        examPeriodBean.setExam(examService.map(entity.getExamPeriod().getExam()));

        ticketBean.setExamPeriod(examPeriodBean);
        return ticketBean;
    }

    @Override
    protected Ticket map(TicketBean bean) {
        return new Ticket();
    }

}
