package com.justjava.ecommerce.product;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/vendor/images")
@PreAuthorize("hasRole('VENDOR')")
public class ImageUploadController {

    private static final Set<String> ALLOWED_TYPES = Set.of("image/jpeg", "image/png", "image/webp", "image/gif");
    private static final long MAX_SIZE = 10 * 1024 * 1024;

    private final Path uploadDir;

    public ImageUploadController(@Value("${app.upload-dir:./uploads/products}") String uploadPath) {
        this.uploadDir = Paths.get(uploadPath).toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.uploadDir);
        } catch (IOException e) {
            throw new IllegalStateException("Could not create upload directory: " + uploadPath, e);
        }
    }

    @PostMapping("/upload")
    public ResponseEntity<?> upload(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "No file provided"));
        }
        if (file.getSize() > MAX_SIZE) {
            return ResponseEntity.badRequest().body(Map.of("error", "File exceeds 10MB limit"));
        }
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_TYPES.contains(contentType)) {
            return ResponseEntity.badRequest().body(Map.of("error", "Only JPEG, PNG, WebP, and GIF images are allowed"));
        }

        try {
            String ext = getExtension(file.getOriginalFilename());
            String filename = UUID.randomUUID() + ext;
            Path target = uploadDir.resolve(filename).normalize();

            if (!target.startsWith(uploadDir)) {
                return ResponseEntity.badRequest().body(Map.of("error", "Invalid filename"));
            }

            file.transferTo(target);
            String url = "/uploads/products/" + filename;
            log.info("Uploaded product image: {}", url);
            return ResponseEntity.ok(Map.of("url", url));
        } catch (IOException e) {
            log.error("Failed to save uploaded file", e);
            return ResponseEntity.internalServerError().body(Map.of("error", "Upload failed"));
        }
    }

    private String getExtension(String filename) {
        if (filename == null) return ".jpg";
        int dot = filename.lastIndexOf('.');
        return dot >= 0 ? filename.substring(dot).toLowerCase() : ".jpg";
    }
}
