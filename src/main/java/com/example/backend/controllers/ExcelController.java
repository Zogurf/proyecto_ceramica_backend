package com.example.backend.controllers;

import com.example.backend.repositories.PersonaRepository;
import com.example.backend.repositories.ProductRepository;
import com.example.backend.repositories.UserRepository;
import com.example.backend.services.ExcelExportService;
import com.example.backend.services.PdfExportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/api/excel")
@CrossOrigin(origins = {"http://localhost:3000", "http://127.0.0.1:3000"})
@RequiredArgsConstructor
public class ExcelController {
    private final PersonaRepository personaRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final ExcelExportService excelExportService;
    private final PdfExportService pdfExportService;

    @GetMapping("/personas")
    public ResponseEntity<byte[]> exportPersonasExcel() {
        try {
            byte[] excelFile = excelExportService.exportPersonasToExcel(personaRepository.findAll());
            return buildDownloadResponse(excelFile, "personas_" + System.currentTimeMillis() + ".xlsx");
        } catch (IOException ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/products")
    public ResponseEntity<byte[]> exportProductsExcel() {
        try {
            byte[] excelFile = excelExportService.exportProductsToExcel(productRepository.findAll());
            return buildDownloadResponse(excelFile, "productos_" + System.currentTimeMillis() + ".xlsx");
        } catch (IOException ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/users")
    public ResponseEntity<byte[]> exportUsersExcel() {
        try {
            byte[] excelFile = excelExportService.exportUsersToExcel(userRepository.findAll());
            return buildDownloadResponse(excelFile, "usuarios_" + System.currentTimeMillis() + ".xlsx");
        } catch (IOException ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/pdf/personas")
    public ResponseEntity<byte[]> exportPersonasPdf() {
        try {
            byte[] pdfFile = pdfExportService.exportPersonasToPdf(personaRepository.findAll());
            return buildDownloadResponse(pdfFile, "personas_" + System.currentTimeMillis() + ".pdf");
        } catch (IOException ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/pdf/products")
    public ResponseEntity<byte[]> exportProductsPdf() {
        try {
            byte[] pdfFile = pdfExportService.exportProductsToPdf(productRepository.findAll());
            return buildDownloadResponse(pdfFile, "productos_" + System.currentTimeMillis() + ".pdf");
        } catch (IOException ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/pdf/users")
    public ResponseEntity<byte[]> exportUsersPdf() {
        try {
            byte[] pdfFile = pdfExportService.exportUsersToPdf(userRepository.findAll());
            return buildDownloadResponse(pdfFile, "usuarios_" + System.currentTimeMillis() + ".pdf");
        } catch (IOException ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    private ResponseEntity<byte[]> buildDownloadResponse(byte[] file, String filename) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename);
        return ResponseEntity.ok().headers(headers).body(file);
    }
}
