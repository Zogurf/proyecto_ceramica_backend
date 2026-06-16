package com.example.backend.services;

import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.example.backend.models.Persona;
import com.example.backend.models.Product;
import com.example.backend.models.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PdfExportService {

    // Convertir lista de Personas a PDF
    public byte[] exportPersonasToPdf(List<Persona> personas) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(outputStream);
        PdfDocument pdfDoc = new PdfDocument(writer);
        Document document = new Document(pdfDoc);

        // Título
        document.add(new Paragraph("Reporte de Personas")
                .setFontSize(18)
                .setBold()
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(20));

        // Tabla
        float[] columnWidths = {50, 100, 80, 100, 100, 100, 100};
        Table table = new Table(UnitValue.createPercentArray(columnWidths));
        table.setWidth(UnitValue.createPercentValue(100));

        // Headers
        String[] headers = {"ID", "Nombre", "DNI", "Nombre", "Apellido P.", "Apellido M.", "Nacimiento"};
        for (String header : headers) {
            Cell cell = new Cell()
                    .add(new Paragraph(header))
                    .setBackgroundColor(new DeviceRgb(0, 0, 255))
                    .setFontColor(new DeviceRgb(255, 255, 255))
                    .setBold();
            table.addHeaderCell(cell);
        }

        // Datos
        for (Persona persona : personas) {
            table.addCell(new Cell().add(new Paragraph(String.valueOf(persona.getId()))));
            table.addCell(new Cell().add(new Paragraph(persona.getName() != null ? persona.getName() : "")));
            table.addCell(new Cell().add(new Paragraph(persona.getDni() != null ? persona.getDni() : "")));
            table.addCell(new Cell().add(new Paragraph(persona.getFirstName() != null ? persona.getFirstName() : "")));
            table.addCell(new Cell().add(new Paragraph(persona.getLastName() != null ? persona.getLastName() : "")));
            table.addCell(new Cell().add(new Paragraph(persona.getMotherLastName() != null ? persona.getMotherLastName() : "")));
            table.addCell(new Cell().add(new Paragraph(persona.getBirthDate() != null ? persona.getBirthDate() : "")));
        }

        document.add(table);
        document.close();

        return outputStream.toByteArray();
    }

    // Convertir lista de Usuarios a PDF
    public byte[] exportUsersToPdf(List<User> users) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(outputStream);
        PdfDocument pdfDoc = new PdfDocument(writer);
        Document document = new Document(pdfDoc);

        // Título
        document.add(new Paragraph("Reporte de Usuarios")
                .setFontSize(18)
                .setBold()
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(20));

        // Tabla
        float[] columnWidths = {50, 120, 120, 100, 80, 100};
        Table table = new Table(UnitValue.createPercentArray(columnWidths));
        table.setWidth(UnitValue.createPercentValue(100));

        // Headers
        String[] headers = {"ID", "Nombre", "Email", "Rol", "DNI", "Nacimiento"};
        for (String header : headers) {
            Cell cell = new Cell()
                    .add(new Paragraph(header))
                    .setBackgroundColor(new DeviceRgb(0, 0, 255))  // ✅ CORREGIDO
                    .setFontColor(new DeviceRgb(255, 255, 255))    // ✅ CORREGIDO
                    .setBold();
            table.addHeaderCell(cell);
        }

        // Datos
        for (User user : users) {
            table.addCell(new Cell().add(new Paragraph(String.valueOf(user.getId()))));
            table.addCell(new Cell().add(new Paragraph(
                    user.getPersona() != null && user.getPersona().getName() != null ? user.getPersona().getName() : "")));
            table.addCell(new Cell().add(new Paragraph(user.getEmail() != null ? user.getEmail() : "")));
            table.addCell(new Cell().add(new Paragraph(
                    user.getRole() != null ? user.getRole().getName() : "")));
            table.addCell(new Cell().add(new Paragraph(
                    user.getPersona() != null && user.getPersona().getDni() != null ? user.getPersona().getDni() : "")));
            table.addCell(new Cell().add(new Paragraph(
                    user.getPersona() != null && user.getPersona().getBirthDate() != null ? user.getPersona().getBirthDate() : "")));
        }

        document.add(table);
        document.close();

        return outputStream.toByteArray();
    }

    // Convertir lista de Productos a PDF
    public byte[] exportProductsToPdf(List<Product> products) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(outputStream);
        PdfDocument pdfDoc = new PdfDocument(writer);
        Document document = new Document(pdfDoc);

        // Título
        document.add(new Paragraph("Reporte de Productos")
                .setFontSize(18)
                .setBold()
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(20));

        // Tabla
        float[] columnWidths = {50, 100, 80, 80, 80, 100, 100};
        Table table = new Table(UnitValue.createPercentArray(columnWidths));
        table.setWidth(UnitValue.createPercentValue(100));

        // Headers
        String[] headers = {"ID", "Nombre", "Precio", "Stock", "Estado", "Imagen", "Categoría"};
        for (String header : headers) {
            Cell cell = new Cell()
                    .add(new Paragraph(header))
                    .setBackgroundColor(new DeviceRgb(0, 0, 255))  // ✅ CORREGIDO
                    .setFontColor(new DeviceRgb(255, 255, 255))    // ✅ CORREGIDO
                    .setBold();
            table.addHeaderCell(cell);
        }

        // Datos
        for (Product product : products) {
            table.addCell(new Cell().add(new Paragraph(String.valueOf(product.getId()))));
            table.addCell(new Cell().add(new Paragraph(product.getName() != null ? product.getName() : "")));
            table.addCell(new Cell().add(new Paragraph(String.format("S/. %.2f", product.getPrice()))));
            table.addCell(new Cell().add(new Paragraph(String.valueOf(product.getStock()))));
            table.addCell(new Cell().add(new Paragraph(product.isStatus() ? "Activo" : "Inactivo")));
            table.addCell(new Cell().add(new Paragraph(product.getImageUrl() != null ? product.getImageUrl() : "")));
            table.addCell(new Cell().add(new Paragraph(
                    product.getCategory() != null ? product.getCategory().getName() : "")));
        }

        document.add(table);
        document.close();

        return outputStream.toByteArray();
    }
}