package com.escuela.abc_Escuelita.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ScannerController {

    @GetMapping("/scanner")
    public String scanner(Model model) {
        model.addAttribute("title", "Escáner de Entradas y Salidas");
        return "scanner/index";
    }
}
