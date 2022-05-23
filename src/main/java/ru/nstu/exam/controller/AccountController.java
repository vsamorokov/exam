package ru.nstu.exam.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Account")
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

    @GetMapping("/me")
    @Operation(summary = "Get info about self")
    public AccountBean me(@UserAccount Account account) {
        return accountService.me(account);
    }

    @PutMapping("/me")
    @Operation(summary = "Update sender's account")
    public AccountBean updateMyAccount(@RequestBody AccountBean accountBean, @UserAccount Account account) {
        return accountService.update(accountBean, account);
    }

    @IsAdmin
    @PutMapping
    @Operation(summary = "Update other's account")
    public AccountBean updateAccount(@RequestBody AccountBean accountBean) {
        return accountService.update(accountBean);
    }

    @IsAdmin
    @PostMapping
    @Operation(summary = "Create an account", description = "Used by admins to create admins")
    public AccountBean createAccount(@RequestBody AccountBean accountBean) {
        return accountService.createAccount(accountBean);
    }
}
