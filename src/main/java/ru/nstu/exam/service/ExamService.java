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
import java.util.stream.Collectors;

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

    public ExamBean findOne(Long examId) {
        Exam exam = findById(examId);
        if (exam == null) {
            userError("Exam not found");
        }
        return map(exam);
    }

    public ExamBean createExam(CreateExamBean examBean, Account account) {
        if (!account.getRoles().contains(UserRole.ROLE_TEACHER)) {
            serverError("Not teacher cannot create exam");
        }
        Teacher teacher = teacherService.findByAccount(account);
        if (teacher == null) {
            serverError("No teacher found");
        }
        Discipline discipline = disciplineService.findById(examBean.getDisciplineId());
        if (discipline == null) {
            userError("No discipline with provided id");
        }
        ExamRule examRule = examRuleService.findById(examBean.getExamRuleId());
        if (examRule == null) {
            userError("No exam rule with provided id");
        }
        List<Long> groupIds = examBean.getGroupIds();
        if (CollectionUtils.isEmpty(groupIds)) {
            userError("Exam must have at least 1 group");
        }
        List<Group> groups = new ArrayList<>(groupIds.size());
        for (Long groupId : groupIds) {
            Group group = groupService.findById(groupId);
            if (group == null) {
                userError("No group with id " + groupId);
            }
            groups.add(group);
        }
        if (examBean.getStartTime() == null) {
            userError("Empty start date");
        }

        Exam exam = new Exam();
        exam.setDiscipline(discipline);
        exam.setExamRule(examRule);
        exam.setTeacher(teacher);
        exam.setGroups(groups);

        Exam saved = save(exam);

        try {
            createPeriodInternal(saved, examBean.getStartTime(), examRule);
        } catch (ExamException e) {
            delete(saved);
            throw e;
        }

        return map(saved);
    }

    public ExamBean updateExam(Long examId, CreateExamBean examBean, Account account) {
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

        Long disciplineId = examBean.getDisciplineId();
        if (disciplineId != null) {
            Discipline discipline = disciplineService.findById(disciplineId);
            if (discipline == null) {
                userError("No discipline with provided id");
            }
            exam.setDiscipline(discipline);
        }

        Long examRuleId = examBean.getExamRuleId();
        if (examRuleId != null) {
            ExamRule examRule = examRuleService.findById(examRuleId);
            if (examRule == null) {
                userError("No exam rule with provided id");
            }
            exam.setExamRule(examRule);
        }

        List<Long> groupIds = examBean.getGroupIds();
        if (!CollectionUtils.isEmpty(groupIds)) {
            List<Group> groups = new ArrayList<>(groupIds.size());
            for (Long groupId : groupIds) {
                Group group = groupService.findById(groupId);
                if (group == null) {
                    userError("No group with id " + groupId);
                }
                groups.add(group);
            }
            exam.setGroups(groups);
        }

        if (examBean.getStartTime() != null) {
            updatePeriodInternal(exam, examBean.getStartTime(), exam.getExamRule());
        }

        return map(save(exam));
    }

    public void deleteExam(Long examId, Account account) {
        Exam exam = findById(examId);
        if (exam == null) {
            userError("Exam not found");
        }
        if (!Objects.equals(exam.getTeacher().getAccount().getId(), account.getId())) {
            userError("That teacher cannot do this");
        }

        List<ExamPeriod> periods = exam.getExamPeriods();
        if (periods.stream().anyMatch(p -> ExamPeriodState.PROGRESS.equals(p.getState()))) {
            userError("Exam in progress");
        }
        for (ExamPeriod examPeriod : periods) {
            ticketService.deleteByPeriod(examPeriod);
            examPeriod.setDeleted(true);
            examPeriodRepository.save(examPeriod);
        }
        delete(exam);
    }

    private void createPeriodInternal(Exam exam, Long start, ExamRule examRule) {
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

    private void updatePeriodInternal(Exam exam, Long start, ExamRule examRule) {
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

    public ExamPeriodBean updatePeriod(Long periodId, UpdateExamPeriodBean bean) {
        ExamPeriod period = examPeriodRepository.findById(periodId).orElseGet(() -> userError("No period found"));
        if (bean.getStart() != null && bean.getState() == null) {
            if (!ExamPeriodState.REDACTION.equals(period.getState())) {
                userError("Wrong state");
            }
            Exam exam = period.getExam();
            LocalDateTime start = toLocalDateTime(bean.getStart());
            period.setStart(start);
            period.setEnd(start.plusMinutes(exam.getExamRule().getDuration()));
            ExamPeriod saved = examPeriodRepository.save(period);
            return map(saved);
        }
        if (bean.getStart() == null && bean.getState() != null) {
            if (!bean.getState().isAllowed(period)) {
                userError("Wrong state");
            }
            period.setState(bean.getState());
            ExamPeriod saved = examPeriodRepository.save(period);
            return map(saved);
        }
        return userError("Wrong parameters");
    }

    public List<TicketBean> findUnPassed(Long examId) {
        Exam exam = findById(examId);
        if (exam == null) {
            userError("No exam found");
        }
        return ticketService.getUnPassed(exam);
    }


    public ExamPeriodBean getPeriod(Long periodId, Account account) {
        ExamPeriod period = examPeriodRepository.findById(periodId).orElseGet(() -> userError("Period not found"));

        if (account.getRoles().contains(UserRole.ROLE_ADMIN)) {
            if (!Objects.equals(period.getExam().getTeacher().getAccount().getId(), account.getId())) {
                userError("Teacher not allowed to this exam");
            }
            return map(period);
        } else if (account.getRoles().contains(UserRole.ROLE_STUDENT)) {
            if (!period.getState().isAfter(ExamPeriodState.ALLOWANCE) ||
                    period.getTickets().stream()
                            .noneMatch(t -> t.getAllowed() && Objects.equals(t.getStudent().getAccount().getId(), account.getId()))) {
                userError("Student not allowed to this exam");
            }
            return map(period);
        }
        return userError("Not allowed to this exam");
    }

    public List<ExamPeriodBean> findPeriods(Long examId) {
        Exam exam = findById(examId);
        if (exam == null) {
            userError("No exam found");
        }
        List<ExamPeriod> periods = examPeriodRepository.findAllByExam(exam);
        List<ExamPeriodBean> beans = new ArrayList<>(periods.size());
        for (ExamPeriod examPeriod : periods) {
            ExamPeriodBean examPeriodBean = map(examPeriod);
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

    public MessageBean newMessage(Long periodId, NewMessageBean messageBean, Account account) {
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
        examBean.setDisciplineId(entity.getDiscipline() == null ? null : entity.getDiscipline().getId());
        examBean.setExamRuleId(entity.getExamRule() == null ? null : entity.getExamRule().getId());
        examBean.setGroupIds(entity.getGroups().stream().map(Group::getId).collect(Collectors.toList()));
        return examBean;
    }

    @Override
    protected Exam map(ExamBean bean) {
        return new Exam();
    }

    protected ExamPeriodBean map(ExamPeriod entity) {
        ExamPeriodBean bean = new ExamPeriodBean();
        bean.setId(entity.getId());
        bean.setExamId(entity.getExam().getId());
        bean.setStart(toMillis(entity.getStart()));
        bean.setEnd(toMillis(entity.getEnd()));
        bean.setState(entity.getState());
        return bean;
    }

}
