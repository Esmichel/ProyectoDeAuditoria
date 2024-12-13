package com.auditoria.proyecto_ctf.api.controllers.error;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpServletRequest;
//TODO
/*@Controller
public class ErrorController implements org.springframework.boot.web.servlet.error.ErrorController {

    @RequestMapping("/error")
    public String handleError(HttpServletRequest request) {
        // Handle the error. You can check the status code for more granular control
        Integer statusCode = (Integer) request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        
        // If the error is 404 (Page Not Found), redirect to /dashboard
        if (statusCode != null && statusCode == HttpStatus.NOT_FOUND.value()) {
            return "redirect:/dashboard";
        }
        
        // For other errors, you can return a generic error page or a different redirect
        return "redirect:/error"; // Or another fallback page
    }

    @Override
    public String getErrorPath() {
        return "/error"; // The path for error handling
    }
}*/
