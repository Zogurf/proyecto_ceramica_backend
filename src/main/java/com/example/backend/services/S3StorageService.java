package com.example.backend.services;

import com.example.backend.dto.UploadImageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.text.Normalizer;
import java.time.Instant;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class S3StorageService {
    private static final long MAX_IMAGE_SIZE = 5 * 1024 * 1024;
    private static final Set<String> ALLOWED_TYPES = Set.of("image/jpeg", "image/png", "image/webp", "image/gif");

    @Value("${aws.bucket-name:}")
    private String bucketName;

    @Value("${aws.region:us-east-2}")
    private String region;

    @Value("${aws.access-key:}")
    private String accessKey;

    @Value("${aws.secret-key:}")
    private String secretKey;

    public UploadImageResponse uploadImage(MultipartFile file, String folder) {
        validate(file);
        ensureConfigured();

        String safeFolder = sanitizeFolder(folder);
        String extension = getExtension(file.getOriginalFilename(), file.getContentType());
        String key = safeFolder + "/" + Instant.now().toEpochMilli() + "-" + UUID.randomUUID() + extension;

        try (S3Client s3 = buildClient()) {
            PutObjectRequest request = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .contentType(file.getContentType())
                    .cacheControl("public, max-age=31536000")
                    .build();

            s3.putObject(request, RequestBody.fromBytes(file.getBytes()));
        } catch (IOException ex) {
            throw new RuntimeException("No se pudo leer la imagen", ex);
        }

        return new UploadImageResponse(publicUrl(key), key);
    }

    private S3Client buildClient() {
        AwsBasicCredentials credentials = AwsBasicCredentials.create(accessKey, secretKey);
        return S3Client.builder()
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .build();
    }

    private void validate(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new RuntimeException("No se envio una imagen");
        }
        if (file.getSize() > MAX_IMAGE_SIZE) {
            throw new RuntimeException("La imagen no puede superar los 5 MB");
        }
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_TYPES.contains(contentType.toLowerCase(Locale.ROOT))) {
            throw new RuntimeException("Solo se permiten imagenes JPG, PNG, WEBP o GIF");
        }
    }

    private void ensureConfigured() {
        if (!StringUtils.hasText(bucketName) || !StringUtils.hasText(region)
                || !StringUtils.hasText(accessKey) || !StringUtils.hasText(secretKey)) {
            throw new RuntimeException("AWS S3 no esta configurado");
        }
    }

    private String sanitizeFolder(String folder) {
        String value = StringUtils.hasText(folder) ? folder : "productos";
        value = Normalizer.normalize(value, Normalizer.Form.NFD).replaceAll("\\p{M}", "");
        value = value.toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9/_-]", "-");
        value = value.replaceAll("-+", "-").replaceAll("^/|/$", "");
        return StringUtils.hasText(value) ? value : "productos";
    }

    private String getExtension(String filename, String contentType) {
        if (StringUtils.hasText(filename) && filename.contains(".")) {
            String ext = filename.substring(filename.lastIndexOf(".")).toLowerCase(Locale.ROOT);
            if (ext.matches("\\.(jpg|jpeg|png|webp|gif)")) {
                return ext;
            }
        }
        return switch (contentType == null ? "" : contentType.toLowerCase(Locale.ROOT)) {
            case "image/png" -> ".png";
            case "image/webp" -> ".webp";
            case "image/gif" -> ".gif";
            default -> ".jpg";
        };
    }

    private String publicUrl(String key) {
        return "https://" + bucketName + ".s3." + region + ".amazonaws.com/" + key;
    }
}
