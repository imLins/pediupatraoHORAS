package com.umc.pediupatrao.service;

import com.umc.pediupatrao.entity.Auditoria;
import com.umc.pediupatrao.repository.AuditoriaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AuditoriaService {

    @Autowired
    private AuditoriaRepository auditoriaRepository;

    public void registrarAuditoria(String operacao, String entidade, String idEntidade,
                                   String valoresAnteriores, String valoresNovos, String justificativa) {

        Auditoria auditoria = new Auditoria();

        // Identifica quem está logado e o seu perfil
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated()) {
            auditoria.setUsuario(auth.getName());
            auditoria.setPerfilUsuario(auth.getAuthorities().toString());
        } else {
            auditoria.setUsuario("Sistema");
            auditoria.setPerfilUsuario("N/A");
        }

        auditoria.setDataHora(LocalDateTime.now());
        auditoria.setOperacao(operacao);
        auditoria.setEntidadeAfetada(entidade);
        auditoria.setIdEntidade(idEntidade);
        auditoria.setValoresAnteriores(valoresAnteriores);
        auditoria.setValoresNovos(valoresNovos);
        auditoria.setJustificativa(justificativa);

        auditoriaRepository.save(auditoria);
    }
}