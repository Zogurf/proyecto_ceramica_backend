package com.example.backend.services;

import com.example.backend.models.Persona;
import com.example.backend.models.Product;
import com.example.backend.models.User;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExcelExportService {

    //Excel para lista de Personas a Excel
    public byte[] exportPersonasToExcel(List<Persona> personas) throws IOException {
    try (Workbook workbook = new XSSFWorkbook()) {  
        Sheet sheet = workbook.createSheet("Personas");

        // Crear encabezados
        Row headerRow = sheet.createRow(0);
        CellStyle headerStyle = createHeaderStyle(workbook);

        String[] headers = {"ID", "Nombre", "DNI", "Nombre", "Apellido Paterno", "Apellido Materno", "Fecha Nacimiento"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        // Llenar datos
        int rowNum = 1;
        CellStyle dataStyle = createDataStyle(workbook);
        for (Persona persona : personas) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(persona.getId());
            row.createCell(1).setCellValue(persona.getName() != null ? persona.getName() : "");
            row.createCell(2).setCellValue(persona.getDni() != null ? persona.getDni() : "");
            row.createCell(3).setCellValue(persona.getFirstName() != null ? persona.getFirstName() : "");
            row.createCell(4).setCellValue(persona.getLastName() != null ? persona.getLastName() : "");
            row.createCell(5).setCellValue(persona.getMotherLastName() != null ? persona.getMotherLastName() : "");
            row.createCell(6).setCellValue(persona.getBirthDate() != null ? persona.getBirthDate() : "");
            
            for (Cell cell : row) {
                cell.setCellStyle(dataStyle);
            }
        }

        // Ajustar ancho de columnas
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }

        // Convertir a bytes
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        return outputStream.toByteArray();
    }
}

    // Exportar lista de Usuarios a Excel
    public byte[] exportUsersToExcel(List<User> users) throws IOException {
    try (Workbook workbook = new XSSFWorkbook()) {
        Sheet sheet = workbook.createSheet("Usuarios");

        Row headerRow = sheet.createRow(0);
        CellStyle headerStyle = createHeaderStyle(workbook);

        String[] headers = {"ID", "Nombre", "Email", "Rol", "DNI", "Fecha Nacimiento"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        int rowNum = 1;
        CellStyle dataStyle = createDataStyle(workbook);
        for (User user : users) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(user.getId());
            row.createCell(1).setCellValue(user.getPersona() != null && user.getPersona().getName() != null ? user.getPersona().getName() : "");
            row.createCell(2).setCellValue(user.getEmail() != null ? user.getEmail() : "");
            row.createCell(3).setCellValue(user.getRole() != null ? user.getRole().getName() : "");
            row.createCell(4).setCellValue(user.getPersona() != null && user.getPersona().getDni() != null ? user.getPersona().getDni() : "");
            row.createCell(5).setCellValue(user.getPersona() != null && user.getPersona().getBirthDate() != null ? user.getPersona().getBirthDate() : "");

            for (Cell cell : row) {
                cell.setCellStyle(dataStyle);
            }
        }

        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        return outputStream.toByteArray();
    }
}

    // Excel para lista de Productos    
    public byte[] exportProductsToExcel(List<Product> products) throws IOException {
    try (Workbook workbook = new XSSFWorkbook()) {
        Sheet sheet = workbook.createSheet("Productos");

        // Crear encabezados
        Row headerRow = sheet.createRow(0);
        CellStyle headerStyle = createHeaderStyle(workbook);

        String[] headers = {"ID", "Nombre", "Precio", "Stock", "Estado", "URL Imagen", "Categoría"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        // Llenar datos
        int rowNum = 1;
        CellStyle dataStyle = createDataStyle(workbook);
        for (Product product : products) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(product.getId());
            row.createCell(1).setCellValue(product.getName() != null ? product.getName() : "");
            row.createCell(2).setCellValue(product.getPrice().doubleValue());
            row.createCell(3).setCellValue(product.getStock());
            row.createCell(4).setCellValue(product.isStatus() ? "Activo" : "Inactivo");
            row.createCell(5).setCellValue(product.getImageUrl() != null ? product.getImageUrl() : "");
            row.createCell(6).setCellValue(product.getCategory() != null ? product.getCategory().getName() : "");
            
            for (Cell cell : row) {
                cell.setCellStyle(dataStyle);
            }
        }

        // Ajustar ancho de columnas
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }

        // Convertir a bytes
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        return outputStream.toByteArray();
    }
}

    private CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setColor(IndexedColors.WHITE.getIndex());
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.BLUE.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        return style;
    }

    private CellStyle createDataStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setAlignment(HorizontalAlignment.LEFT);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        return style;
    }
}
