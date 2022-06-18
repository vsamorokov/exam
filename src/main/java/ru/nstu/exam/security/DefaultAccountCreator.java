package ru.nstu.exam.security;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import ru.nstu.exam.entity.Account;
import ru.nstu.exam.service.AccountService;

import java.util.Collections;

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "account.default", name = "enabled", havingValue = "true", matchIfMissing = true)
public class DefaultAccountCreator {

    @Value("${account.default.username:admin}")
    private String username;

    @Value("${account.default.password:password}")
    private String password;

    private final AccountService accountService;
    private final PasswordEncoder passwordEncoder;

    @EventListener(ContextRefreshedEvent.class)
    public void createAccount(){
        long count = accountService.getRepository().count();

        if(count > 0){
            return;
        }

        Account account = new Account();
        account.setUsername(username);
        account.setPassword(passwordEncoder.encode(password));
        account.setRoles(Collections.singleton(UserRole.ROLE_ADMIN));
        account.setName("name");
        account.setSurname("surname");

        accountService.save(account);
    }

}
