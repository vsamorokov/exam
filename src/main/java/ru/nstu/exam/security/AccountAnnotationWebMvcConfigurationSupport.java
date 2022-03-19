package ru.nstu.exam.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import ru.nstu.exam.service.AccountService;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class AccountAnnotationWebMvcConfigurationSupport extends WebMvcConfigurationSupport {

    private final AccountService accountService;

    @Override
    protected void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.add(new UserAccountAnnotationMethodArgumentResolver(accountService));
        super.addArgumentResolvers(argumentResolvers);
    }
}
