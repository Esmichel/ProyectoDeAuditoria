package com.auditoria.proyecto_ctf.api.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/api/auth")
public class LoginController {

    @GetMapping("/logform")
    public String showLoginPage() {
        return "login";
    }
}
