package ru.nstu.exam.service;

import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.nstu.exam.bean.AnswerBean;
import ru.nstu.exam.bean.EntityBean;
import ru.nstu.exam.bean.ExamPeriodBean;
import ru.nstu.exam.bean.TicketBean;
import ru.nstu.exam.entity.*;
import ru.nstu.exam.enums.ExamPeriodState;
import ru.nstu.exam.repository.TicketRepository;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Comparator.comparingLong;
import static ru.nstu.exam.exception.ExamException.userError;
import static ru.nstu.exam.utils.Utils.toMillis;

@Service
public class TicketService extends BasePersistentService<Ticket, TicketBean, TicketRepository> {
    private final AnswerService answerService;
    private final StudentService studentService;
    private final ExamService examService;
    private final TaskService taskService;

    public TicketService(TicketRepository repository, AnswerService answerService, @Lazy StudentService studentService, @Lazy ExamService examService, TaskService taskService) {
        super(repository);
        this.answerService = answerService;
        this.studentService = studentService;
        this.examService = examService;
        this.taskService = taskService;
    }

    public void generateTickets(ExamRule examRule, ExamPeriod examPeriod, List<Group> groups) {
        List<Task> questions = taskService.getQuestions(examRule);
        List<Task> exercises = taskService.getExercises(examRule);
        if (questions.size() < examRule.getQuestionCount()) {
            userError("Not enough questions to create a ticket");
        }
        if (exercises.size() < examRule.getExerciseCount()) {
            userError("Not enough exercises to create a ticket");
        }
        for (Group group : groups) {
            for (Student student : group.getStudents()) {
                generateTicket(examRule, examPeriod, student, questions, exercises);
            }
        }
    }

    public void generateTicket(ExamRule examRule, ExamPeriod examPeriod, Student student, List<Task> questions, List<Task> exercises) {
        Ticket ticket = new Ticket();
        ticket.setAllowed(false);
        ticket.setSemesterRating(0);
        ticket.setExamRating(0);
        ticket.setStudent(student);
        ticket.setExamPeriod(examPeriod);
        ticket = save(ticket);
        try {
            answerService.generateAnswers(ticket, examRule, questions, exercises);
        } catch (Exception e) {
            delete(ticket);
            throw e;
        }
    }

    public void update(List<TicketBean> ticketBeans) {
        if (ticketBeans == null) {
            userError("Null list");
        }
        if (!ticketBeans.stream().allMatch(tb -> tb != null && tb.getId() != null)) {
            userError("Some tickets are null or their id are null");
        }

        ticketBeans.sort(comparingLong(EntityBean::getId));

        List<Ticket> tickets = getRepository().findAllById(
                ticketBeans.stream()
                        .map(EntityBean::getId)
                        .collect(Collectors.toList())
        );

        if (tickets.size() != ticketBeans.size()) {
            userError("Some tickets not found");
        }
        if (!tickets.stream().allMatch(t -> ExamPeriodState.ALLOWANCE.equals(t.getExamPeriod().getState()))) {
            userError("Wrong state");
        }
        tickets.sort(comparingLong(Ticket::getId));

        for (int i = 0; i < ticketBeans.size(); i++) {
            TicketBean ticketBean = ticketBeans.get(i);
            Ticket ticket = tickets.get(i);

            ticket.setSemesterRating(ticketBean.getSemesterRating());
            ticket.setExamRating(ticketBean.getSemesterRating());
            ticket.setAllowed(ticketBean.getAllowed());
        }
        getRepository().saveAllAndFlush(tickets);
    }

    public List<TicketBean> getUnPassed(Exam exam) {
        return mapToBeans(exam.getExamPeriods().stream()
                .map(ExamPeriod::getTickets)
                .flatMap(Collection::stream)
                .filter(t -> exam.getExamRule().getMinimalRating() > (t.getExamRating()))
                .collect(Collectors.toList()));
    }

    public List<TicketBean> getStudentTickets(Student student) {
        List<Ticket> tickets = getRepository().findAllByStudent(student);
        return mapToBeans(tickets);
    }

    public List<AnswerBean> getAnswers(Long ticketId, Pageable pageable) {
        Ticket ticket = findById(ticketId);
        if (ticket == null) {
            userError("No ticket found");
        }
        ExamPeriodState state = ticket.getExamPeriod().getState();
        if (!state.isAfter(ExamPeriodState.READY)) {
            userError("Exam did not start yet");
        }
        return answerService.findByTicket(ticket, pageable);
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
        examPeriodBean.setStart(toMillis(entity.getExamPeriod().getStart()));
        examPeriodBean.setEnd(toMillis(entity.getExamPeriod().getEnd()));
        examPeriodBean.setExam(examService.map(entity.getExamPeriod().getExam()));

        ticketBean.setExamPeriod(examPeriodBean);
        return ticketBean;
    }

    @Override
    protected Ticket map(TicketBean bean) {
        return new Ticket();
    }

    public List<TicketBean> findByPeriod(ExamPeriod period) {
        return mapToBeans(getRepository().findAllByExamPeriod(period));
    }
}
