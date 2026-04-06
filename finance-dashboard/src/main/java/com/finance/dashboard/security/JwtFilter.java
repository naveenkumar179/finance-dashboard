package com.finance.dashboard.security;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collections;

@Component
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwt;

    public JwtFilter(JwtUtil jwt) {
        this.jwt = jwt;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req,
                                    HttpServletResponse res,
                                    FilterChain chain)
            throws ServletException, IOException {

        String header = req.getHeader("Authorization");

        if (header != null && header.startsWith("Bearer ")) {
            try {
                String token = header.substring(7);
                Claims claims = jwt.extract(token);

                if (claims != null) {
                    String email = claims.getSubject();
                    // Using .get() safely by checking if it exists
                    Object roleObj = claims.get("role"); 
                    
                    if (email != null && roleObj != null) {
                        String role = roleObj.toString();
                        
                        // Ensure it has ROLE_ prefix for Spring's hasRole()
                        String authority = role.startsWith("ROLE_") ? role : "ROLE_" + role;

                        UsernamePasswordAuthenticationToken auth =
                                new UsernamePasswordAuthenticationToken(
                                        email,
                                        null,
                                        Collections.singletonList(new SimpleGrantedAuthority(authority))
                                );

                        SecurityContextHolder.getContext().setAuthentication(auth);
                    }
                }
            } catch (Exception e) {
                // This prevents the "NoSuchElementException" from crashing the service
                logger.error("Could not set user authentication in security context", e);
                SecurityContextHolder.clearContext();
            }
        }

        chain.doFilter(req, res);
    }
}