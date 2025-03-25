package com.baki.backer.global.filter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Set;
import org.springframework.stereotype.Component;

@Component
public class SuspiciousPathFilter implements Filter {
    private static final Set<String> BLOCKED_PATH_PREFIXES = Set.of(
            "/.git",
            "/geoserver",
            "/cdn-cgi",
            "/phpmyadmin",
            "/wp-admin",
            "/wp-login",
            "/admin",
            "/actuator",
            "/env",
            "/console"
    );

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) servletRequest;
        HttpServletResponse res = (HttpServletResponse) servletResponse;

        String path = req.getRequestURI().toLowerCase();

        for (String blocked : BLOCKED_PATH_PREFIXES) {
            if (path.startsWith(blocked)) {
                // 로그 찍기 (선택)
                System.out.println("🚫 차단된 경로 요청: " + path + " from IP: " + req.getRemoteAddr());

                res.sendError(HttpServletResponse.SC_NOT_FOUND); // or SC_FORBIDDEN
                return;
            }
        }

        filterChain.doFilter(servletRequest, servletResponse);
    }
}
