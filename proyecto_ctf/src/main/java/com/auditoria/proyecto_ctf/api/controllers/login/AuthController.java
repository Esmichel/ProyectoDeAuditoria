package com.auditoria.proyecto_ctf.api.controllers.login;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.auditoria.proyecto_ctf.api.dto.users.JwtAuthResponse;
import com.auditoria.proyecto_ctf.api.dto.users.LoginDto;
import com.auditoria.proyecto_ctf.application.authentication.AuthService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    // Build Login REST API
    /*@PostMapping("/loginform")
    public ResponseEntity<JwtAuthResponse> login(@RequestBody LoginDto loginDto) {
        String token = authService.login(loginDto);

        JwtAuthResponse jwtAuthResponse = new JwtAuthResponse();
        jwtAuthResponse.setAccessToken(token);

        return new ResponseEntity<>(jwtAuthResponse, HttpStatus.OK);
    }*/

    @PostMapping("/loginform")
    public ResponseEntity<JwtAuthResponse> login(@RequestBody LoginDto loginDto) {
        String token = authService.login(loginDto);

        JwtAuthResponse jwtAuthResponse = new JwtAuthResponse();
        jwtAuthResponse.setAccessToken(token);

        // Create an HttpOnly cookie
        ResponseCookie cookie = ResponseCookie.from("jwt_token", token)
                .httpOnly(true)
                .secure(true) // Set to true if using HTTPS
                .path("/") // Set the cookie path
                .maxAge(60 * 60 * 24 * 7) // Set the cookie expiration time (7 days)
                .build();

        // Add the cookie to the response
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(jwtAuthResponse);
    }

}
