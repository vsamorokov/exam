package ru.nstu.exam.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;
import ru.nstu.exam.bean.AnswerBean;
import ru.nstu.exam.bean.MessageBean;
import ru.nstu.exam.entity.Account;
import ru.nstu.exam.security.IsTeacher;
import ru.nstu.exam.security.UserAccount;
import ru.nstu.exam.service.AnswerService;

@RestController
@RequestMapping("/answer")
@RequiredArgsConstructor
public class AnswerController {
    private final AnswerService answerService;


    @GetMapping("/{answerId}/message")
    public Page<MessageBean> getMessages(@PathVariable Long answerId,
                                         @UserAccount Account account,
                                         @PageableDefault Pageable pageable
    ) {
        return answerService.findAllMessages(answerId, account, pageable);
    }

    @PostMapping("/{answerId}/message")
    public MessageBean newMessage(@PathVariable Long answerId,
                                  @RequestBody MessageBean messageBean,
                                  @UserAccount Account account
    ) {
        return answerService.newMessage(answerId, messageBean, account);
    }

    @IsTeacher
    @PutMapping("/{answerId}")
    public void rate(@PathVariable Long answerId,
                     @RequestBody AnswerBean answerBean,
                     @UserAccount Account account
    ) {
        answerService.rate(answerId, answerBean, account);
    }
}
