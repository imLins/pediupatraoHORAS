package com.umc.pediupatrao.controller;

import com.umc.pediupatrao.repository.AuditoriaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AuditoriaController {

    @Autowired
    private AuditoriaRepository auditoriaRepository;

    @GetMapping("/auditoria")
    public String listarAuditoria(Model model) {
        // Envia todos os logs para a tela HTML, ordenados pela inclusão
        model.addAttribute("auditorias", auditoriaRepository.findAll());
        return "auditoria"; // Nome do arquivo HTML que vamos criar
    }
}