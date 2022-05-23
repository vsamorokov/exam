package ru.nstu.exam.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.nstu.exam.bean.AccountBean;
import ru.nstu.exam.entity.Account;
import ru.nstu.exam.repository.AccountRepository;

import static ru.nstu.exam.utils.Utils.*;

@Service
public class AccountService extends BasePersistentService<Account, AccountBean, AccountRepository> {

    private final PasswordEncoder passwordEncoder;

    public AccountService(AccountRepository repository, PasswordEncoder passwordEncoder) {
        super(repository);
        this.passwordEncoder = passwordEncoder;
    }

    public AccountBean me(Account account) {
        return map(account);
    }

    public Account findAccountByUsername(String username) {
        return getRepository().findByUsername(username);
    }

    public AccountBean findOne(Long accountId) {
        Account account = findById(accountId);
        checkNotNull(account, "Account not found");
        return map(account);
    }

    public AccountBean createAccount(AccountBean accountBean) {
        checkUsername(accountBean.getUsername());
        checkPassword(accountBean.getPassword());

        Account account = map(accountBean);

        return map(save(account));
    }

    /**
     * Used by account owner
     */
    public AccountBean update(AccountBean bean, Account account) {
        checkPassword(bean.getPassword());

        account.setPassword(passwordEncoder.encode(bean.getPassword()));
        account.setName(bean.getName());
        account.setSurname(bean.getSurname());

        return map(save(account));
    }

    public AccountBean update(AccountBean bean) {
        Account account = findById(bean.getId());
        checkNotNull(account, "Account not found");
        checkUsername(bean.getUsername());
        checkPassword(bean.getPassword());

        account.setUsername(bean.getUsername());
        account.setPassword(passwordEncoder.encode(bean.getPassword()));
        account.setRoles(bean.getRoles());
        account.setName(bean.getName());
        account.setSurname(bean.getSurname());
        return map(save(account));
    }

    private void checkPassword(String password) {
        checkNotNull(password, "Password must not be null");
    }

    private void checkUsername(String username) {
        checkNotEmpty(username, "Username must not be null or empty");
        checkNull(findAccountByUsername(username), "Account with specified username already exists");
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
