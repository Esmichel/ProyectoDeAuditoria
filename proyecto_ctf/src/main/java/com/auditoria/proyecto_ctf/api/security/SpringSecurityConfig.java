package com.auditoria.proyecto_ctf.api.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.password.MessageDigestPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.auditoria.proyecto_ctf.application.authentication.JwtAuthenticationEntryPoint;
import com.auditoria.proyecto_ctf.application.authentication.JwtAuthenticationFilter;

@Configuration
@EnableMethodSecurity
public class SpringSecurityConfig {

    private final JwtAuthenticationEntryPoint authenticationEntryPoint;
    private final JwtAuthenticationFilter authenticationFilter;

    public SpringSecurityConfig(JwtAuthenticationEntryPoint authenticationEntryPoint, JwtAuthenticationFilter authenticationFilter) {
        this.authenticationEntryPoint = authenticationEntryPoint;
        this.authenticationFilter = authenticationFilter;
    }

    @Bean
    public static PasswordEncoder passwordEncoder() {
        return new MessageDigestPasswordEncoder("MD5"); // Se usa MD5 intencionadamente (pese a estar deprecado).
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http.csrf(csrf -> csrf.disable()) // Desactiva CSRF (ajustar según las necesidades).
                .authorizeHttpRequests(authorize -> {
                    // Permitir acceso público a los endpoints de autenticación y login.
                    authorize.requestMatchers("/api/auth/**").permitAll();
                    authorize.requestMatchers(HttpMethod.OPTIONS, "/**").permitAll(); // Permitir opciones CORS.
                    authorize.requestMatchers("/login").permitAll(); // Permitir acceso sin autenticación al login.

                    // Proteger las rutas sensibles.
                    authorize.requestMatchers("/dashboard").authenticated(); // Requiere autenticación.
                    authorize.anyRequest().authenticated(); // Requiere autenticación para cualquier otra ruta.
                })
                .formLogin(form -> form
                        .loginPage("/login") // Especifica la URL personalizada para la página de login.
                        .defaultSuccessUrl("/dashboard", true) // Redirige al dashboard tras login exitoso.
                        .permitAll() // Permitir acceso sin autenticación al login.
                )
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(authenticationEntryPoint)) // Manejo de excepciones de autenticación.
                .addFilterBefore(authenticationFilter, UsernamePasswordAuthenticationFilter.class); // Filtro JWT.

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }
}
