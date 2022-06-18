package ru.nstu.exam.security;

import liquibase.repackaged.org.apache.commons.collections4.CollectionUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import ru.nstu.exam.entity.AccessToken;
import ru.nstu.exam.entity.Account;
import ru.nstu.exam.repository.AccessTokenRepository;
import ru.nstu.exam.service.AccountService;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Base64;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccessTokenService {

    private final static int TOKEN_LENGTH = 20;
    private final static ZoneId UTC = ZoneId.of("UTC");

    @Value("${auth.token.expiration-minutes:1440}") // 24 hours
    private int tokenExpiration;

    private final SecureRandom random = new SecureRandom();

    private final AccessTokenRepository tokenRepository;
    private final AccountService accountService;

    public String generateToken(Authentication authentication) {

        String tokenString = generateTokenString(TOKEN_LENGTH);

        if(!(authentication.getPrincipal() instanceof UserDetailsImpl)) {
            return null;
        }

        Account account = accountService.findAccountByUsername(authentication.getName());
        if(account == null) {
            return null;
        }

        AccessToken accessToken = new AccessToken();
        accessToken.setToken(tokenString);
        accessToken.setAccount(account);
        accessToken.setIssueTime(LocalDateTime.now(UTC));

        tokenRepository.saveAndFlush(accessToken);

        return tokenString;
    }

    public AccessToken findAccessToken(String token) {
        return tokenRepository.findByToken(token);
    }

    public String generateTokenString(int length) {
        byte[] bytes = new byte[length];
        random.nextBytes(bytes);
        return new String(Base64.getEncoder().encode(bytes));
    }

    public boolean isExpired(AccessToken accessToken) {
        return accessToken.getIssueTime().plusMinutes(tokenExpiration).isBefore(LocalDateTime.now(UTC));
    }

    public AccessToken refreshToken(Long tokenId) {
        AccessToken accessToken = tokenRepository.findById(tokenId).orElse(null);
        if (accessToken == null) {
            return null;
        }
        accessToken.setIssueTime(LocalDateTime.now(UTC));
        return tokenRepository.save(accessToken);
    }

    @Scheduled(fixedDelay = 60000L)
    void deleteExpired() {
        LocalDateTime expired = LocalDateTime.now(UTC).minusMinutes(tokenExpiration);
        List<AccessToken> tokens = tokenRepository.findAllByIssueTimeBefore(expired);
        if (CollectionUtils.isEmpty(tokens)) {
            return;
        }
        tokenRepository.deleteAll(tokens);
        log.info("{} expired tokens deleted", tokens.size());
    }
}
