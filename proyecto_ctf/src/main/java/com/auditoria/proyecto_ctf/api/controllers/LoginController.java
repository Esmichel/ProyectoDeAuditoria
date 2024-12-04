package com.auditoria.proyecto_ctf.api.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class LoginController {

    @RequestMapping("/")
    public String home(HttpServletRequest request, Model model) {
        return "redirect:/process";
    }
    
    @RequestMapping("/login")
    public String login(HttpServletRequest request) {
        if (request.getUserPrincipal()!=null) {
            return "redirect:/";
        }
        return "login";
    }
}
