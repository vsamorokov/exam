package ru.nstu.exam.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.nstu.exam.bean.AnswerBean;
import ru.nstu.exam.bean.MessageBean;
import ru.nstu.exam.entity.*;
import ru.nstu.exam.enums.ExamPeriodState;
import ru.nstu.exam.repository.AnswerRepository;
import ru.nstu.exam.security.UserRole;

import java.util.*;
import java.util.stream.Collectors;

import static ru.nstu.exam.exception.ExamException.userError;

@Service
public class AnswerService extends BasePersistentService<Answer, AnswerBean, AnswerRepository> {
    private final Random random = new Random();

    private final TaskService taskService;
    private final MessageService messageService;

    public AnswerService(AnswerRepository repository, TaskService taskService, MessageService messageService) {
        super(repository);
        this.taskService = taskService;
        this.messageService = messageService;
    }

    public void generateAnswers(Ticket ticket, ExamRule examRule, List<Task> questions, List<Task> exercises) {
        try {
            Set<Long> usedQuestions = new HashSet<>(examRule.getQuestionCount());
            Set<Long> usedExercises = new HashSet<>(examRule.getExerciseCount());
            for (int i = 0; i < examRule.getQuestionCount(); i++) {
                createAnswer(ticket, questions, usedQuestions);
            }
            for (int i = 0; i < examRule.getExerciseCount(); i++) {
                createAnswer(ticket, exercises, usedExercises);
            }
        } catch (Exception e) {
            List<Answer> answers = getRepository().findAllByTicket(ticket);
            answers.forEach(this::delete);
            throw e;
        }
    }

    private void createAnswer(Ticket ticket, List<Task> tasks, Set<Long> usedTasks) {
        Answer answer = new Answer();
        answer.setTicket(ticket);
        int index = random.nextInt(tasks.size());
        while (usedTasks.contains(tasks.get(index).getId())) {
            index = random.nextInt(tasks.size());
        }
        answer.setTask(tasks.get(index));
        usedTasks.add(tasks.get(index).getId());
        save(answer);
    }

    public List<AnswerBean> findByTicket(Ticket ticket) {
        return getRepository().findAllByTicket(ticket).stream().map(this::map).collect(Collectors.toList());
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

    public MessageBean newMessage(Long answerId, MessageBean messageBean, Account account) {
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

    @Override
    protected AnswerBean map(Answer entity) {
        AnswerBean answerBean = new AnswerBean();
        answerBean.setRating(entity.getRating());
        answerBean.setTask(taskService.map(entity.getTask()));
        return answerBean;
    }

    @Override
    protected Answer map(AnswerBean bean) {
        return null;
    }

}
