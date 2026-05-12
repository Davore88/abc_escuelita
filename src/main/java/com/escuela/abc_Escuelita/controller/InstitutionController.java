package com.escuela.abc_Escuelita.controller;

import com.escuela.abc_Escuelita.model.AdminUser;
import com.escuela.abc_Escuelita.model.Institution;
import com.escuela.abc_Escuelita.repository.AdminUserRepository;
import com.escuela.abc_Escuelita.repository.InstitutionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/institutions")
public class InstitutionController {

    @Autowired
    private InstitutionRepository institutionRepository;

    @Autowired
    private AdminUserRepository adminUserRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping
    public String listInstitutions(Model model) {
        List<Institution> institutions = institutionRepository.findAll();
        model.addAttribute("institutions", institutions);
        return "institutions/list";
    }

    @GetMapping("/create")
    public String createInstitutionForm() {
        return "institutions/create";
    }

    @PostMapping("/create")
    public String processInstitutionCreation(@RequestParam String name, 
                                             @RequestParam(required = false) String address,
                                             RedirectAttributes redirectAttributes) {
        Institution institution = new Institution();
        institution.setName(name);
        institution.setAddress(address);
        institutionRepository.save(institution);
        
        redirectAttributes.addFlashAttribute("successMessage", "Institución creada exitosamente.");
        return "redirect:/institutions";
    }

    @GetMapping("/{id}/directors/create")
    public String createDirectorForm(@PathVariable Long id, Model model) {
        Institution institution = institutionRepository.findById(id).orElse(null);
        if (institution == null) {
            return "redirect:/institutions";
        }
        model.addAttribute("institution", institution);
        return "institutions/create_director";
    }

    @PostMapping("/{id}/directors/create")
    public String processDirectorCreation(@PathVariable Long id,
                                          @RequestParam String username,
                                          @RequestParam String password,
                                          @RequestParam String firstName,
                                          @RequestParam String lastName,
                                          RedirectAttributes redirectAttributes) {
        Institution institution = institutionRepository.findById(id).orElse(null);
        if (institution != null) {
            // Check if username already exists
            if (adminUserRepository.findByUsername(username).isPresent()) {
                redirectAttributes.addFlashAttribute("errorMessage", "El nombre de usuario ya está en uso.");
                return "redirect:/institutions/" + id + "/directors/create";
            }

            AdminUser director = new AdminUser();
            director.setUsername(username);
            director.setPassword(passwordEncoder.encode(password));
            director.setFirstName(firstName);
            director.setLastName(lastName);
            director.setRole("ROLE_DIRECTOR");
            director.setInstitution(institution);
            adminUserRepository.save(director);
            
            redirectAttributes.addFlashAttribute("successMessage", "Director creado exitosamente para " + institution.getName());
        }
        return "redirect:/institutions";
    }

    @GetMapping("/edit/{id}")
    public String editInstitutionForm(@PathVariable Long id, Model model) {
        Institution institution = institutionRepository.findById(id).orElse(null);
        if (institution == null) {
            return "redirect:/institutions";
        }
        model.addAttribute("institution", institution);
        
        // Asumimos un solo director principal para editar en este formulario simple
        AdminUser director = null;
        if (institution.getAdmins() != null && !institution.getAdmins().isEmpty()) {
            director = institution.getAdmins().get(0);
        }
        model.addAttribute("director", director);
        
        return "institutions/edit";
    }

    @PostMapping("/edit/{id}")
    public String processInstitutionEdit(@PathVariable Long id,
                                         @RequestParam String name,
                                         @RequestParam(required = false) String address,
                                         @RequestParam(required = false) String directorUsername,
                                         @RequestParam(required = false) String directorPassword,
                                         @RequestParam(required = false) String directorFirstName,
                                         @RequestParam(required = false) String directorLastName,
                                         RedirectAttributes redirectAttributes) {
        Institution institution = institutionRepository.findById(id).orElse(null);
        if (institution != null) {
            institution.setName(name);
            institution.setAddress(address);
            institutionRepository.save(institution);

            if (directorUsername != null && !directorUsername.trim().isEmpty()) {
                AdminUser director = null;
                if (institution.getAdmins() != null && !institution.getAdmins().isEmpty()) {
                    director = institution.getAdmins().get(0);
                } else {
                    director = new AdminUser();
                    director.setRole("ROLE_DIRECTOR");
                    director.setInstitution(institution);
                }
                
                // Verificar si el username cambió y si ya existe
                if (!directorUsername.equals(director.getUsername()) && adminUserRepository.findByUsername(directorUsername).isPresent()) {
                    redirectAttributes.addFlashAttribute("errorMessage", "El nombre de usuario para el director ya está en uso.");
                    return "redirect:/institutions/edit/" + id;
                }

                director.setUsername(directorUsername);
                if (directorFirstName != null) director.setFirstName(directorFirstName);
                if (directorLastName != null) director.setLastName(directorLastName);
                if (directorPassword != null && !directorPassword.trim().isEmpty()) {
                    director.setPassword(passwordEncoder.encode(directorPassword));
                }
                adminUserRepository.save(director);
            }

            redirectAttributes.addFlashAttribute("successMessage", "Institución actualizada exitosamente.");
        }
        return "redirect:/institutions";
    }
}
