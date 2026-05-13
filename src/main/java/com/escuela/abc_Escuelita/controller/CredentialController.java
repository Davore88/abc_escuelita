package com.escuela.abc_Escuelita.controller;

import com.escuela.abc_Escuelita.model.Student;
import com.escuela.abc_Escuelita.model.Tutor;
import com.escuela.abc_Escuelita.repository.TutorRepository;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.Image;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.ColumnText;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfWriter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Controller
@RequestMapping("/credentials")
public class CredentialController {

    @Autowired
    private TutorRepository tutorRepository;

    @GetMapping("/pdf/{tutorId}")
    public void generatePdfCredential(@PathVariable Long tutorId, HttpServletRequest request, HttpServletResponse response) throws IOException {
        Tutor tutor = tutorRepository.findById(tutorId).orElse(null);
        if (tutor == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        response.setContentType("application/pdf");
        String headerKey = "Content-Disposition";
        String headerValue = "inline; filename=credencial_tutor_" + tutorId + ".pdf";
        response.setHeader(headerKey, headerValue);

        float width = 240f;
        float height = 380f;
        Rectangle pagesize = new Rectangle(width, height);
        Document document = new Document(pagesize, 0, 0, 0, 0);
        
        try {
            PdfWriter writer = PdfWriter.getInstance(document, response.getOutputStream());
            document.open();
            
            PdfContentByte cb = writer.getDirectContent();
            
            // --- FRONT PAGE ---
            drawWaves(cb, width, height);
            
            BaseFont helveticaBold = BaseFont.createFont(BaseFont.HELVETICA_BOLD, BaseFont.CP1252, BaseFont.NOT_EMBEDDED);
            BaseFont helvetica = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.CP1252, BaseFont.NOT_EMBEDDED);
            BaseFont timesItalic = BaseFont.createFont(BaseFont.TIMES_ITALIC, BaseFont.CP1252, BaseFont.NOT_EMBEDDED);
            Color darkBlue = new Color(16, 53, 91);
            
            // Header
            cb.beginText();
            cb.setFontAndSize(helveticaBold, 16);
            cb.setColorFill(Color.BLACK);
            String headerTxt = "Escuela Demo";
            float hWidth = helveticaBold.getWidthPoint(headerTxt, 16);
            cb.setTextMatrix((width - hWidth) / 2, 325);
            cb.showText(headerTxt);
            cb.endText();
            
            // Photo
            if (tutor.getPhotoData() != null) {
                try {
                    Image img = Image.getInstance(tutor.getPhotoData());
                    float photoSize = 110f;
                    float photoX = (width - photoSize) / 2;
                    float photoY = 195f;
                    
                    cb.saveState();
                    cb.roundRectangle(photoX, photoY, photoSize, photoSize, 15f);
                    cb.clip();
                    cb.newPath();
                    
                    img.scaleAbsolute(photoSize, photoSize);
                    img.setAbsolutePosition(photoX, photoY);
                    cb.addImage(img);
                    
                    cb.restoreState();
                    
                    // Photo Border
                    cb.saveState();
                    cb.setColorStroke(darkBlue);
                    cb.setLineWidth(3f);
                    cb.roundRectangle(photoX, photoY, photoSize, photoSize, 15f);
                    cb.stroke();
                    cb.restoreState();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                // Placeholder
                cb.saveState();
                cb.setColorStroke(darkBlue);
                cb.setLineWidth(2f);
                cb.roundRectangle((width - 110f) / 2, 195f, 110f, 110f, 15f);
                cb.stroke();
                cb.beginText();
                cb.setFontAndSize(helvetica, 12);
                cb.setColorFill(darkBlue);
                float pWidth = helvetica.getWidthPoint("[Sin Foto]", 12);
                cb.setTextMatrix((width - pWidth) / 2, 245f);
                cb.showText("[Sin Foto]");
                cb.endText();
                cb.restoreState();
            }
            
            // Name
            cb.beginText();
            cb.setFontAndSize(timesItalic, 18);
            cb.setColorFill(darkBlue);
            String tutorName = tutor.getFirstName() + " " + tutor.getLastName();
            float nWidth = timesItalic.getWidthPoint(tutorName, 18);
            cb.setTextMatrix((width - nWidth) / 2, 175);
            cb.showText(tutorName);
            cb.endText();
            
            // Role
            String studentName = "Alumno";
            if (tutor.getStudents() != null && !tutor.getStudents().isEmpty()) {
                Student firstStudent = tutor.getStudents().get(0);
                studentName = firstStudent.getFirstName() + " " + firstStudent.getLastName();
            }
            
            ColumnText ct = new ColumnText(cb);
            ct.setSimpleColumn(20, 130, 220, 170);
            Font roleFont = new Font(helveticaBold, 12, Font.NORMAL, darkBlue);
            Paragraph p = new Paragraph("Tutor de: " + studentName, roleFont);
            p.setAlignment(Element.ALIGN_CENTER);
            ct.addElement(p);
            ct.go();
            
            // QR Code
            String baseUrl = ServletUriComponentsBuilder.fromRequestUri(request)
                    .replacePath(null)
                    .build()
                    .toUriString();
            String verifyUrl = baseUrl + "/credentials/verify/" + tutorId;
            
            try {
                QRCodeWriter qrCodeWriter = new QRCodeWriter();
                BitMatrix bitMatrix = qrCodeWriter.encode(verifyUrl, BarcodeFormat.QR_CODE, 100, 100);
                ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();
                MatrixToImageWriter.writeToStream(bitMatrix, "PNG", pngOutputStream);
                
                Image qrImage = Image.getInstance(pngOutputStream.toByteArray());
                float qrSize = 90f;
                qrImage.scaleAbsolute(qrSize, qrSize);
                qrImage.setAbsolutePosition((width - qrSize) / 2, 40f);
                cb.addImage(qrImage);
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            // --- BACK PAGE ---
            document.newPage();
            drawWaves(cb, width, height);
            
            // Header
            cb.beginText();
            cb.setFontAndSize(helveticaBold, 18);
            cb.setColorFill(Color.BLACK);
            String backHeader = "Borcelle";
            float bhWidth = helveticaBold.getWidthPoint(backHeader, 18);
            cb.setTextMatrix((width - bhWidth) / 2, 310);
            cb.showText(backHeader);
            cb.endText();
            
            // Contact info
            String tPhone = "911-234-5678";
            String tEmail = tutor.getEmail() != null && !tutor.getEmail().isEmpty() ? tutor.getEmail() : "hola@unsitiogenial.es";
            String tWeb = "www.unsitiogenial.es";
            
            cb.beginText();
            cb.setFontAndSize(helvetica, 12);
            cb.setColorFill(darkBlue);
            
            float w1 = helvetica.getWidthPoint(tPhone, 12);
            cb.setTextMatrix((width - w1) / 2, 240);
            cb.showText(tPhone);
            
            float w2 = helvetica.getWidthPoint(tEmail, 12);
            cb.setTextMatrix((width - w2) / 2, 190);
            cb.showText(tEmail);
            
            float w3 = helvetica.getWidthPoint(tWeb, 12);
            cb.setTextMatrix((width - w3) / 2, 140);
            cb.showText(tWeb);
            cb.endText();
            
            // Footer
            cb.beginText();
            cb.setFontAndSize(timesItalic, 16);
            cb.setColorFill(darkBlue);
            String footerTxt = "Feria de negocio";
            float fWidth = timesItalic.getWidthPoint(footerTxt, 16);
            cb.setTextMatrix((width - fWidth) / 2, 80);
            cb.showText(footerTxt);
            cb.endText();
            
            document.close();
            
        } catch (Exception ex) {
            ex.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
    
    private void drawWaves(PdfContentByte cb, float width, float height) {
        Color lightBlue = new Color(216, 239, 251);
        Color darkBlue = new Color(16, 53, 91);
        Color mediumBlue = new Color(127, 180, 213);
        Color darkGreyBlue = new Color(74, 98, 120);

        // Background
        cb.setColorFill(lightBlue);
        cb.rectangle(0, 0, width, height);
        cb.fill();

        // Top Wave 1 (Medium Blue)
        cb.setColorFill(mediumBlue);
        cb.moveTo(0, height);
        cb.lineTo(0, height - 30);
        cb.curveTo(width * 0.3f, height - 45, width * 0.7f, height - 10, width, height - 35);
        cb.lineTo(width, height);
        cb.fill();

        // Top Wave 2 (Dark Blue)
        cb.setColorFill(darkBlue);
        cb.moveTo(0, height);
        cb.lineTo(0, height - 15);
        cb.curveTo(width * 0.4f, height - 40, width * 0.6f, height + 10, width, height - 25);
        cb.lineTo(width, height);
        cb.fill();

        // Bottom Wave 1 (Dark Grey Blue)
        cb.setColorFill(darkGreyBlue);
        cb.moveTo(0, 0);
        cb.lineTo(0, 40);
        cb.curveTo(width * 0.3f, 10, width * 0.7f, 50, width, 20);
        cb.lineTo(width, 0);
        cb.fill();

        // Bottom Wave 2 (Medium Blue)
        cb.setColorFill(mediumBlue);
        cb.moveTo(0, 0);
        cb.lineTo(0, 30);
        cb.curveTo(width * 0.4f, 5, width * 0.6f, 40, width, 10);
        cb.lineTo(width, 0);
        cb.fill();

        // Bottom Wave 3 (Dark Blue)
        cb.setColorFill(darkBlue);
        cb.moveTo(0, 0);
        cb.lineTo(0, 20);
        cb.curveTo(width * 0.3f, -5, width * 0.7f, 30, width, 5);
        cb.lineTo(width, 0);
        cb.fill();
    }

    @GetMapping("/verify/{tutorId}")
    public String verifyCredential(@PathVariable Long tutorId, Model model) {
        Tutor tutor = tutorRepository.findById(tutorId).orElse(null);
        if (tutor == null) {
            return "error/404";
        }
        model.addAttribute("tutor", tutor);
        return "credentials/verify";
    }
}
