package ru.nstu.exam.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;
import ru.nstu.exam.bean.AnswerBean;
import ru.nstu.exam.bean.MessageBean;
import ru.nstu.exam.bean.full.FullAnswerBean;
import ru.nstu.exam.entity.Account;
import ru.nstu.exam.security.UserAccount;
import ru.nstu.exam.service.AnswerService;

@RestController
@RequestMapping("/answers")
@RequiredArgsConstructor
@Tag(name = "Answer")
public class AnswerController {
    private final AnswerService answerService;

    @GetMapping("/{answerId}/message")
    @Operation(summary = "Get messages by an answer")
    public Page<MessageBean> getMessages(@PathVariable Long answerId,
                                         @PageableDefault Pageable pageable
    ) {
        return answerService.findAllMessages(answerId, pageable);
    }

    @PostMapping("/{answerId}/message")
    @Operation(summary = "Send message to an answer")
    public MessageBean newMessage(@PathVariable Long answerId,
                                  @RequestBody MessageBean messageBean,
                                  @UserAccount Account account
    ) {
        return answerService.newMessage(answerId, messageBean, account);
    }

    @GetMapping("/{answerId}/full")
    public FullAnswerBean getFull(@PathVariable Long answerId, @RequestParam(required = false, defaultValue = "0") int level) {
        return answerService.findFull(answerId, level);
    }

    @PutMapping("/state")
    public AnswerBean updateState(
            @RequestBody AnswerBean bean,
            @UserAccount Account account
    ) {
        return answerService.updateState(bean, account);
    }
}
