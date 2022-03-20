package ru.nstu.exam.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.*;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import ru.nstu.exam.service.AccountService;

import java.util.List;

@EnableWebMvc
@Configuration
@RequiredArgsConstructor
public class AccountAnnotationWebMvcConfigurationSupport implements WebMvcConfigurer {

    private final AccountService accountService;

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.add(new UserAccountAnnotationMethodArgumentResolver(accountService));
    }
}
