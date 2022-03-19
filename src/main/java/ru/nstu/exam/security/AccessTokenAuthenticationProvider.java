package ru.nstu.exam.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;
import ru.nstu.exam.entity.AccessToken;
import ru.nstu.exam.entity.Account;

@Component
@RequiredArgsConstructor
public class AccessTokenAuthenticationProvider implements AuthenticationProvider {

    private final AccessTokenService accessTokenService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        String token = (String) authentication.getCredentials();

        AccessToken accessToken = accessTokenService.findAccessToken(token);

        if (accessToken == null) {
            throw new BadCredentialsException("Token cannot be found");
        }

        if (accessTokenService.isExpired(accessToken)) {
            throw new BadCredentialsException("Provided token is expired");
        }

        accessToken = accessTokenService.refreshToken(accessToken.getId());
        if (accessToken == null) {
            throw new BadCredentialsException("Token cannot be found");
        }

        Account account = accessToken.getAccount();
        if (account == null) {
            throw new BadCredentialsException("No account associated with the token: " + token);
        }

        UserDetailsImpl userDetails = new UserDetailsImpl(account);
        return new AccessTokenAuthenticationToken(userDetails, userDetails.getPassword(), userDetails.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.isAssignableFrom(AccessTokenAuthenticationToken.class);
    }
}
