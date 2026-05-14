package com.escuela.abc_Escuelita.controller;

import com.escuela.abc_Escuelita.model.Institution;
import com.escuela.abc_Escuelita.model.StudentGroup;
import com.escuela.abc_Escuelita.repository.InstitutionRepository;
import com.escuela.abc_Escuelita.repository.StudentGroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/groups")
public class StudentGroupController {

    @Autowired
    private StudentGroupRepository studentGroupRepository;

    @Autowired
    private InstitutionRepository institutionRepository;

    @GetMapping
    public String listGroups(Model model) {
        Long institutionId = 1L; // Hardcoded para demostración
        List<StudentGroup> groups = studentGroupRepository.findByInstitutionId(institutionId);
        model.addAttribute("groups", groups);
        return "groups/list";
    }

    @PostMapping("/create")
    public String createGroup(@RequestParam String name) {
        Long institutionId = 1L; // Hardcoded para demostración
        Institution institution = institutionRepository.findById(institutionId).orElse(null);
        if (institution != null && name != null && !name.trim().isEmpty()) {
            StudentGroup group = new StudentGroup();
            group.setName(name.trim());
            group.setInstitution(institution);
            studentGroupRepository.save(group);
        }
        return "redirect:/groups";
    }

    @PostMapping("/delete/{id}")
    public String deleteGroup(@PathVariable Long id) {
        studentGroupRepository.deleteById(id);
        return "redirect:/groups";
    }
}
