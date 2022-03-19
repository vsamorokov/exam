package ru.nstu.exam.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import ru.nstu.exam.bean.AccountBean;
import ru.nstu.exam.entity.Account;
import ru.nstu.exam.repository.AccountRepository;

import java.util.Collections;

import static ru.nstu.exam.exception.ExamException.userError;
import static ru.nstu.exam.security.UserRole.ROLE_ADMIN;

@Service
public class AccountService extends BasePersistentService<Account, AccountBean, AccountRepository> {

    private final PasswordEncoder passwordEncoder;

    public AccountService(AccountRepository repository, PasswordEncoder passwordEncoder) {
        super(repository);
        this.passwordEncoder = passwordEncoder;
    }

    public Account findAccountByUsername(String username) {
        return getRepository().findByUsername(username);
    }

    public AccountBean createAccount(AccountBean accountBean) {
        if (findAccountByUsername(accountBean.getUsername()) != null) {
            userError("Account with specified username already exists");
        }

        Account account = map(accountBean);

        return map(save(account));
    }

    public void changePassword(AccountBean accountBean, Account account) {
        if(!StringUtils.hasText(accountBean.getPassword())){
            userError("Password must not be empty");
        }
        account.setPassword(passwordEncoder.encode(accountBean.getPassword()));
        save(account);
    }

    @Override
    protected AccountBean map(Account entity) {
        AccountBean accountBean = new AccountBean();
        accountBean.setId(entity.getId());
        accountBean.setUsername(entity.getUsername());
        accountBean.setName(entity.getName());
        accountBean.setSurname(entity.getSurname());
        accountBean.setRoles(entity.getRoles());
        return accountBean;
    }

    @Override
    protected Account map(AccountBean bean) {
        Account account = new Account();
        account.setUsername(bean.getUsername());
        account.setPassword(passwordEncoder.encode(bean.getPassword()));
        account.setName(bean.getName());
        account.setSurname(bean.getSurname());
        account.setRoles(bean.getRoles());
        return account;
    }

}
