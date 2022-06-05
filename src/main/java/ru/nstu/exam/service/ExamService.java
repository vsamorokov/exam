package ru.nstu.exam.service;

import org.springframework.stereotype.Service;
import ru.nstu.exam.bean.AnswerBean;
import ru.nstu.exam.bean.ExamBean;
import ru.nstu.exam.bean.StudentRatingBean;
import ru.nstu.exam.bean.full.FullExamBean;
import ru.nstu.exam.entity.*;
import ru.nstu.exam.enums.ExamState;
import ru.nstu.exam.enums.StudentRatingState;
import ru.nstu.exam.notification.NotificationService;
import ru.nstu.exam.repository.ExamRepository;
import ru.nstu.exam.service.mapper.FullExamMapper;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static java.time.ZoneOffset.UTC;
import static ru.nstu.exam.enums.ExamState.*;
import static ru.nstu.exam.utils.Utils.*;

@Service
public class ExamService extends BasePersistentService<Exam, ExamBean, ExamRepository> {
    private final GroupService groupService;
    private final DisciplineService disciplineService;
    private final StudentRatingService studentRatingService;
    private final FullExamMapper fullExamMapper;
    private final List<NotificationService> notificationServices;
    private final ReportService reportService;
    private final GroupRatingService groupRatingService;
    private final TeacherService teacherService;

    public ExamService(ExamRepository repository, GroupService groupService, DisciplineService disciplineService, StudentRatingService studentRatingService, FullExamMapper fullExamMapper, List<NotificationService> notificationServices, ReportService reportService, GroupRatingService groupRatingService, TeacherService teacherService) {
        super(repository);
        this.groupService = groupService;
        this.disciplineService = disciplineService;
        this.studentRatingService = studentRatingService;
        this.fullExamMapper = fullExamMapper;
        this.notificationServices = notificationServices;
        this.reportService = reportService;
        this.groupRatingService = groupRatingService;
        this.teacherService = teacherService;
    }

    public List<ExamBean> findAll(Account account) {
        Teacher teacher = teacherService.findByAccount(account);
        checkNotNull(teacher, "Teacher not found");
        return mapToBeans(getRepository().findAllByTeacher(teacher));
    }

    public FullExamBean findFull(Long examId, int level) {
        Exam exam = findById(examId);
        checkNotNull(exam, "Exam not found");
        return fullExamMapper.map(exam, level);
    }

    public ExamBean findOne(Long examId) {
        Exam exam = findById(examId);
        checkNotNull(exam, "Exam not found");
        return map(exam);
    }

    public ExamBean createExam(ExamBean examBean, Account account) {
        Exam exam = new Exam();
        Teacher teacher = teacherService.findByAccount(account);
        checkNotNull(teacher, "Teacher not found");
        exam.setTeacher(teacher);
        fillExam(exam, examBean);
        exam.setState(REDACTION);
        Exam saved = save(exam);

        studentRatingService.examCreated(saved);
        notificationServices.forEach(s -> s.examCreated(saved));
        return map(saved);
    }

    public ExamBean updateExam(ExamBean examBean, Account account) {
        Exam exam = findById(examBean.getId());
        checkNotNull(exam, "Exam with id " + examBean.getId() + " not found");
        Teacher teacher = teacherService.findByAccount(account);
        checkTrue(Objects.equals(teacher == null ? null : teacher.getId(), exam.getTeacher().getId()), "Wrong teacher");

        checkTrue(ExamState.REDACTION.equals(exam.getState()), "Wrong state");

        fillExam(exam, examBean);

        return map(save(exam));
    }

    private void fillExam(Exam exam, ExamBean bean) {

        exam.setName(bean.getName());

        Discipline discipline = disciplineService.findById(bean.getDisciplineId());
        checkNotNull(discipline, "No discipline with provided id");
        exam.setDiscipline(discipline);

        if (bean.isOneGroup()) {
            Group group = groupService.findById(bean.getGroupId());
            checkNotNull(group, "Group with id " + bean.getGroupId() + " not found");

            GroupRating groupRating = groupRatingService.find(discipline, group);
            checkNotNull(groupRating, "Group rating for group " + group.getName() + " and discipline " + discipline.getName() + "not found");

            List<StudentRating> studentRatings = groupRating.getStudentRatings().stream()
                    .filter(r -> r.getStudentRatingState().equals(StudentRatingState.ALLOWED))
                    .collect(Collectors.toList());

            exam.setOneGroup(true);
            exam.setGroup(group);
            exam.setStudentRatings(studentRatings);
        } else {
            List<StudentRating> ratings = discipline.getGroupRatings().stream()
                    .map(GroupRating::getStudentRatings)
                    .flatMap(Collection::stream)
                    .filter(r -> StudentRatingState.ALLOWED.equals(r.getStudentRatingState()))
                    .collect(Collectors.toList());

            exam.setOneGroup(false);
            exam.setGroup(null);
            exam.setStudentRatings(ratings);
        }
    }

    public ExamBean updateState(ExamBean examBean) {
        Exam exam = findById(examBean.getId());
        checkNotNull(exam, "Exam not found");

        ExamState newState = examBean.getState();
        checkNotNull(newState, "State cannot be null");
        if (newState.equals(exam.getState())) {
            return map(exam);
        }

        if (TIME_SET.equals(newState)) {
            return map(setTime(exam, examBean));
        }

        if (PROGRESS.equals(newState)) {
            return map(startExam(exam));
        }

        if (FINISHED.equals(newState)) {
            return map(finishExam(exam));
        }

        if (CLOSED.equals(newState)) {
            return map(closeExam(exam));
        }

        checkTrue(newState.isAllowedFor(exam), "Wrong state");
        exam.setState(newState);

        Exam saved = save(exam);

        if (READY.equals(newState)) {
            notificationServices.forEach(s -> s.examReady(exam));
        }

        return map(saved);
    }

    private Exam setTime(Exam exam, ExamBean examBean) {
        checkTrue(TIME_SET.isAllowedFor(exam), "Wrong state");
        Long start = examBean.getStart();
        checkNotNull(start, "Exam start cannot be null");
        exam.setStart(toLocalDateTime(start));
        exam.setState(TIME_SET);
        Long end = examBean.getEnd();
        if (end != null) {
            checkTrue(toLocalDateTime(start).isAfter(toLocalDateTime(end)), "End must be after start");
            exam.setEnd(toLocalDateTime(end));
        } else { // From duration
            if (exam.isOneGroup()) {
                GroupRating groupRating = groupRatingService.find(exam.getDiscipline(), exam.getGroup());
                Integer duration = groupRating.getExamRule().getDuration();
                exam.setEnd(toLocalDateTime(start).plusMinutes(duration));
            } else {
                Integer duration = exam.getStudentRatings().stream()
                        .map(StudentRating::getGroupRating)
                        .map(GroupRating::getExamRule)
                        .map(ExamRule::getDuration)
                        .max(Comparator.comparingInt(o -> o))
                        .orElse(0);
                exam.setEnd(toLocalDateTime(start).plusMinutes(duration));
            }
        }
        Exam saved = save(exam);
        notificationServices.forEach(s -> s.examTimeSet(saved));

        return saved;
    }

    private Exam startExam(Exam exam) {
        checkTrue(PROGRESS.isAllowedFor(exam), "Wrong state");
        exam.setState(PROGRESS);

        Exam saved = save(exam);

        studentRatingService.examStarted(saved);

        notificationServices.forEach(s -> s.examStarted(saved));
        return saved;
    }

    private Exam finishExam(Exam exam) {
        checkTrue(FINISHED.isAllowedFor(exam), "Wrong state");
        exam.setState(FINISHED);
        Exam saved = save(exam);

        studentRatingService.examFinished(saved);

        notificationServices.forEach(s -> s.examFinished(saved));
        return saved;
    }

    private Exam closeExam(Exam exam) {
        checkTrue(CLOSED.isAllowedFor(exam), "Wrong state");
        exam.setState(CLOSED);

        Exam saved = save(exam);

        reportService.generateReport(saved);
        notificationServices.forEach(s -> s.examClosed(saved));
        return saved;
    }

    public void delete(Long examId) {
        Exam exam = findById(examId);
        checkNotNull(exam, "Exam not found");
        delete(exam);
    }

    @Override
    public void delete(Exam exam) {
        checkTrue(exam.getState().equals(CLOSED) || exam.getState().isBefore(PROGRESS), "Wrong exam state");
        studentRatingService.examDeleted(exam);
        super.delete(exam);
    }

    @Override
    protected ExamBean map(Exam entity) {
        ExamBean examBean = new ExamBean();
        examBean.setId(entity.getId());
        examBean.setStart(toMillis(entity.getStart()));
        examBean.setEnd(toMillis(entity.getEnd()));
        examBean.setState(entity.getState());
        examBean.setDisciplineId(entity.getDiscipline().getId());
        examBean.setName(entity.getName());
        examBean.setGroupId(entity.getGroup() == null ? null : entity.getGroup().getId());
        examBean.setOneGroup(entity.isOneGroup());
        examBean.setTeacherId(entity.getTeacher().getId());
        return examBean;
    }

    public List<StudentRatingBean> findRatings(Long examId) {
        Exam exam = findById(examId);
        checkNotNull(exam, "Exam not found");

        return studentRatingService.findByExam(exam);
    }

    public void updateExamStates() {
        List<Exam> readyExams = getRepository().findAllByStateIn(Collections.singleton(TIME_SET));

        for (Exam readyExam : readyExams) {
            if (readyExam.getStart().isAfter(LocalDateTime.now(UTC))) {
                startExam(readyExam);
            }
        }

        List<Exam> progressExams = getRepository().findAllByStateIn(Collections.singleton(PROGRESS));

        for (Exam progressExam : progressExams) {
            if (progressExam.getEnd().isAfter(LocalDateTime.now(UTC))) {
                progressExam.setState(FINISHED);
                Exam saved = save(progressExam);
                notificationServices.forEach(s -> s.examFinished(saved));
            }
        }
    }

    public List<AnswerBean> findAnswers(Long examId) {
        Exam exam = findById(examId);
        checkNotNull(exam, String.format("Exam with id %s not found", examId));
        return exam.getStudentRatings().stream()
                .map(studentRatingService::getAnswers)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }
}
