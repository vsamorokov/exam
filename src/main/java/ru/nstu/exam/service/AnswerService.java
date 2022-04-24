package ru.nstu.exam.service;

import liquibase.repackaged.org.apache.commons.collections4.CollectionUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.nstu.exam.bean.*;
import ru.nstu.exam.entity.*;
import ru.nstu.exam.entity.utils.RatingMapping;
import ru.nstu.exam.enums.AnswerStatus;
import ru.nstu.exam.enums.ExamPeriodState;
import ru.nstu.exam.enums.TaskType;
import ru.nstu.exam.repository.AnswerRepository;
import ru.nstu.exam.security.UserRole;

import java.util.*;
import java.util.stream.Collectors;

import static ru.nstu.exam.exception.ExamException.userError;

@Service
public class AnswerService extends BasePersistentService<Answer, StudentAnswerBean, AnswerRepository> {
    private final Random random = new Random();

    private final MessageService messageService;
    private final TeacherService teacherService;

    public AnswerService(AnswerRepository repository, MessageService messageService, TeacherService teacherService) {
        super(repository);
        this.messageService = messageService;
        this.teacherService = teacherService;
    }

    public void generateAnswers(Ticket ticket, ExamRule examRule, List<Task> questions, List<Task> exercises) {
        try {
            Set<Long> usedQuestions = new HashSet<>(examRule.getQuestionCount());
            Set<Long> usedExercises = new HashSet<>(examRule.getExerciseCount());
            for (int i = 0; i < examRule.getQuestionCount(); i++) {
                generateAnswer(ticket, questions, usedQuestions, i + 1);
            }
            for (int i = 0; i < examRule.getExerciseCount(); i++) {
                generateAnswer(ticket, exercises, usedExercises, i + 1);
            }
        } catch (Exception e) {
            List<Answer> answers = getRepository().findAllByTicket(ticket);
            answers.forEach(this::delete);
            throw e;
        }
    }

    private void generateAnswer(Ticket ticket, List<Task> tasks, Set<Long> usedTasks, int number) {
        Answer answer = new Answer();
        answer.setTicket(ticket);
        int index = random.nextInt(tasks.size());
        while (usedTasks.contains(tasks.get(index).getId())) {
            index = random.nextInt(tasks.size());
        }
        answer.setTask(tasks.get(index));
        answer.setNumber(number);
        answer.setStatus(null);
        usedTasks.add(tasks.get(index).getId());
        save(answer);
    }

    public List<StudentAnswerBean> findByTicket(Ticket ticket, Pageable pageable) {
        return getRepository().findAllByTicket(ticket, pageable).stream().map(this::map).collect(Collectors.toList());
    }

    public void deleteByTicket(Ticket ticket) {
        for (Answer answer : getRepository().findAllByTicket(ticket)) {
            delete(answer);
        }
    }

    public Page<MessageBean> findAllMessages(Long answerId, Account account, Pageable pageable) {
        Answer answer = findById(answerId);
        if (answer == null) {
            userError("No answer found");
        }
        if (account.getRoles().contains(UserRole.ROLE_STUDENT)) {
            if (!Objects.equals(account.getId(), answer.getTicket().getStudent().getAccount().getId())) {
                userError("That student is not allowed to read there");
            }
        }
        if (account.getRoles().contains(UserRole.ROLE_TEACHER)) {
            if (!Objects.equals(account.getId(), answer.getTicket().getExamPeriod().getExam().getTeacher().getAccount().getId())) {
                userError("That teacher is not allowed to read there");
            }
        }

        return messageService.findAllByAnswer(answer, pageable);
    }

    public MessageBean newMessage(Long answerId, NewMessageBean messageBean, Account account) {
        Answer answer = findById(answerId);
        if (answer == null) {
            userError("No answer found");
        }
        ExamPeriod examPeriod = answer.getTicket().getExamPeriod();
        if (!ExamPeriodState.PROGRESS.equals(examPeriod.getState())) {
            userError("Wrong state");
        }
        if (account.getRoles().contains(UserRole.ROLE_STUDENT)) {
            if (!Objects.equals(answer.getTicket().getStudent().getAccount().getId(), account.getId())) {
                userError("That student is not allowed to write there");
            }
            return messageService.createAnswerMessage(messageBean, answer, account);
        } else if (account.getRoles().contains(UserRole.ROLE_TEACHER)) {
            if (!Objects.equals(examPeriod.getExam().getTeacher().getAccount().getId(), account.getId())) {
                userError("That teacher is not allowed to write there");
            }
            return messageService.createAnswerMessage(messageBean, answer, account);
        }
        return userError("Admin cannot write there");
    }

    public void rate(Long answerId, UpdateAnswerBean answerBean, Account account) {
        Answer answer = findById(answerId);
        if (answer == null) {
            userError("No answer found");
        }
        Teacher teacher = teacherService.findByAccount(account);
        if (!Objects.equals(answer.getTicket().getExamPeriod().getExam().getTeacher().getId(), teacher.getId())) {
            userError("That teacher is not allowed to rate");
        }
        Integer rating = answerBean.getRating();
        if (rating == null) {
            userError("Rating must not be null");
        }
        RatingSystem ratingSystem = answer.getTicket().getExamPeriod().getExam().getExamRule().getRatingSystem();
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
        Task task = answer.getTask();
        if (TaskType.QUESTION.equals(task.getTaskType()) && rating > maxQuestionRating
                || TaskType.EXERCISE.equals(task.getTaskType()) && rating > maxExerciseRating
        ) {
            userError("Rating must bot be bigger that max of rating system");
        }
        answer.setRating(rating);
        AnswerStatus status = ratingSystem.getRatingMap().get(task.getTaskType()).get(rating);
        answer.setStatus(status == null ? AnswerStatus.CHECKING : status);

        save(answer);
    }

    @Override
    public void delete(Answer answer) {
        for (Message message : CollectionUtils.emptyIfNull(answer.getMessages())) {
            messageService.delete(message);
        }
        super.delete(answer);
    }

    @Override
    protected StudentAnswerBean map(Answer entity) {
        Task task = entity.getTask();

        StudentAnswerBean bean = new StudentAnswerBean();
        bean.setId(entity.getId());
        bean.setRating(entity.getRating());
        bean.setTicketId(entity.getTicket() == null ? null : entity.getTicket().getId());

        StudentTaskBean taskBean = new StudentTaskBean();
        taskBean.setId(task.getId());
        taskBean.setTaskType(task.getTaskType());
        taskBean.setArtefactId(task.getArtefact() == null ? null : task.getArtefact().getId());
        taskBean.setText(task.getText());
        taskBean.setThemeName(task.getTheme() == null ? null : task.getTheme().getName());
        bean.setTask(taskBean);

        bean.setNumber(entity.getNumber());
        bean.setStatus(entity.getStatus());
        return bean;
    }

    @Override
    protected Answer map(StudentAnswerBean bean) {
        return null;
    }

}
