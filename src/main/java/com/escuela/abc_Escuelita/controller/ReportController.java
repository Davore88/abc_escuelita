package com.escuela.abc_Escuelita.controller;

import com.escuela.abc_Escuelita.model.AccessLog;
import com.escuela.abc_Escuelita.model.Student;
import com.escuela.abc_Escuelita.repository.AccessLogRepository;
import com.escuela.abc_Escuelita.repository.StudentRepository;
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.awt.Color;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
public class ReportController {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private AccessLogRepository accessLogRepository;

    @GetMapping("/reports")
    public String reportsIndex(Model model) {
        Long institutionId = 1L; // Hardcoded for demo as in StudentController
        List<Student> students = studentRepository.findByInstitutionId(institutionId);
        model.addAttribute("students", students);
        model.addAttribute("title", "Reportes de Asistencia");
        return "reports/index";
    }

    @PostMapping("/reports/download")
    public void downloadReport(
            @RequestParam Long studentId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            HttpServletResponse response) throws IOException {

        Student student = studentRepository.findById(studentId).orElse(null);
        if (student == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Student not found");
            return;
        }

        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);

        List<AccessLog> logs = accessLogRepository.findByStudentIdAndTimestampBetweenOrderByTimestampAsc(studentId, startDateTime, endDateTime);

        response.setContentType("application/pdf");
        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=reporte_asistencia_" + studentId + ".pdf";
        response.setHeader(headerKey, headerValue);

        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, response.getOutputStream());

        document.open();

        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, Color.BLACK);
        Font subTitleFont = FontFactory.getFont(FontFactory.HELVETICA, 12, Color.DARK_GRAY);
        Font tableHeaderFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, Color.WHITE);
        Font tableBodyFont = FontFactory.getFont(FontFactory.HELVETICA, 11, Color.BLACK);

        Paragraph title = new Paragraph("Reporte de Asistencia Semanal", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(10);
        document.add(title);

        Paragraph studentInfo = new Paragraph("Alumno: " + student.getFirstName() + " " + student.getLastName(), subTitleFont);
        studentInfo.setAlignment(Element.ALIGN_CENTER);
        document.add(studentInfo);

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        Paragraph dateInfo = new Paragraph("Período: " + startDate.format(dateFormatter) + " al " + endDate.format(dateFormatter), subTitleFont);
        dateInfo.setAlignment(Element.ALIGN_CENTER);
        dateInfo.setSpacingAfter(20);
        document.add(dateInfo);

        PdfPTable table = new PdfPTable(4);
        table.setWidthPercentage(100);
        table.setSpacingBefore(10f);
        table.setSpacingAfter(10f);
        
        try {
            table.setWidths(new float[]{2f, 2f, 2f, 4f});
        } catch (Exception e) {
            e.printStackTrace();
        }

        PdfPCell cell = new PdfPCell();
        cell.setBackgroundColor(new Color(22, 163, 74)); // Tailwind brand-600
        cell.setPadding(8);

        cell.setPhrase(new Phrase("Fecha", tableHeaderFont));
        table.addCell(cell);
        
        cell.setPhrase(new Phrase("Hora", tableHeaderFont));
        table.addCell(cell);

        cell.setPhrase(new Phrase("Acción", tableHeaderFont));
        table.addCell(cell);

        cell.setPhrase(new Phrase("Tutor Responsable", tableHeaderFont));
        table.addCell(cell);

        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");

        for (AccessLog log : logs) {
            PdfPCell bodyCell = new PdfPCell(new Phrase(log.getTimestamp().format(dateFormatter), tableBodyFont));
            bodyCell.setPadding(6);
            table.addCell(bodyCell);
            
            bodyCell = new PdfPCell(new Phrase(log.getTimestamp().format(timeFormatter), tableBodyFont));
            bodyCell.setPadding(6);
            table.addCell(bodyCell);

            String actionText = log.getAccessType().equals("ENTRY") ? "Entrada" : "Salida";
            bodyCell = new PdfPCell(new Phrase(actionText, tableBodyFont));
            bodyCell.setPadding(6);
            table.addCell(bodyCell);

            String tutorName = log.getTutor() != null ? log.getTutor().getFirstName() + " " + log.getTutor().getLastName() : "N/A";
            bodyCell = new PdfPCell(new Phrase(tutorName, tableBodyFont));
            bodyCell.setPadding(6);
            table.addCell(bodyCell);
        }

        if (logs.isEmpty()) {
            PdfPCell emptyCell = new PdfPCell(new Phrase("No hay registros en este período", tableBodyFont));
            emptyCell.setColspan(4);
            emptyCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            emptyCell.setPadding(10);
            table.addCell(emptyCell);
        }

        document.add(table);
        document.close();
    }
}
