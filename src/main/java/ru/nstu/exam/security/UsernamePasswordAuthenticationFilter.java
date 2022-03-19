package ru.nstu.exam.security;

import org.springframework.core.log.LogMessage;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.AuthenticationConverter;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class UsernamePasswordAuthenticationFilter extends OncePerRequestFilter {

    private final static RequestMatcher DEFAULT_LOGIN_MATCHER = new AntPathRequestMatcher("/login", "POST");
    private static Integer index = 1;

    private final AuthenticationEntryPoint authenticationEntryPoint = new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED);
    private final RequestMatcher matcher;
    private final AuthenticationManager authenticationManager;
    private final AuthenticationSuccessHandler authenticationSuccessHandler;

    public UsernamePasswordAuthenticationFilter(AuthenticationManager authenticationManager, AuthenticationSuccessHandler authenticationSuccessHandler) {
        this(authenticationManager, authenticationSuccessHandler, DEFAULT_LOGIN_MATCHER);
    }

    public UsernamePasswordAuthenticationFilter(AuthenticationManager authenticationManager, AuthenticationSuccessHandler authenticationSuccessHandler, RequestMatcher matcher) {
        setBeanName(getClass().getName() + index++);
        this.authenticationManager = authenticationManager;
        this.authenticationSuccessHandler = authenticationSuccessHandler;
        this.matcher = matcher;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain chain) throws ServletException, IOException {
        if (!requiresAuthentication(request)) {
            chain.doFilter(request, response);
            return;
        }
        try {
            Authentication authRequest = convert(request);

            this.logger.trace(LogMessage.format("Found username '%s' in Username Password Authentication", authRequest.getName()));
            Authentication authResult = authenticationManager.authenticate(authRequest);
            successfulAuthentication(request, response, chain, authResult);
        } catch (AuthenticationException e) {
            SecurityContextHolder.clearContext();
            this.logger.debug("Failed to process authentication request", e);
            authenticationEntryPoint.commence(request, response, e);
            chain.doFilter(request, response);
        }
    }

    private void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) throws IOException, ServletException {
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);
        if (this.logger.isDebugEnabled()) {
            this.logger.debug(LogMessage.format("Set SecurityContextHolder to %s", authentication));
        }
        authenticationSuccessHandler.onAuthenticationSuccess(request, response, chain, authentication); // handler decides whether to continue chain or not
    }

    private boolean requiresAuthentication(HttpServletRequest request) {
        return matcher.matches(request);
    }

    private static Authentication convert(HttpServletRequest request) {
        byte[] authHeader = request.getHeader("X-Authentication").trim().getBytes(StandardCharsets.UTF_8);
        byte[] decoded = Base64.getDecoder().decode(authHeader);

        String token = new String(decoded, StandardCharsets.UTF_8);
        int delim = token.indexOf(":");
        if (delim == -1) {
            throw new BadCredentialsException("Invalid basic authentication token");
        }
        return new UsernamePasswordAuthenticationToken(token.substring(0, delim), token.substring(delim + 1));
    }
}
