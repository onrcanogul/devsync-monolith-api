package com.api.devsync.filter;

import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RequestNotPermitted;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class GlobalRateLimitFilter extends OncePerRequestFilter {

    private final RateLimiter rateLimiter;

    public GlobalRateLimitFilter(RateLimiterRegistry registry) {
        this.rateLimiter = registry.rateLimiter("globalLimiter");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {
        try {
            rateLimiter.acquirePermission();
            filterChain.doFilter(request, response);
        } catch (RequestNotPermitted e) {
            response.setStatus(HttpServletResponse.SC_GATEWAY_TIMEOUT);
            response.getWriter().write("Too Many Request !!");
        }
    }
}
