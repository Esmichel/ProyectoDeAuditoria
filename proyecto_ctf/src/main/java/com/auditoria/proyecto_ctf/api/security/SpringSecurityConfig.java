package com.auditoria.proyecto_ctf.api.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.MessageDigestPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.auditoria.proyecto_ctf.application.authentication.JwtAuthenticationEntryPoint;
import com.auditoria.proyecto_ctf.application.authentication.JwtAuthenticationFilter;

@Configuration
@EnableMethodSecurity
public class SpringSecurityConfig {


    private JwtAuthenticationEntryPoint authenticationEntryPoint;

    private JwtAuthenticationFilter authenticationFilter;

    public SpringSecurityConfig(
            JwtAuthenticationEntryPoint authenticationEntryPoint, JwtAuthenticationFilter authenticationFilter) {
        this.authenticationEntryPoint = authenticationEntryPoint;
        this.authenticationFilter = authenticationFilter;
    }

    @Bean
    public static PasswordEncoder passwordEncoder() {
        return new MessageDigestPasswordEncoder("MD5"); // Sabemos que esta deprecado pero necesitamos que se inseguro
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable()) // Disable CSRF for simplicity, can be adjusted if necessary
                .authorizeHttpRequests((authorize) -> {
                    // Public authentication endpoints
                    authorize.requestMatchers("/api/auth/**").permitAll();
                    // Allow OPTIONS requests to any endpoint
                    authorize.requestMatchers(HttpMethod.OPTIONS, "/**").permitAll();
                    // Allow unauthenticated access to the login page
                    authorize.requestMatchers("/login").permitAll();
                    // Allow unauthenticated access to static resources (CSS, JS, images, etc.)
                    authorize.requestMatchers("/css/**", "/js/**", "/img/**", "/favicon.ico").permitAll();
                    // All other requests require authentication
                    authorize.anyRequest().authenticated();
                })
                .formLogin(form -> form
                        .loginPage("/login") // Specify the custom login page URL
                        .permitAll() // Allow unauthenticated access to the login page
                )
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(authenticationEntryPoint)) // Custom authentication entry point
                .addFilterBefore(authenticationFilter, UsernamePasswordAuthenticationFilter.class); // Custom JWT filter

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    // @Bean
    // public UserDetailsService userDetailsService(){
    //
    // UserDetails ramesh = User.builder()
    // .username("ramesh")
    // .password(passwordEncoder().encode("password"))
    // .roles("USER")
    // .build();
    //
    // UserDetails admin = User.builder()
    // .username("admin")
    // .password(passwordEncoder().encode("admin"))
    // .roles("ADMIN")
    // .build();
    //
    // return new InMemoryUserDetailsManager(ramesh, admin);
    // }

    // You may want to implement a custom UserDetailsService if you're not using
    // in-memory authentication
    // @Bean
    // public UserDetailsService userDetailsService() {
    // UserDetails user = User.builder()
    // .username("user")
    // .password(passwordEncoder().encode("password"))
    // .roles("USER")
    // .build();
    // return new InMemoryUserDetailsManager(user);
    // }

}