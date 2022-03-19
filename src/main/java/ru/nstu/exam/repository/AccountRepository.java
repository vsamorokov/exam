package ru.nstu.exam.repository;

import org.springframework.stereotype.Repository;
import ru.nstu.exam.entity.Account;

@Repository
public interface AccountRepository extends PersistableEntityRepository<Account> {

    Account findByUsername(String username);
}
