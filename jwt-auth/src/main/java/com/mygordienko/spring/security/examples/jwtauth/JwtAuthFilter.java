package com.mygordienko.spring.security.examples.jwtauth;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {
    public JwtAuthFilter(JwtService jwtService, UserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        final String jwt;
        final String username;

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            // NOTE: ignore request if there is no bearer header
            filterChain.doFilter(request, response);
            return;
        }

        jwt = authHeader.substring(7);
        username = jwtService.extractUsername(jwt);

        // NOTE: perform authentication only if not already authenticated
        //  it is important to count for anonymous authentication
        var currentAuthentication = SecurityContextHolder.getContext().getAuthentication();

        if (username != null && (currentAuthentication == null || !existingAuthenticationForPrincipal(username))) {
            var userDetails = this.userDetailsService.loadUserByUsername(username);

            // NOTE: create a new context (so to not modify existing one, best practice)
            var authToken = new JwtAuthenticationToken(username, userDetails.getAuthorities());
            var context = SecurityContextHolder.createEmptyContext();
            context.setAuthentication(authToken);
            SecurityContextHolder.setContext(context);
        }

        filterChain.doFilter(request, response);
    }

    private boolean existingAuthenticationForPrincipal(String username) {
        var present = Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
                .filter(auth -> username.equals(auth.getPrincipal()))
                .filter(Authentication::isAuthenticated)
                .isPresent();
        return present;
    }
}
