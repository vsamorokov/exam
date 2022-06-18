package ru.nstu.exam.notification.firebase;

import org.springframework.stereotype.Service;
import ru.nstu.exam.entity.Account;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static ru.nstu.exam.utils.Utils.checkNotEmpty;

@Service
public class ClientTokenRegistry {
    private final Map<Long, String> accountIdToToken = new ConcurrentHashMap<>();

    public void addToken(Account account, String token) {
        checkNotEmpty(token, "Token cannot be empty");
        accountIdToToken.put(account.getId(), token);
    }
    public String getToken(Long id) {
        return accountIdToToken.get(id);
    }
}
