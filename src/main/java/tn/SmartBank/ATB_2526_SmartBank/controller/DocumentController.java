package tn.SmartBank.ATB_2526_SmartBank.controller;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;

@RestController
@RequestMapping("/api/documents")
public class DocumentController {

    private static final Path DOCUMENTS_DIR = Path.of("C:\\Users\\Houssein\\OneDrive\\Documents\\stage doc\\ATB-2526-SmartBank\\ATB-2526-SmartBank-Backend\\src\\main\\java\\tn\\SmartBank\\ATB_2526_SmartBank\\Documents");

    @GetMapping("/{fileName}")
    public ResponseEntity<Resource> preview(@PathVariable String fileName) throws MalformedURLException {
        Path file = DOCUMENTS_DIR.resolve(fileName).normalize();
        if (!file.startsWith(DOCUMENTS_DIR) || !Files.exists(file)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        Resource resource = new UrlResource(file.toUri());
        String contentType = "application/octet-stream";
        try {
            String detected = Files.probeContentType(file);
            if (detected != null) {
                contentType = detected;
            }
        } catch (Exception ignored) {
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + file.getFileName() + "\"")
                .body(resource);
    }
}
