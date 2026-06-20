package com.appverse.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        // 1. Grab the Authorization header from Swagger
        String header = request.getHeader("Authorization");

        // 2. Check if it contains a Bearer token
        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7); // Remove the word "Bearer "
            
            try {
                // 3. Extract the user's email from the token to verify it is valid
                String email = jwtTokenProvider.getEmailFromToken(token); // Or getUsernameFromToken, depending on your provider
                
                // 4. Give the user an "All Access Pass" for this specific request
                UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(email, null, new ArrayList<>());
                SecurityContextHolder.getContext().setAuthentication(auth);
            } catch (Exception e) {
                System.out.println("Token validation failed: " + e.getMessage());
            }
        }
        
        // 5. Send the request to the next step
        filterChain.doFilter(request, response);
    }
}