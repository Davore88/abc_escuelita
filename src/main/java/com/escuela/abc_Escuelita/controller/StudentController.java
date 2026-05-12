package com.escuela.abc_Escuelita.controller;

import com.escuela.abc_Escuelita.model.Student;
import com.escuela.abc_Escuelita.model.Tutor;
import com.escuela.abc_Escuelita.repository.StudentRepository;
import com.escuela.abc_Escuelita.repository.TutorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.ArrayList;

@Controller
@RequestMapping("/students")
public class StudentController {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private TutorRepository tutorRepository;

    @Autowired
    private com.escuela.abc_Escuelita.repository.InstitutionRepository institutionRepository;

    @jakarta.annotation.PostConstruct
    public void init() {
        com.escuela.abc_Escuelita.model.Institution institution = institutionRepository.findById(1L).orElseGet(() -> {
            com.escuela.abc_Escuelita.model.Institution newInst = new com.escuela.abc_Escuelita.model.Institution();
            newInst.setName("Escuela Demo");
            return institutionRepository.save(newInst);
        });

        List<Student> unassignedStudents = studentRepository.findAll().stream()
                .filter(s -> s.getInstitution() == null)
                .toList();

        for (Student s : unassignedStudents) {
            s.setInstitution(institution);
            studentRepository.save(s);
        }
    }

    @GetMapping
    public String listStudents(@RequestParam(required = false) String search, Model model) {
        Long institutionId = 1L; // Hardcoded para la demostración
        List<Student> students;
        if (search != null && !search.trim().isEmpty()) {
            students = studentRepository.searchStudents(search.trim(), institutionId);
            model.addAttribute("search", search.trim());
        } else {
            students = studentRepository.findByInstitutionId(institutionId);
        }
        model.addAttribute("students", students);
        return "students/list";
    }

    @GetMapping("/register")
    public String registerStudent() {
        return "students/register";
    }

    @PostMapping("/register")
    public String processRegistration(@RequestParam String studentFirstName,
                                      @RequestParam String studentLastName,
                                      @RequestParam(required = false) String studentGroup,
                                      @RequestParam(value = "studentPhoto", required = false) org.springframework.web.multipart.MultipartFile studentPhoto,
                                      @RequestParam String tutorFirstName,
                                      @RequestParam String tutorLastName,
                                      @RequestParam String tutorEmail,
                                      @RequestParam(value = "tutorPhoto", required = false) org.springframework.web.multipart.MultipartFile tutorPhoto,
                                      RedirectAttributes redirectAttributes) {
        
        Student student = new Student();
        student.setFirstName(studentFirstName);
        student.setLastName(studentLastName);
        student.setGroupName(studentGroup);
        
        try {
            if (studentPhoto != null && !studentPhoto.isEmpty()) {
                student.setPhotoData(studentPhoto.getBytes());
            }
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
        
        com.escuela.abc_Escuelita.model.Institution institution = institutionRepository.findById(1L).orElseGet(() -> {
            com.escuela.abc_Escuelita.model.Institution newInst = new com.escuela.abc_Escuelita.model.Institution();
            newInst.setName("Escuela Demo");
            return institutionRepository.save(newInst);
        });
        student.setInstitution(institution);
        
        student = studentRepository.save(student);

        Tutor tutor = new Tutor();
        tutor.setFirstName(tutorFirstName);
        tutor.setLastName(tutorLastName);
        tutor.setEmail(tutorEmail);
        
        try {
            if (tutorPhoto != null && !tutorPhoto.isEmpty()) {
                tutor.setPhotoData(tutorPhoto.getBytes());
            }
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
        
        tutor = tutorRepository.save(tutor);
        
        if (student.getTutors() == null) {
            student.setTutors(new ArrayList<>());
        }
        student.getTutors().add(tutor);
        studentRepository.save(student);

        redirectAttributes.addFlashAttribute("registroExitoso", true);
        redirectAttributes.addFlashAttribute("newTutorId", tutor.getId());
        return "redirect:/students";
    }

    @GetMapping("/edit/{id}")
    public String editStudent(@PathVariable Long id, Model model) {
        Student student = studentRepository.findById(id).orElse(null);
        if (student == null) {
            return "redirect:/students";
        }
        model.addAttribute("student", student);
        return "students/edit";
    }

    @PostMapping("/edit/{id}")
    public String updateStudent(@PathVariable Long id, @RequestParam String studentFirstName, 
                                @RequestParam String studentLastName, @RequestParam String studentGroup) {
        Student student = studentRepository.findById(id).orElse(null);
        if (student != null) {
            student.setFirstName(studentFirstName);
            student.setLastName(studentLastName);
            student.setGroupName(studentGroup);
            studentRepository.save(student);
        }
        return "redirect:/students";
    }

    @GetMapping("/{id}/tutors/add")
    public String addTutor(@PathVariable Long id, Model model) {
        Student student = studentRepository.findById(id).orElse(null);
        if (student == null) {
            return "redirect:/students";
        }
        model.addAttribute("student", student);
        return "students/add_tutor";
    }

    @PostMapping("/{id}/tutors/add")
    public String saveTutor(@PathVariable Long id) {
        // Here you would save the tutor and link to the student
        return "redirect:/students";
    }
}
