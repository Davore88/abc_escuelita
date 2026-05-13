package com.escuela.abc_Escuelita.controller;

import com.escuela.abc_Escuelita.model.AccessLog;
import com.escuela.abc_Escuelita.model.Student;
import com.escuela.abc_Escuelita.model.Tutor;
import com.escuela.abc_Escuelita.repository.AccessLogRepository;
import com.escuela.abc_Escuelita.repository.TutorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/scanner")
public class ScannerApiController {

    @Autowired
    private TutorRepository tutorRepository;

    @Autowired
    private AccessLogRepository accessLogRepository;

    @GetMapping("/tutor/{tutorId}")
    public ResponseEntity<?> getTutorInfo(@PathVariable Long tutorId) {
        Tutor tutor = tutorRepository.findById(tutorId).orElse(null);
        if (tutor == null) {
            return ResponseEntity.notFound().build();
        }

        Map<String, Object> response = new HashMap<>();
        response.put("tutorId", tutor.getId());
        response.put("tutorName", tutor.getFirstName() + " " + tutor.getLastName());
        if (tutor.getPhotoData() != null) {
            response.put("tutorPhotoBase64", Base64.getEncoder().encodeToString(tutor.getPhotoData()));
        }

        List<Map<String, Object>> studentsData = new ArrayList<>();
        String actionType = "ENTRY"; // Default

        if (tutor.getStudents() != null && !tutor.getStudents().isEmpty()) {
            for (Student student : tutor.getStudents()) {
                Map<String, Object> studentMap = new HashMap<>();
                studentMap.put("studentId", student.getId());
                studentMap.put("studentName", student.getFirstName() + " " + student.getLastName());
                if (student.getPhotoData() != null) {
                    studentMap.put("studentPhotoBase64", Base64.getEncoder().encodeToString(student.getPhotoData()));
                }
                studentsData.add(studentMap);
            }

            // Determine if next action is ENTRY or EXIT based on the first student's log today
            Student firstStudent = tutor.getStudents().get(0);
            LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
            LocalDateTime endOfDay = LocalDate.now().atTime(LocalTime.MAX);
            
            AccessLog lastLog = accessLogRepository.findFirstByStudentIdAndTimestampBetweenOrderByTimestampDesc(
                    firstStudent.getId(), startOfDay, endOfDay);
            
            if (lastLog != null && lastLog.getAccessType().equals("ENTRY")) {
                actionType = "EXIT";
            } else {
                actionType = "ENTRY";
            }
        }

        response.put("students", studentsData);
        response.put("nextAction", actionType);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/log")
    public ResponseEntity<?> createLog(@RequestBody Map<String, Object> payload) {
        Long tutorId = Long.valueOf(payload.get("tutorId").toString());
        String actionType = payload.get("actionType").toString(); // "ENTRY" or "EXIT"

        Tutor tutor = tutorRepository.findById(tutorId).orElse(null);
        if (tutor == null) {
            return ResponseEntity.badRequest().body("Tutor not found");
        }

        if (tutor.getStudents() != null) {
            LocalDateTime now = LocalDateTime.now();
            for (Student student : tutor.getStudents()) {
                AccessLog log = new AccessLog();
                log.setTutor(tutor);
                log.setStudent(student);
                log.setInstitution(student.getInstitution());
                log.setAccessType(actionType);
                log.setTimestamp(now);
                accessLogRepository.save(log);
            }
        }
        
        Map<String, String> result = new HashMap<>();
        result.put("status", "success");
        result.put("message", "Registro guardado correctamente.");
        return ResponseEntity.ok(result);
    }
}
