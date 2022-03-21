package ru.nstu.exam.service;

import liquibase.repackaged.org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import ru.nstu.exam.bean.*;
import ru.nstu.exam.entity.*;
import ru.nstu.exam.enums.ExamPeriodState;
import ru.nstu.exam.repository.ExamPeriodRepository;
import ru.nstu.exam.repository.ExamRepository;
import ru.nstu.exam.security.UserRole;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static ru.nstu.exam.exception.ExamException.serverError;
import static ru.nstu.exam.exception.ExamException.userError;

@Service
public class ExamService extends BasePersistentService<Exam, ExamBean, ExamRepository> {
    private final ExamRuleService examRuleService;
    private final TeacherService teacherService;
    private final GroupService groupService;
    private final DisciplineService disciplineService;
    private final TicketService ticketService;
    private final ExamPeriodRepository examPeriodRepository;

    public ExamService(ExamRepository repository, ExamRuleService examRuleService, TeacherService teacherService, GroupService groupService, DisciplineService disciplineService, TicketService ticketService, ExamPeriodRepository examPeriodRepository) {
        super(repository);
        this.examRuleService = examRuleService;
        this.teacherService = teacherService;
        this.groupService = groupService;
        this.disciplineService = disciplineService;
        this.ticketService = ticketService;
        this.examPeriodRepository = examPeriodRepository;
    }

    public List<ExamBean> findAll(Account account) {
        if (!account.getRoles().contains(UserRole.ROLE_TEACHER)) {
            userError("Only teachers can get exams");
        }
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

        createPeriod(saved, examBean.getStartTime(), examRule);

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

    private void createPeriod(Exam exam, LocalDateTime start, ExamRule examRule) {
        ExamPeriod examPeriod = new ExamPeriod();
        examPeriod.setStart(start);
        examPeriod.setEnd(start.plusMinutes(examRule.getDuration()));
        examPeriod.setExam(exam);
        examPeriod.setState(ExamPeriodState.REDACTION);
        examPeriod = examPeriodRepository.save(examPeriod);
        ticketService.generateTickets(examRule, examPeriod, exam.getGroups());
    }

    private void updatePeriod(Exam exam, LocalDateTime start, ExamRule examRule) {
        List<ExamPeriod> periods = examPeriodRepository.findAllByExam(exam);
        if (periods.size() != 1) {
            userError("Cannot modify exam that already had been closed");
        }
        ExamPeriod period = periods.get(0);
        if (!ExamPeriodState.REDACTION.equals(period.getState())) {
            userError("Wrong state");
        }
        period.setStart(start);
        period.setEnd(start.plusMinutes(examRule.getDuration()));
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
        if (bean.getStart() != null) {
            userError("Start date must be not null");
        }
        period.setStart(bean.getStart());
        period.setEnd(bean.getStart().plusMinutes(exam.getExamRule().getDuration()));
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
            examPeriodBean.setStart(examPeriod.getStart());
            examPeriodBean.setEnd(examPeriod.getEnd());
            beans.add(examPeriodBean);
        }
        return beans;
    }

    public List<TicketBean> findTickets(Long periodId) {
        ExamPeriod period = examPeriodRepository.findById(periodId).orElseGet(() -> userError("No exam period found"));

        return ticketService.findByPeriod(period);
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
