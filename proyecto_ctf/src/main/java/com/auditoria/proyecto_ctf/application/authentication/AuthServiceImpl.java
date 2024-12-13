package com.auditoria.proyecto_ctf.application.authentication;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.auditoria.proyecto_ctf.api.dto.users.LoginDto;

@Service

public class AuthServiceImpl implements AuthService {

    private AuthenticationManager authenticationManager;
    private JwtTokenProvider jwtTokenProvider;

    public AuthServiceImpl(AuthenticationManager authenticationManager, JwtTokenProvider jwtTokenProvider) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public String login(String username, String password/*LoginDto loginDto*/) {

        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                username,
                password        
                /*loginDto.getUsername(),
                loginDto.getPassword()*/
        ));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        return jwtTokenProvider.generateToken(authentication);

    }
}
