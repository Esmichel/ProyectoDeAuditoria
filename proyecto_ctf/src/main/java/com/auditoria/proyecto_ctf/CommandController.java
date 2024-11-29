package com.auditoria.proyecto_ctf;

import org.springframework.web.bind.annotation.*;
import java.io.*;

@RestController
@RequestMapping("/command")
public class CommandController {

    @PostMapping
    public String executeCommand(@RequestParam("cmd") String command) {
        StringBuilder output = new StringBuilder();

        try {
            Process process = Runtime.getRuntime().exec(command);
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()));

            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }
        } catch (IOException e) {
            return "Error ejecutando comando: " + e.getMessage();
        }

        return output.toString();
    }
}
