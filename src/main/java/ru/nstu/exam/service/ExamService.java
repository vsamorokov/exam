package ru.nstu.exam.service;

import liquibase.repackaged.org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import ru.nstu.exam.bean.ExamBean;
import ru.nstu.exam.bean.ExamRuleBean;
import ru.nstu.exam.bean.GroupBean;
import ru.nstu.exam.bean.TicketBean;
import ru.nstu.exam.entity.*;
import ru.nstu.exam.repository.ExamPeriodRepository;
import ru.nstu.exam.repository.ExamRepository;
import ru.nstu.exam.security.UserRole;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static ru.nstu.exam.exception.ExamException.serverError;
import static ru.nstu.exam.exception.ExamException.userError;

@Service
public class ExamService extends BasePersistentService<Exam, ExamBean, ExamRepository> {
    private final ExamRuleService examRuleService;
    private final TeacherService teacherService;
    private final GroupService groupService;
    private final TicketService ticketService;
    private final ExamPeriodRepository examPeriodRepository;

    public ExamService(ExamRepository repository, ExamRuleService examRuleService, TeacherService teacherService, GroupService groupService, TicketService ticketService, ExamPeriodRepository examPeriodRepository) {
        super(repository);
        this.examRuleService = examRuleService;
        this.teacherService = teacherService;
        this.groupService = groupService;
        this.ticketService = ticketService;
        this.examPeriodRepository = examPeriodRepository;
    }

    public ExamBean createExam(ExamBean examBean, Account account) {
        if(!account.getRoles().contains(UserRole.ROLE_TEACHER)){
            serverError("Not teacher cannot create exam");
        }
        Teacher teacher = teacherService.findByAccount(account);
        if(teacher == null) {
            serverError("No teacher found");
        }
        ExamRuleBean examRuleBean = examBean.getExamRule();
        if(examRuleBean == null) {
            userError("Exam must have exam rule");
        }
        if(examRuleBean.getId() == null) {
            userError("Exam rule must have an id");
        }
        ExamRule examRule = examRuleService.findById(examRuleBean.getId());
        if(examRule == null) {
            userError("No exam rule with provided id");
        }
        List<GroupBean> groupBeans = examBean.getGroups();
        if(CollectionUtils.isEmpty(groupBeans)) {
            userError("Exam must have at least 1 group");
        }
        List<Group> groups = new ArrayList<>(groupBeans.size());
        for (GroupBean groupBean : groupBeans) {
            if(groupBean.getId() == null) {
                userError("Group must have an id");
            }
            Group group = groupService.findById(groupBean.getId());
            if(group == null) {
                userError("No group with id " + groupBean.getId());
            }
            groups.add(group);
        }
        if(examBean.getStartTime() == null) {
            userError("Empty start date");
        }

        Exam exam = map(examBean);
        exam.setExamRule(examRule);
        exam.setTeacher(teacher);
        exam.setGroups(groups);

        Exam saved = save(exam);

        createPeriod(saved, examBean.getStartTime(), examRule);

        return map(saved);
    }

    private void createPeriod(Exam exam, LocalDateTime start, ExamRule examRule) {
        ExamPeriod examPeriod = new ExamPeriod();
        examPeriod.setStart(start);
        examPeriod.setEnd(start.plusMinutes(examRule.getDuration()));
        examPeriod.setExam(exam);
        examPeriod = examPeriodRepository.save(examPeriod);
        ticketService.generateTickets(examRule, examPeriod, exam.getGroups());
    }

    public List<TicketBean> findUnPassed(Long examId) {
        Exam exam = findById(examId);
        if(exam == null) {
            userError("No exam found");
        }
        return ticketService.getUnPassed(exam);
    }

    @Override
    protected ExamBean map(Exam entity) {
        ExamBean examBean = new ExamBean();
        examBean.setId(entity.getId());
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
