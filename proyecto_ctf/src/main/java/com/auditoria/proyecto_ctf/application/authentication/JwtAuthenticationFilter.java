package com.auditoria.proyecto_ctf.application.authentication;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

// Execute Before Executing Spring Security Filters
// Validate the JWT Token and Provides user details to Spring Security for Authentication
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private JwtTokenProvider jwtTokenProvider;

    private UserDetailsService userDetailsService;

    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider, UserDetailsService userDetailsService) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        if (request.getRequestURI().startsWith("/login") || request.getRequestURI().startsWith("/api/auth/loginform")) {
            filterChain.doFilter(request, response); // Just pass the request to the next filter
            return;
        }

        // Allow static resources (CSS, JS, images) to pass
        if (request.getRequestURI().startsWith("/css") ||
                request.getRequestURI().startsWith("/js") ||
                request.getRequestURI().startsWith("/img") ||
                request.getRequestURI().endsWith(".ico") ||
                request.getRequestURI().endsWith(".css") ||
                request.getRequestURI().endsWith(".js") ||
                request.getRequestURI().endsWith(".png") ||
                request.getRequestURI().endsWith(".jpg") ||
                request.getRequestURI().endsWith(".jpeg") ||
                request.getRequestURI().endsWith(".gif") ||
                request.getRequestURI().endsWith(".svg")) {
            filterChain.doFilter(request, response); // Skip authentication for static resources
            return;
        }

        // Get JWT token from HTTP request
        String token = getTokenFromRequest(request);

        // Validate Token
        if (StringUtils.hasText(token) && jwtTokenProvider.validateToken(token)) {
            // get username from token
            String username = jwtTokenProvider.getUsername(token);

            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                    userDetails,
                    null,
                    userDetails.getAuthorities());

            authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        }

        filterChain.doFilter(request, response);
    }

    /*
     * private String getTokenFromRequest(HttpServletRequest request) {
     * String bearerToken = request.getHeader("Authorization");
     * 
     * if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
     * return bearerToken.substring(7, bearerToken.length());
     * }
     * 
     * return null;
     * }
     */

    public String getTokenFromRequest(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("jwt_token".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
}