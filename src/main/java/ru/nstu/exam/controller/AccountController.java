package ru.nstu.exam.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.nstu.exam.bean.AccountBean;
import ru.nstu.exam.entity.Account;
import ru.nstu.exam.security.IsAdmin;
import ru.nstu.exam.security.UserAccount;
import ru.nstu.exam.service.AccountService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/account")
public class AccountController {

    private final AccountService accountService;

    @IsAdmin
    @GetMapping
    @Operation(summary = "Get all accounts")
    public List<AccountBean> findAll() {
        return accountService.findAll();
    }

    @GetMapping("/{accountId}")
    @Operation(summary = "Get one account")
    public AccountBean findOne(@PathVariable Long accountId) {
        return accountService.findOne(accountId);
    }

    @PostMapping("/change-password")
    @Operation(summary = "Change password")
    public void changePassword(@RequestBody AccountBean accountBean, @UserAccount Account account) {
        accountService.changePassword(accountBean, account);
    }

    @IsAdmin
    @PostMapping
    @Operation(summary = "Create an account", description = "Used by admins to create admins")
    public AccountBean createAccount(@RequestBody AccountBean accountBean) {
        return accountService.createAccount(accountBean);
    }
}
