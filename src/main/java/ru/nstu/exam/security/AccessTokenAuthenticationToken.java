package ru.nstu.exam.security;

import lombok.ToString;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

public class AccessTokenAuthenticationToken extends AbstractAuthenticationToken {

    private final UserDetails userDetails;

    private final String token;

    public AccessTokenAuthenticationToken(UserDetailsImpl principal, String credentials, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.userDetails = principal;
        this.token = credentials;
        super.setAuthenticated(true);
    }

    public AccessTokenAuthenticationToken(String credentials) {
        super(null);
        this.userDetails = null;
        this.token = credentials;
        super.setAuthenticated(false);
    }

    @Override
    public Object getCredentials() {
        return this.token;
    }

    @Override
    public Object getPrincipal() {
        return this.userDetails;
    }

    @Override
    public void setAuthenticated(boolean authenticated) {
        if (authenticated) {
            throw new IllegalArgumentException("Use constructor instead");
        }
        super.setAuthenticated(false);
    }
}
