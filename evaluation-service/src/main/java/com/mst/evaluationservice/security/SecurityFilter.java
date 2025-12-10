package com.mst.evaluationservice.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class SecurityFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

            String rolesHeader = request.getHeader("X-User-Roles");
            String userEmailHeader = request.getHeader("X-User-Email");

            if (rolesHeader != null && userEmailHeader != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                Set<SimpleGrantedAuthority> authorities = Arrays.stream(rolesHeader.split(","))
                        .map(String::trim)
                        .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                        .collect(Collectors.toSet());
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(userEmailHeader, null, authorities);

                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
                System.out.println(SecurityContextHolder.getContext().getAuthentication().getAuthorities());
            }
            filterChain.doFilter(request, response);

//    private void sendError(HttpServletResponse response,
//                           int status,
//                           String error,
//                           String message) throws IOException {
//
//        response.setStatus(status);
//        response.setContentType("application/json");
//
//        response.getWriter().write("""
//            {
//               "status": %d,
//               "error": "%s",
//               "message": "%s"
//            }
//        """.formatted(status, error, message));
//    }
    }
}
