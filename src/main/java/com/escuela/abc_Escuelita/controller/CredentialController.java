package com.escuela.abc_Escuelita.controller;

import com.escuela.abc_Escuelita.model.Tutor;
import com.escuela.abc_Escuelita.repository.TutorRepository;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Image;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Rectangle;
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

        Rectangle pagesize = new Rectangle(240f, 380f);
        Document document = new Document(pagesize, 10f, 10f, 10f, 10f);
        PdfWriter.getInstance(document, response.getOutputStream());

        document.open();

        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14);
        Font nameFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);
        Font labelFont = FontFactory.getFont(FontFactory.HELVETICA, 10);

        Paragraph instName = new Paragraph("ESCUELA DEMO", titleFont);
        instName.setAlignment(Element.ALIGN_CENTER);
        instName.setSpacingAfter(10);
        document.add(instName);

        Paragraph role = new Paragraph("CREDENCIAL DE TUTOR", labelFont);
        role.setAlignment(Element.ALIGN_CENTER);
        role.setSpacingAfter(10);
        document.add(role);

        if (tutor.getPhotoData() != null) {
            try {
                Image img = Image.getInstance(tutor.getPhotoData());
                img.scaleToFit(100f, 100f);
                img.setAlignment(Element.ALIGN_CENTER);
                document.add(img);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Paragraph noPhoto = new Paragraph("[Sin Foto]", labelFont);
            noPhoto.setAlignment(Element.ALIGN_CENTER);
            document.add(noPhoto);
        }

        Paragraph name = new Paragraph(tutor.getFirstName() + " " + tutor.getLastName(), nameFont);
        name.setAlignment(Element.ALIGN_CENTER);
        name.setSpacingBefore(10);
        document.add(name);

        Paragraph email = new Paragraph(tutor.getEmail() != null ? tutor.getEmail() : "", labelFont);
        email.setAlignment(Element.ALIGN_CENTER);
        email.setSpacingAfter(10);
        document.add(email);

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
            byte[] qrBytes = pngOutputStream.toByteArray();

            Image qrImage = Image.getInstance(qrBytes);
            qrImage.setAlignment(Element.ALIGN_CENTER);
            document.add(qrImage);
        } catch (Exception e) {
            e.printStackTrace();
        }

        document.close();
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
