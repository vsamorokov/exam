package ru.nstu.exam.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.nstu.exam.bean.AccountBean;
import ru.nstu.exam.entity.Account;
import ru.nstu.exam.security.IsAdmin;
import ru.nstu.exam.security.UserAccount;
import ru.nstu.exam.service.AccountService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/account")
public class AccountController {

    private final AccountService accountService;


    @PostMapping("/change-password")
    public void changePassword(@RequestBody AccountBean accountBean, @UserAccount Account account) {
        accountService.changePassword(accountBean, account);
    }

    @IsAdmin
    @PostMapping
    public AccountBean createAccount(@RequestBody AccountBean accountBean){
        return accountService.createAccount(accountBean);
    }

}
