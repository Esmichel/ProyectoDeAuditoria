package com.auditoria.proyecto_ctf.api.controllers.login;

import java.net.URI;

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
    /*
     * @PostMapping("/loginform")
     * public ResponseEntity<JwtAuthResponse> login(@RequestBody LoginDto loginDto)
     * {
     * String token = authService.login(loginDto);
     * 
     * JwtAuthResponse jwtAuthResponse = new JwtAuthResponse();
     * jwtAuthResponse.setAccessToken(token);
     * 
     * return new ResponseEntity<>(jwtAuthResponse, HttpStatus.OK);
     * }
     */

    @PostMapping("/loginform")
    public ResponseEntity<JwtAuthResponse> login(@RequestParam String username, @RequestParam String password/*@RequestBody LoginDto loginDto*/) {
        String token = authService.login(username, password/*loginDto*/);

        JwtAuthResponse jwtAuthResponse = new JwtAuthResponse();
        jwtAuthResponse.setAccessToken(token);

        ResponseCookie cookie = ResponseCookie.from("jwt_token", token)
                .httpOnly(true)
                .secure(true) // Ensure this is set to true if you're using HTTPS
                .path("/") // Path for which the cookie is valid
                .maxAge(60 * 60 * 24 * 7) // Cookie expiration (7 days)
                .sameSite("None") // Allow cookies to be sent in cross-origin requests
                .build();

        // Add the cookie to the response
        return ResponseEntity.status(HttpStatus.FOUND) // 302 redirect
                .header(HttpHeaders.SET_COOKIE, cookie.toString()) // Attach the cookie
                .header(HttpHeaders.LOCATION, "/dashboard")  // Redirect URL
                .build();
    }

}
