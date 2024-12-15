package com.auditoria.proyecto_ctf.api.controllers.dashboard;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.auditoria.proyecto_ctf.api.dto.users.JwtAuthResponse;
import com.auditoria.proyecto_ctf.api.dto.users.LoginDto;
import com.auditoria.proyecto_ctf.application.authentication.AuthService;

import org.springframework.http.MediaType;

import ch.qos.logback.core.model.Model;
import io.jsonwebtoken.io.IOException;
import io.jsonwebtoken.lang.Arrays;
import jakarta.annotation.Resource;
import jakarta.persistence.criteria.Path;

import org.springframework.core.io.FileSystemResource;

@RestController
@RequestMapping("/api/dashboard")
public class UploadController {

    @Value("${uploades.imagenes.windows}")
    private String UPLOAD_DIR;

    @PostMapping("/upload")
    public ResponseEntity<String> uploadImage(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("No file selected");
        }

        File directory = new File(UPLOAD_DIR);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        // C:\Users\ander\Desktop\Master\Auditoria\Proyecto
        // Final\ProcectoAuditoriaCodigo\ProyectoDeAuditoria
        String filename = file.getOriginalFilename();
        File destinationFile = new File(UPLOAD_DIR + filename);

        try {
            file.transferTo(destinationFile);
            return ResponseEntity.ok("File uploaded successfully: " + filename);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Failed to upload the file");
        } catch (IllegalStateException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (java.io.IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return ResponseEntity.status(200).body("File upload");
    }

    @GetMapping("/images")
    public ResponseEntity<List<String>> getUploadedImages() {
        // Obtener la carpeta donde se almacenan los archivos
        File folder = new File(UPLOAD_DIR);

        // Listar los archivos en el directorio
        File[] listOfFiles = folder.listFiles();

        // Crear una lista para los nombres de los archivos
        List<String> fileNames = new ArrayList<>();

        // Validar que la carpeta existe y no está vacía
        if (listOfFiles != null) {
            for (File file : listOfFiles) {
                if (file.isFile()) {
                    fileNames.add(file.getName());
                }
            }
        }

        return ResponseEntity.ok(fileNames);
    }

    @GetMapping("/show-image/{filename}")
    @ResponseBody
    public ResponseEntity<Resource> showImage(@PathVariable String filename) throws IOException {
        File imageFile = new File(UPLOAD_DIR + filename);
        if (!imageFile.exists()) {
            return ResponseEntity.notFound().build();
        }

        Resource resource = (Resource) new FileSystemResource(imageFile);
        String contentType = null;
        try {
            contentType = Files.probeContentType(imageFile.toPath());
        } catch (java.io.IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } // Dynamically detect MIME type
        if (filename.endsWith(".png")) {
            contentType = "image/png";
        } else if (filename.endsWith(".jpg") || filename.endsWith(".jpeg")) {
            contentType = "image/jpeg";
        } else if (filename.endsWith(".sh")) {
            contentType = "application/json";
        } else {
            contentType = "application/octet-stream"; // Predeterminado
        }

        // Execute the shell script
        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.command("bash", UPLOAD_DIR + filename); // Use bash to execute the shell script
        processBuilder.inheritIO(); // Optionally inherit the I/O of the current process

        try {
            Process process = processBuilder.start();
        } catch (java.io.IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType)) // Set the correct MIME type
                .body(resource);
    }

    /*@GetMapping("/execute/{filename}")
    public String executeFile(@PathVariable String filename) throws IOException, InterruptedException {
        // Check if file exists
        File fileToExecute = new File(UPLOAD_DIR + filename);
        if (!fileToExecute.exists()) {
            return "File not found.";
        }

        // Execute the shell script (be careful!)
        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.command("bash", fileToExecute.getAbsolutePath());

        // Redirect error stream to the output stream
        processBuilder.redirectErrorStream(true);

        // Start the process
        int exitCode = 0;
        StringBuilder output = new StringBuilder();
        try {
            Process process = processBuilder.start();

            // Capture the output of the process (both stdout and stderr)
            InputStreamReader inputStreamReader = new InputStreamReader(process.getInputStream());
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line;

            // Read each line of the output
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }

            exitCode = process.waitFor();
            System.out.println("Output of execution:\n" + output.toString());
        } catch (java.io.IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        // Return execution result
        if (exitCode == 0) {
            return "File executed successfully!";
        } else {
            return "Error executing file!";
        }
    }*/

    @GetMapping("/execute/{filename}")
    public String executeFile(@PathVariable String filename) throws IOException, InterruptedException, java.io.IOException {
        // Verificar si el archivo existe

        File fileToExecute = new File(UPLOAD_DIR + filename);
        if (!fileToExecute.exists()) {
            return "File not found.";
        }

        // Obtener la extensión del archivo
        String fileExtension = getFileExtension(filename);

        // Si es una imagen, mostrarla en la interfaz web
        if (isImage(fileExtension)) {
            return "<img src='/uploads/" + filename + "' alt='Image' />";
        }

        // Si es un archivo PHP, ejecutar el archivo
        if (fileExtension.equals("php")) {
            // Configurar el proceso para ejecutar el archivo PHP
            ProcessBuilder processBuilder = new ProcessBuilder();
            processBuilder.command("php", fileToExecute.getAbsolutePath());

            // Redirigir el error al flujo de salida
            processBuilder.redirectErrorStream(true);

            // Iniciar el proceso
            int exitCode = 0;
            StringBuilder output = new StringBuilder();
            try {
                Process process = processBuilder.start();

                // Capturar la salida del proceso (tanto stdout como stderr)
                InputStreamReader inputStreamReader = new InputStreamReader(process.getInputStream());
                BufferedReader reader = new BufferedReader(inputStreamReader);
                String line;

                // Leer cada línea de la salida
                while ((line = reader.readLine()) != null) {
                    output.append(line).append("\n");
                }

                exitCode = process.waitFor();
                System.out.println("Output of PHP execution:\n" + output.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }

            // Retornar el resultado de la ejecución del PHP
            if (exitCode == 0) {
                return "PHP file executed successfully!";
            } else {
                return "Error executing PHP file!";
            }
        }

        // Si es un archivo shell script (.sh), ejecutar el archivo
        if (fileExtension.equals("sh")) {
            // Configurar el proceso para ejecutar el archivo shell script
            ProcessBuilder processBuilder = new ProcessBuilder();
            processBuilder.command("bash", fileToExecute.getAbsolutePath());

            // Redirigir el error al flujo de salida
            processBuilder.redirectErrorStream(true);

            // Iniciar el proceso
            int exitCode = 0;
            StringBuilder output = new StringBuilder();
            try {
                Process process = processBuilder.start();

                // Capturar la salida del proceso
                InputStreamReader inputStreamReader = new InputStreamReader(process.getInputStream());
                BufferedReader reader = new BufferedReader(inputStreamReader);
                String line;

                // Leer cada línea de la salida
                while ((line = reader.readLine()) != null) {
                    output.append(line).append("\n");
                }

                exitCode = process.waitFor();
                System.out.println("Output of shell script execution:\n" + output.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }

            // Retornar el resultado de la ejecución del script
            if (exitCode == 0) {
                return "Shell script executed successfully!";
            } else {
                return "Error executing shell script!";
            }
        }

        // Si el archivo no es una imagen, PHP ni shell script, retornar un mensaje de
        // errorjava.io.IOException
        return "Unsupported file type!";
    }

    // Método para obtener la extensión del archivo
    private String getFileExtension(String filename) {
        int lastIndexOfDot = filename.lastIndexOf(".");
        if (lastIndexOfDot == -1) {
            return ""; // No tiene extensión
        }
        return filename.substring(lastIndexOfDot + 1).toLowerCase();
    }

    // Método para verificar si el archivo es una imagen
    private boolean isImage(String fileExtension) {
        // Lista de extensiones comunes para imágenes
        return fileExtension.equals("jpg") || fileExtension.equals("jpeg") || fileExtension.equals("png")
                || fileExtension.equals("gif");
    }

}
