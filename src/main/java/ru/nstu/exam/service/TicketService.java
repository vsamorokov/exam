package ru.nstu.exam.service;

import liquibase.repackaged.org.apache.commons.collections4.CollectionUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.nstu.exam.bean.*;
import ru.nstu.exam.entity.*;
import ru.nstu.exam.entity.utils.RatingMapping;
import ru.nstu.exam.enums.ExamPeriodState;
import ru.nstu.exam.enums.TaskType;
import ru.nstu.exam.repository.TicketRepository;
import ru.nstu.exam.service.mapper.FullTicketMapper;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Comparator.comparingLong;
import static ru.nstu.exam.exception.ExamException.userError;
import static ru.nstu.exam.utils.Utils.checkNotNull;
import static ru.nstu.exam.utils.Utils.toMillis;

@Service
public class TicketService extends BasePersistentService<Ticket, TicketBean, TicketRepository> {
    private final AnswerService answerService;
    private final TaskService taskService;
    private final TeacherService teacherService;
    private final FullTicketMapper ticketMapper;

    public TicketService(TicketRepository repository, AnswerService answerService, TaskService taskService, TeacherService teacherService, FullTicketMapper ticketMapper) {
        super(repository);
        this.answerService = answerService;
        this.taskService = taskService;
        this.teacherService = teacherService;
        this.ticketMapper = ticketMapper;
    }

    public FullTicketBean findFull(Long ticketId, int level) {
        Ticket ticket = findById(ticketId);
        checkNotNull(ticket, "Ticket not found");
        return ticketMapper.map(ticket, level);
    }

    public List<TicketBean> findByPeriod(ExamPeriod period) {
        return mapToBeans(getRepository().findAllByExamPeriod(period));
    }

    public void deleteByPeriod(ExamPeriod examPeriod) {
        for (Ticket ticket : getRepository().findAllByExamPeriod(examPeriod)) {
            answerService.deleteByTicket(ticket);
            delete(ticket);
        }
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

    public void update(List<UpdateTicketBean> ticketBeans) {
        if (ticketBeans == null) {
            userError("Null list");
        }
        if (!ticketBeans.stream().allMatch(tb -> tb != null && tb.getId() != null)) {
            userError("Some tickets are null or their id are null");
        }

        ticketBeans.sort(comparingLong(UpdateTicketBean::getId));

        List<Ticket> tickets = getRepository().findAllById(
                ticketBeans.stream()
                        .map(UpdateTicketBean::getId)
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
            UpdateTicketBean ticketBean = ticketBeans.get(i);
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
                .filter(t -> exam.getExamRule().getMinimalRating() > t.getExamRating())
                .collect(Collectors.toList()));
    }

    public List<StudentTicketBean> getStudentTickets(Student student) {
        return getRepository().findAllByStudent(student).stream().map(this::mapToStudentBean).collect(Collectors.toList());
    }

    public List<StudentAnswerBean> getAnswers(Long ticketId, Pageable pageable) {
        Ticket ticket = findById(ticketId);
        if (ticket == null) {
            userError("No ticket found");
        }
        ExamPeriodState state = ticket.getExamPeriod().getState();
        if (state.isBefore(ExamPeriodState.PROGRESS)) {
            userError("Exam did not start yet");
        }
        return answerService.findByTicket(ticket, pageable);
    }

    @Override
    public void delete(Ticket ticket) {
        for (Answer answer : CollectionUtils.emptyIfNull(ticket.getAnswers())) {
            answerService.delete(answer);
        }
        super.delete(ticket);
    }

    @Override
    protected TicketBean map(Ticket entity) {
        TicketBean ticketBean = new TicketBean();
        ticketBean.setId(entity.getId());
        ticketBean.setAllowed(entity.getAllowed());
        ticketBean.setExamRating(entity.getExamRating());
        ticketBean.setSemesterRating(entity.getSemesterRating());
        ticketBean.setStudentId(entity.getStudent().getId());
        ticketBean.setExamPeriodId(entity.getExamPeriod().getId());
        return ticketBean;
    }

    @Override
    protected Ticket map(TicketBean bean) {
        return new Ticket();
    }

    private StudentTicketBean mapToStudentBean(Ticket ticket) {
        ExamPeriod examPeriod = ticket.getExamPeriod();
        Exam exam = examPeriod.getExam();
        Teacher teacher = exam.getTeacher();
        Discipline discipline = exam.getDiscipline();

        StudentTicketBean bean = new StudentTicketBean();

        bean.setId(ticket.getId());
        bean.setAllowed(ticket.getAllowed());
        bean.setExamRating(ticket.getExamRating());
        bean.setSemesterRating(ticket.getSemesterRating());

        ExamPeriodBean examPeriodBean = new ExamPeriodBean();
        examPeriodBean.setId(examPeriod.getId());
        examPeriodBean.setStart(toMillis(examPeriod.getStart()));
        examPeriodBean.setEnd(toMillis(examPeriod.getEnd()));
        examPeriodBean.setState(examPeriod.getState());
        examPeriodBean.setExamId(examPeriod.getExam().getId());
        bean.setExamPeriod(examPeriodBean);

        bean.setDisciplineName(discipline.getName());

        bean.setTeacher(teacherService.map(teacher));

        RatingSystem ratingSystem = ticket.getExamPeriod().getExam().getExamRule().getRatingSystem();
        Integer maxQuestionRating = ratingSystem.getRatingMappings().stream()
                .filter(rm -> rm.getTaskType() == TaskType.QUESTION)
                .max(Comparator.comparingInt(RatingMapping::getRating))
                .map(RatingMapping::getRating)
                .orElse(0);
        Integer maxExerciseRating = ratingSystem.getRatingMappings().stream()
                .filter(rm -> rm.getTaskType() == TaskType.EXERCISE)
                .max(Comparator.comparingInt(RatingMapping::getRating))
                .map(RatingMapping::getRating)
                .orElse(0);
        bean.setMaxQuestionRating(maxQuestionRating);
        bean.setMaxExerciseRating(maxExerciseRating);

        return bean;
    }
}
