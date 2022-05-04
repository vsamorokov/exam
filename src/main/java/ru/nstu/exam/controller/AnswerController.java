package ru.nstu.exam.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;
import ru.nstu.exam.bean.FullAnswerBean;
import ru.nstu.exam.bean.MessageBean;
import ru.nstu.exam.bean.NewMessageBean;
import ru.nstu.exam.bean.UpdateAnswerBean;
import ru.nstu.exam.entity.Account;
import ru.nstu.exam.security.IsTeacher;
import ru.nstu.exam.security.UserAccount;
import ru.nstu.exam.service.AnswerService;

@RestController
@RequestMapping("/answer")
@RequiredArgsConstructor
@Tag(name = "Answer")
public class AnswerController {
    private final AnswerService answerService;

    @GetMapping("/{answerId}/message")
    @Operation(summary = "Get messages by an answer")
    public Page<MessageBean> getMessages(@PathVariable Long answerId,
                                         @UserAccount Account account,
                                         @PageableDefault Pageable pageable
    ) {
        return answerService.findAllMessages(answerId, account, pageable);
    }

    @PostMapping("/{answerId}/message")
    @Operation(summary = "Send message to an answer")
    public MessageBean newMessage(@PathVariable Long answerId,
                                  @RequestBody NewMessageBean messageBean,
                                  @UserAccount Account account
    ) {
        return answerService.newMessage(answerId, messageBean, account);
    }

    @IsTeacher
    @PutMapping("/{answerId}")
    @Operation(summary = "Put a mark to an answer")
    public void rate(@PathVariable Long answerId,
                     @RequestBody UpdateAnswerBean answerBean,
                     @UserAccount Account account
    ) {
        answerService.rate(answerId, answerBean, account);
    }

    @GetMapping("/{answerId}/full")
    public FullAnswerBean getFull(@PathVariable Long answerId, @RequestParam(required = false, defaultValue = "0") int level) {
        return answerService.findFull(answerId, level);
    }
}
