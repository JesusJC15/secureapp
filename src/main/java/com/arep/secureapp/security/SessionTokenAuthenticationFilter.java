package com.arep.secureapp.security;

import java.io.IOException;
import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.arep.secureapp.repository.UserAccountRepository;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class SessionTokenAuthenticationFilter extends OncePerRequestFilter {

    private final SessionTokenService sessionTokenService;
    private final UserAccountRepository userAccountRepository;

    public SessionTokenAuthenticationFilter(SessionTokenService sessionTokenService,
            UserAccountRepository userAccountRepository) {
        this.sessionTokenService = sessionTokenService;
        this.userAccountRepository = userAccountRepository;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {
        String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String token = authorizationHeader.substring(7).trim();
            sessionTokenService.resolveUserId(token)
                    .flatMap(userAccountRepository::findById)
                    .ifPresent(user -> {
                        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                                user.getUsername(),
                                token,
                                List.of(new SimpleGrantedAuthority("ROLE_USER")));
                        authentication.setDetails(user.getId());
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                    });
        }

        filterChain.doFilter(request, response);
    }
}
