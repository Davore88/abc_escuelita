package com.escuela.abc_Escuelita.config;

import com.escuela.abc_Escuelita.model.AdminUser;
import com.escuela.abc_Escuelita.repository.AdminUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class DataSeeder implements CommandLineRunner {

    @Autowired
    private AdminUserRepository adminUserRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        Optional<AdminUser> adminUser = adminUserRepository.findByUsername("admin");
        
        if (adminUser.isEmpty()) {
            AdminUser root = new AdminUser();
            root.setUsername("admin");
            root.setPassword(passwordEncoder.encode("admin"));
            root.setFirstName("Administrador");
            root.setLastName("Sistema");
            root.setRole("ROLE_ROOT");
            adminUserRepository.save(root);
            System.out.println("Default ROOT admin created with username 'admin' and password 'admin'");
        }
    }
}
