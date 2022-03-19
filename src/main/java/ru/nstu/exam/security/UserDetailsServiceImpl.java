package ru.nstu.exam.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.nstu.exam.entity.Account;
import ru.nstu.exam.repository.AccountRepository;
import ru.nstu.exam.service.AccountService;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final AccountService accountService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Account account = accountService.findAccountByUsername(username);

        if(account == null){
            String message = String.format("user with username %s not found", username);
            throw new UsernameNotFoundException(message);
        }
        return new UserDetailsImpl(account);
    }
}
