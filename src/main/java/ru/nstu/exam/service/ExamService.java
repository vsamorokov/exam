package ru.nstu.exam.service;

import liquibase.repackaged.org.apache.commons.collections4.CollectionUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.nstu.exam.bean.*;
import ru.nstu.exam.entity.*;
import ru.nstu.exam.enums.ExamPeriodState;
import ru.nstu.exam.exception.ExamException;
import ru.nstu.exam.repository.ExamPeriodRepository;
import ru.nstu.exam.repository.ExamRepository;
import ru.nstu.exam.security.UserRole;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static java.time.ZoneOffset.UTC;
import static ru.nstu.exam.exception.ExamException.serverError;
import static ru.nstu.exam.exception.ExamException.userError;
import static ru.nstu.exam.utils.Utils.toLocalDateTime;
import static ru.nstu.exam.utils.Utils.toMillis;

@Service
public class ExamService extends BasePersistentService<Exam, ExamBean, ExamRepository> {
    private final ExamRuleService examRuleService;
    private final TeacherService teacherService;
    private final GroupService groupService;
    private final DisciplineService disciplineService;
    private final TicketService ticketService;
    private final ExamPeriodRepository examPeriodRepository;
    private final MessageService messageService;

    public ExamService(ExamRepository repository, ExamRuleService examRuleService, TeacherService teacherService, GroupService groupService, DisciplineService disciplineService, TicketService ticketService, ExamPeriodRepository examPeriodRepository, MessageService messageService) {
        super(repository);
        this.examRuleService = examRuleService;
        this.teacherService = teacherService;
        this.groupService = groupService;
        this.disciplineService = disciplineService;
        this.ticketService = ticketService;
        this.examPeriodRepository = examPeriodRepository;
        this.messageService = messageService;
    }

    public List<ExamBean> findAll(Account account) {
        Teacher teacher = teacherService.findByAccount(account);
        if (teacher == null) {
            serverError("No teacher found");
        }
        return mapToBeans(getRepository().findAllByTeacher(teacher));
    }

    public ExamBean createExam(ExamBean examBean, Account account) {
        if (!account.getRoles().contains(UserRole.ROLE_TEACHER)) {
            serverError("Not teacher cannot create exam");
        }
        Teacher teacher = teacherService.findByAccount(account);
        if (teacher == null) {
            serverError("No teacher found");
        }
        DisciplineBean disciplineBean = examBean.getDiscipline();
        if (disciplineBean == null) {
            userError("Exam must have discpline");
        }
        if (disciplineBean.getId() == null) {
            userError("Discipline must have an id");
        }
        Discipline discipline = disciplineService.findById(disciplineBean.getId());
        if (discipline == null) {
            userError("No discipline with provided id");
        }
        ExamRuleBean examRuleBean = examBean.getExamRule();
        if (examRuleBean == null) {
            userError("Exam must have exam rule");
        }
        if (examRuleBean.getId() == null) {
            userError("Exam rule must have an id");
        }
        ExamRule examRule = examRuleService.findById(examRuleBean.getId());
        if (examRule == null) {
            userError("No exam rule with provided id");
        }
        List<GroupBean> groupBeans = examBean.getGroups();
        if (CollectionUtils.isEmpty(groupBeans)) {
            userError("Exam must have at least 1 group");
        }
        List<Group> groups = new ArrayList<>(groupBeans.size());
        for (GroupBean groupBean : groupBeans) {
            if (groupBean.getId() == null) {
                userError("Group must have an id");
            }
            Group group = groupService.findById(groupBean.getId());
            if (group == null) {
                userError("No group with id " + groupBean.getId());
            }
            groups.add(group);
        }
        if (examBean.getStartTime() == null) {
            userError("Empty start date");
        }

        Exam exam = map(examBean);
        exam.setDiscipline(discipline);
        exam.setExamRule(examRule);
        exam.setTeacher(teacher);
        exam.setGroups(groups);

        Exam saved = save(exam);

        try {
            createPeriod(saved, examBean.getStartTime(), examRule);
        } catch (ExamException e) {
            delete(saved);
            throw e;
        }

        return map(saved);
    }

    public ExamBean updateExam(Long examId, ExamBean examBean, Account account) {
        Exam exam = findById(examId);
        if (exam == null) {
            userError("No exam found");
        }
        if (!Objects.equals(exam.getTeacher().getAccount().getId(), account.getId())) {
            userError("Forbidden");
        }
        List<ExamPeriod> periods = examPeriodRepository.findAllByExam(exam);
        if (periods.size() != 1) {
            userError("Cannot modify exam that already had been closed");
        }
        if (!ExamPeriodState.REDACTION.equals(periods.get(0).getState())) {
            userError("Wrong state");
        }

        DisciplineBean disciplineBean = examBean.getDiscipline();
        if (disciplineBean != null) {
            if (disciplineBean.getId() == null) {
                userError("Discipline must have an id");
            }
            Discipline discipline = disciplineService.findById(disciplineBean.getId());
            if (discipline == null) {
                userError("No discipline with provided id");
            }
            exam.setDiscipline(discipline);
        }

        ExamRuleBean examRuleBean = examBean.getExamRule();
        if (examRuleBean != null) {
            if (examRuleBean.getId() == null) {
                userError("Exam rule must have an id");
            }
            ExamRule examRule = examRuleService.findById(examRuleBean.getId());
            if (examRule == null) {
                userError("No exam rule with provided id");
            }
            exam.setExamRule(examRule);
        }

        List<GroupBean> groupBeans = examBean.getGroups();
        if (!CollectionUtils.isEmpty(groupBeans)) {
            List<Group> groups = new ArrayList<>(groupBeans.size());
            for (GroupBean groupBean : groupBeans) {
                if (groupBean.getId() == null) {
                    userError("Group must have an id");
                }
                Group group = groupService.findById(groupBean.getId());
                if (group == null) {
                    userError("No group with id " + groupBean.getId());
                }
                groups.add(group);
            }
            exam.setGroups(groups);
        }

        Exam saved = save(exam);

        if (examBean.getStartTime() != null) {
            updatePeriod(saved, examBean.getStartTime(), saved.getExamRule());
        }

        return map(saved);
    }

    private void createPeriod(Exam exam, Long start, ExamRule examRule) {
        ExamPeriod examPeriod = new ExamPeriod();
        examPeriod.setStart(toLocalDateTime(start));
        examPeriod.setEnd(toLocalDateTime(start).plusMinutes(examRule.getDuration()));
        examPeriod.setExam(exam);
        examPeriod.setState(ExamPeriodState.REDACTION);
        examPeriod = examPeriodRepository.save(examPeriod);
        try {
            ticketService.generateTickets(examRule, examPeriod, exam.getGroups());
        } catch (Exception e) {
            examPeriod.setDeleted(true);
            examPeriodRepository.save(examPeriod);
            throw e;
        }
    }

    private void updatePeriod(Exam exam, Long start, ExamRule examRule) {
        List<ExamPeriod> periods = examPeriodRepository.findAllByExam(exam);
        if (periods.size() != 1) {
            userError("Cannot modify exam that already had been closed");
        }
        ExamPeriod period = periods.get(0);
        if (!ExamPeriodState.REDACTION.equals(period.getState())) {
            userError("Wrong state");
        }
        LocalDateTime startTime = toLocalDateTime(start);

        period.setStart(startTime);
        period.setEnd(startTime.plusMinutes(examRule.getDuration()));
        examPeriodRepository.save(period);
    }

    public void updatePeriod(Long examId, Long periodId, ExamPeriodBean bean) {
        Exam exam = this.findById(examId);
        if (exam == null) {
            userError("No exam found");
        }
        ExamPeriod period = examPeriodRepository.findById(periodId).orElseGet(() -> userError("No period found"));
        if (!Objects.equals(period.getExam().getId(), exam.getId())) {
            userError("Exam period is from another exam");
        }
        if (!ExamPeriodState.REDACTION.equals(period.getState())) {
            userError("Wrong state");
        }
        if (bean.getStart() == null) {
            userError("Start date must be not null");
        }
        LocalDateTime start = toLocalDateTime(bean.getStart());
        period.setStart(start);
        period.setEnd(start.plusMinutes(exam.getExamRule().getDuration()));
        examPeriodRepository.save(period);
    }

    public void updateState(Long examId, Long periodId, ExamPeriodBean bean) {
        Exam exam = this.findById(examId);
        if (exam == null) {
            userError("No exam found");
        }
        ExamPeriod period = examPeriodRepository.findById(periodId).orElseGet(() -> userError("No period found"));
        if (!Objects.equals(period.getExam().getId(), exam.getId())) {
            userError("Exam period is from another exam");
        }
        if (!bean.getState().isAllowed(period)) {
            userError("Wrong state");
        }
        period.setState(bean.getState());
        examPeriodRepository.save(period);
    }

    public List<TicketBean> findUnPassed(Long examId) {
        Exam exam = findById(examId);
        if (exam == null) {
            userError("No exam found");
        }
        return ticketService.getUnPassed(exam);
    }

    public List<ExamPeriodBean> findPeriods(Long examId) {
        Exam exam = findById(examId);
        if (exam == null) {
            userError("No exam found");
        }
        List<ExamPeriod> periods = examPeriodRepository.findAllByExam(exam);
        List<ExamPeriodBean> beans = new ArrayList<>(periods.size());
        for (ExamPeriod examPeriod : periods) {
            ExamPeriodBean examPeriodBean = new ExamPeriodBean();
            examPeriodBean.setId(examPeriod.getId());
            examPeriodBean.setStart(toMillis(examPeriod.getStart()));
            examPeriodBean.setEnd(toMillis(examPeriod.getEnd()));
            examPeriodBean.setState(examPeriod.getState());
            beans.add(examPeriodBean);
        }
        return beans;
    }

    public List<TicketBean> findTickets(Long periodId) {
        ExamPeriod period = examPeriodRepository.findById(periodId).orElseGet(() -> userError("No exam period found"));

        return ticketService.findByPeriod(period);
    }

    public Page<MessageBean> findAllMessages(Long periodId, Account account, Pageable pageable) {
        ExamPeriod examPeriod = examPeriodRepository.findById(periodId).orElseGet(() -> userError("No period found"));
        if (account.getRoles().contains(UserRole.ROLE_STUDENT)) {
            if (examPeriod.getTickets().stream()
                    .noneMatch(t ->
                            Objects.equals(account.getId(), t.getStudent().getAccount().getId())
                    )
            ) {
                userError("That student is not allowed to read there");
            }
        }
        if (account.getRoles().contains(UserRole.ROLE_TEACHER)) {
            if (!Objects.equals(examPeriod.getExam().getTeacher().getAccount().getId(), account.getId())) {
                userError("That teacher is not allowed to read there");
            }
        }
        return messageService.findAllByExamPeriod(examPeriod, pageable);
    }

    public MessageBean newMessage(Long periodId, MessageBean messageBean, Account account) {
        ExamPeriod examPeriod = examPeriodRepository.findById(periodId).orElseGet(() -> userError("No period found"));
        if (!ExamPeriodState.PROGRESS.equals(examPeriod.getState())) {
            userError("Wrong state");
        }
        if (account.getRoles().contains(UserRole.ROLE_STUDENT)) {
            if (examPeriod.getTickets().stream()
                    .noneMatch(t ->
                            Objects.equals(account.getId(), t.getStudent().getAccount().getId())
                    )
            ) {
                userError("That student is not allowed to write there");
            }
            return messageService.createExamPeriodMessage(messageBean, examPeriod, account);
        } else if (account.getRoles().contains(UserRole.ROLE_TEACHER)) {
            if (!Objects.equals(examPeriod.getExam().getTeacher().getAccount().getId(), account.getId())) {
                userError("That teacher is not allowed to write there");
            }
            return messageService.createExamPeriodMessage(messageBean, examPeriod, account);
        }
        return userError("Admin cannot write there");
    }

    public void updateExamStates() {
        List<ExamPeriod> readyPeriods = examPeriodRepository.findAllByStateIn(Collections.singleton(ExamPeriodState.READY));

        for (ExamPeriod readyPeriod : readyPeriods) {
            if (readyPeriod.getStart().isAfter(LocalDateTime.now(UTC))) {
                readyPeriod.setState(ExamPeriodState.PROGRESS);
                examPeriodRepository.save(readyPeriod);
            }
        }

        List<ExamPeriod> inProgressPeriods = examPeriodRepository.findAllByStateIn(Collections.singleton(ExamPeriodState.PROGRESS));

        for (ExamPeriod inProgressPeriod : inProgressPeriods) {
            if (inProgressPeriod.getEnd().isAfter(LocalDateTime.now(UTC))) {
                inProgressPeriod.setState(ExamPeriodState.FINISHED);
                examPeriodRepository.save(inProgressPeriod);
            }
        }
    }

    @Override
    protected ExamBean map(Exam entity) {
        ExamBean examBean = new ExamBean();
        examBean.setId(entity.getId());
        examBean.setDiscipline(disciplineService.map(entity.getDiscipline()));
        examBean.setExamRule(examRuleService.map(entity.getExamRule()));
        examBean.setTeacher(teacherService.map(entity.getTeacher()));
        examBean.setGroups(groupService.mapToBeans(entity.getGroups()));
        return examBean;
    }

    @Override
    protected Exam map(ExamBean bean) {
        return new Exam();
    }
}
