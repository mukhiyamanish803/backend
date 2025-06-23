package com.manish.employara.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FileStorageService {
    private final Cloudinary cloudinary;

    public String uploadFile(MultipartFile file, String dir) {
        try {
            Map<?, ?> uploadResult = cloudinary.uploader().upload(
                    file.getBytes(),
                    ObjectUtils.asMap(
                            "folder", dir, // Removed "employara/" prefix to avoid path duplication
                            "resource_type", "auto"));
            return (String) uploadResult.get("secure_url");
        } catch (IOException e) {
            throw new RuntimeException("Failed to upload file", e);
        }
    }

    public void deleteFromCloudinary(String fileUrl) {
        try {
            if (fileUrl != null && !fileUrl.isEmpty()) {
                // Extract public ID from URL
                String[] urlParts = fileUrl.split("/");
                String publicId = null;
                for (int i = 0; i < urlParts.length; i++) {
                    if (urlParts[i].equals("upload")) {
                        // Combine all parts after "upload" except the file name
                        List<String> idParts = new ArrayList<>();
                        for (int j = i + 2; j < urlParts.length - 1; j++) {
                            idParts.add(urlParts[j]);
                        }
                        // Add filename without extension
                        String fileName = urlParts[urlParts.length - 1];
                        int dotIndex = fileName.lastIndexOf('.');
                        if (dotIndex > 0) {
                            fileName = fileName.substring(0, dotIndex);
                        }
                        idParts.add(fileName);
                        publicId = String.join("/", idParts);
                        break;
                    }
                }
                if (publicId != null) {
                    cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete file from Cloudinary", e);
        }
    }
}
