package com.auditoria.proyecto_ctf.api.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

@RestController
public class PhpController {

    @GetMapping("/execute-php")
    public String executePhp(@RequestParam String scriptPath) {
        ProcessBuilder processBuilder = new ProcessBuilder("php", scriptPath);
        processBuilder.redirectErrorStream(true);
        StringBuilder output = new StringBuilder();

        try {
            Process process = processBuilder.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                return "Error: PHP script exited with code " + exitCode;
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return "Error: " + e.getMessage();
        }
        return output.toString();
    }
}
