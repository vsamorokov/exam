package ru.nstu.exam.security;

import lombok.RequiredArgsConstructor;
import org.springframework.core.log.LogMessage;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.*;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RequiredArgsConstructor
public class AccessTokenAuthenticationFilter extends OncePerRequestFilter {

    public static final String HEADER_SECURITY_TOKEN = "X-Access-Token";

    private final AuthenticationEntryPoint authenticationEntryPoint = new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED);
    private final AuthenticationManager authenticationManager;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain chain) throws ServletException, IOException {

        try {
            Authentication authRequest = convert(request);

            if (authRequest == null) {
                this.logger.trace("Did not process authentication request since failed to find "
                        + "token in Token Authentication header");
                chain.doFilter(request, response);
                return;
            }

            this.logger.trace(LogMessage.format("Found username '%s' in Token Authentication header", authRequest.getCredentials()));
            Authentication authResult = this.authenticationManager.authenticate(authRequest);
            SecurityContext context = SecurityContextHolder.createEmptyContext();
            context.setAuthentication(authResult);
            SecurityContextHolder.setContext(context);
            if (this.logger.isDebugEnabled()) {
                this.logger.debug(LogMessage.format("Set SecurityContextHolder to %s", authResult));
            }
        } catch (AuthenticationException e) {
            SecurityContextHolder.clearContext();
            this.logger.debug("Failed to process authentication request", e);
            this.authenticationEntryPoint.commence(request, response, e);
        }

        chain.doFilter(request, response);
    }

    private Authentication convert(HttpServletRequest request) {
        String token = request.getHeader(HEADER_SECURITY_TOKEN);
        if (token == null) {
            return null;
        }
        return new AccessTokenAuthenticationToken(token);
    }
}