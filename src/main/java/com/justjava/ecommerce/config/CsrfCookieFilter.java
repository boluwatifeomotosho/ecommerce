package com.justjava.ecommerce.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class CsrfCookieFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        CsrfToken csrfToken = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
        if (csrfToken != null) {
            // Subscribing to the deferred token triggers CookieCsrfTokenRepository
            // to write the XSRF-TOKEN cookie to the response, so every page load
            // guarantees a fresh cookie that matches the hidden _csrf form input.
            csrfToken.getToken();
        }
        filterChain.doFilter(request, response);
    }
}
