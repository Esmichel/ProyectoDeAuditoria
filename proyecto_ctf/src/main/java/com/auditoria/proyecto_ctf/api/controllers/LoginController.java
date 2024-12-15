package com.auditoria.proyecto_ctf.api.controllers;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import ch.qos.logback.core.model.Model;
import jakarta.servlet.http.HttpServletRequest;

@Controller
public class LoginController {

    @RequestMapping("/")
    public String home(HttpServletRequest request, Model model) {
        return "redirect:/dashboard";
    }
    
    @RequestMapping("/login")
    public String login(HttpServletRequest request) {
        /*if (SecurityContextHolder.getContext().getAuthentication()!=null) {
            return "redirect:/";
        }*/
        return "login";
    }

    @RequestMapping("/dashboard")
    public String dashboard(HttpServletRequest request) {
        return "dashboard";
    }

    @RequestMapping("/estadisticas")
    public String estadisticas(HttpServletRequest request) {
        return "estadisticas";
    }

    @RequestMapping("/mensajes")
    public String mensajes(HttpServletRequest request) {
        return "mensajes";
    }

    @RequestMapping("/perfil")
    public String perfil(HttpServletRequest request) {
        return "perfil";
    }

    @RequestMapping("/notificaciones")
    public String notificaciones(HttpServletRequest request) {
        return "notificaciones";
    }

    @RequestMapping("/soporte")
    public String soporte(HttpServletRequest request) {
        return "soporte";
    }
}
