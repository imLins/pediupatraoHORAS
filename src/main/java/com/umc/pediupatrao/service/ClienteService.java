package com.umc.pediupatrao.service;

import com.umc.pediupatrao.entity.Cliente;
import com.umc.pediupatrao.repository.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ClienteService {

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private AuditoriaService auditoriaService; // Injetando o nosso espião

    public Cliente novoCliente(Cliente cliente) {
        Cliente salvo = clienteRepository.save(cliente);

        // Registra a criação
        auditoriaService.registrarAuditoria("INCLUSÃO DE CLIENTE", "Cliente", salvo.getId(),
                "N/A", "Novo cliente cadastrado", "Cadastro inicial");

        return salvo;
    }

    public List<Cliente> listarClientes() {
        return clienteRepository.findAll();
    }

    // Método para excluir cliente
    public void excluir(String id) {
        Optional<Cliente> existente = clienteRepository.findById(id);
        if (existente.isPresent()) {
            clienteRepository.deleteById(id);

            // Registra a exclusão
            auditoriaService.registrarAuditoria("EXCLUSÃO DE CLIENTE", "Cliente", id,
                    "Dados do cliente excluído", "N/A", "Exclusão de registro");
        }
    }

    public Optional<Cliente> buscarPorId(String id) {
        return clienteRepository.findById(id);
    }

    // Método para salvar um novo cliente ou atualizar um cliente existente
    public Cliente salvar(Cliente cliente) {
        // Se o cliente não tem ID (novo cliente), salva como novo
        if (cliente.getId() == null || cliente.getId().isEmpty()) {
            Cliente salvo = clienteRepository.save(cliente);

            auditoriaService.registrarAuditoria("INCLUSÃO DE CLIENTE", "Cliente", salvo.getId(),
                    "N/A", "Novo cliente cadastrado via salvar()", "Cadastro inicial");

            return salvo;
        }
        // Se já tem ID (cliente existente), atualiza
        else {
            Cliente existente = clienteRepository.findById(cliente.getId())
                    .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado para atualização."));
            String valoresAntigos = existente.toString();
            String valoresNovos = cliente.toString();

            Cliente atualizado = clienteRepository.save(cliente);

            // Registra a alteração detalhada
            auditoriaService.registrarAuditoria("ALTERAÇÃO DE CLIENTE", "Cliente", atualizado.getId(),
                    valoresAntigos, valoresNovos, "Atualização de dados cadastrais (ex: endereço/telefone)");

            return atualizado;
        }
    }
}