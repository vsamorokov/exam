package ru.nstu.exam.security;

import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.lang.NonNull;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.support.WebArgumentResolver;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import ru.nstu.exam.entity.Account;
import ru.nstu.exam.service.AccountService;


@RequiredArgsConstructor
public class UserAccountAnnotationMethodArgumentResolver implements HandlerMethodArgumentResolver {

    private final AccountService accountService;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(UserAccount.class) && parameter.getParameterType().equals(Account.class);
    }

    @Override
    public Object resolveArgument(@NonNull MethodParameter parameter,
                                  ModelAndViewContainer mavContainer,
                                  @NonNull NativeWebRequest webRequest,
                                  WebDataBinderFactory binderFactory
    ) {
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!StringUtils.hasText(name)) {
            return WebArgumentResolver.UNRESOLVED;
        }
        return accountService.findAccountByUsername(name);
    }
}
