package com.auditoria.proyecto_ctf.application.authentication;

import com.auditoria.proyecto_ctf.api.dto.users.LoginDto;

public interface AuthService {
    String login(LoginDto loginDto);
}