package com.jackdo.storage.filter;

import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;

@Component
public class AuthFilter implements Filter {
    private final String userPassword = System.getenv("ADMIN_PASSWORD");

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        Filter.super.init(filterConfig);
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) servletRequest;
        HttpServletResponse res = (HttpServletResponse) servletResponse;

        Cookie[] cookies = req.getCookies();
        if (cookies == null) {
            req.setAttribute("auth", false);
        } else {
            Optional<String> token = Arrays.stream(cookies).filter(c -> "token".equals(c.getName())).map(Cookie::getValue).findFirst();

            if (token.get().equals(userPassword)) {
                req.setAttribute("auth", true);
            } else {
                req.setAttribute("auth", false);
            }
        }
        filterChain.doFilter(req, res);
    }

    @Override
    public void destroy() {
        Filter.super.destroy();
    }
}
