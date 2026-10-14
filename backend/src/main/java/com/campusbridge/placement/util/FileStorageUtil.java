package com.campusbridge.placement.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Component
public class FileStorageUtil {

    private final Path uploadDir;

    public FileStorageUtil(@Value("${app.upload.dir:./uploads}") String uploadPath) {
        this.uploadDir = Paths.get(uploadPath).toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.uploadDir);
        } catch (IOException e) {
            throw new RuntimeException("Could not initialize upload directory", e);
        }
    }

    public String storeFile(MultipartFile file, String subDirectory) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("Cannot store empty file.");
        }

        Path targetFolder = this.uploadDir.resolve(subDirectory);
        Files.createDirectories(targetFolder);

        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        String uniqueFileName = UUID.randomUUID().toString() + extension;
        Path targetLocation = targetFolder.resolve(uniqueFileName);

        Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
        return "/uploads/" + subDirectory + "/" + uniqueFileName;
    }

    public byte[] loadFileAsBytes(String filePath) throws IOException {
        Path path = this.uploadDir.resolve(filePath.replace("/uploads/", "")).normalize();
        return Files.readAllBytes(path);
    }
}
