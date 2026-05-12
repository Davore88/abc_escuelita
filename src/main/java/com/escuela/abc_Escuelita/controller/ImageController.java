package com.escuela.abc_Escuelita.controller;

import com.escuela.abc_Escuelita.model.Student;
import com.escuela.abc_Escuelita.model.Tutor;
import com.escuela.abc_Escuelita.repository.StudentRepository;
import com.escuela.abc_Escuelita.repository.TutorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/images")
public class ImageController {

    @Autowired
    private TutorRepository tutorRepository;

    @Autowired
    private StudentRepository studentRepository;

    @GetMapping("/tutor/{id}")
    public ResponseEntity<byte[]> getTutorImage(@PathVariable Long id) {
        Tutor tutor = tutorRepository.findById(id).orElse(null);
        if (tutor != null && tutor.getPhotoData() != null) {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_JPEG);
            return new ResponseEntity<>(tutor.getPhotoData(), headers, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping("/student/{id}")
    public ResponseEntity<byte[]> getStudentImage(@PathVariable Long id) {
        Student student = studentRepository.findById(id).orElse(null);
        if (student != null && student.getPhotoData() != null) {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_JPEG);
            return new ResponseEntity<>(student.getPhotoData(), headers, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}
