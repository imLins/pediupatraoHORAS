package com.umc.pediupatrao.service;

import com.umc.pediupatrao.entity.Pedido;
import com.umc.pediupatrao.repository.PedidoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PedidoService {

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private AuditoriaService auditoriaService; // Servico de Auditoria

    public Pedido criarPedido(Pedido pedido) {
        pedido.setDataHoraEntrada(LocalDateTime.now());
        pedido.setResponsavelRegistro(getUsuarioAutenticado());
        pedido.setStatus("RECEBIDO");

        // --- RF02: MATEMÁTICA ---
        // Descobre o preço base a partir do tipo da pizza vindo do frontend
        double precoBase = 0.0;
        if ("Tradicional".equals(pedido.getTipoPizza())) precoBase = 50.0;
        else if ("Especial".equals(pedido.getTipoPizza())) precoBase = 70.0;
        else if ("Doce".equals(pedido.getTipoPizza())) precoBase = 60.0;

        // Grava o preço unitário para não dar problema no futuro (como na hora do desconto)
        pedido.setPrecoUnitario(precoBase);

        int qtd = pedido.getQuantidade() > 0 ? pedido.getQuantidade() : 1;
        double desconto = pedido.getDesconto() != null ? pedido.getDesconto() : 0.0;

        // Calcula o valor total final
        pedido.setValorTotal((precoBase * qtd) - desconto);

        Pedido salvo = pedidoRepository.save(pedido);

        // Registra a criação na auditoria
        auditoriaService.registrarAuditoria("INCLUSÃO DE PEDIDO", "Pedido", salvo.getId(),
                "N/A", "Novo pedido Recebido. Total: R$" + salvo.getValorTotal(), "Abertura de pedido");

        return salvo;
    }

    public List<Pedido> listarPedidos() {
        return pedidoRepository.findAll();
    }

    public Pedido atualizarStatus(String id, String novoStatus) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Pedido não encontrado."));

        String statusAtual = pedido.getStatus();

        if ("CANCELADO".equals(statusAtual) || "FINALIZADO".equals(statusAtual)) {
            throw new IllegalArgumentException("Não é possível alterar o status de um pedido que já está " + statusAtual);
        }

        boolean transicaoValida = false;
        switch (statusAtual) {
            case "RECEBIDO":
                if ("EM PREPARAÇÃO".equals(novoStatus)) transicaoValida = true;
                break;
            case "EM PREPARAÇÃO":
                if ("PRONTO".equals(novoStatus)) transicaoValida = true;
                break;
            case "PRONTO":
                if ("SAIU PARA ENTREGA".equals(novoStatus) || "RETIRADO".equals(novoStatus)) {
                    transicaoValida = true;
                    pedido.setDataHoraSaida(LocalDateTime.now());
                    pedido.setResponsavelSaida(getUsuarioAutenticado());
                }
                break;
            case "SAIU PARA ENTREGA":
            case "RETIRADO":
                if ("FINALIZADO".equals(novoStatus)) transicaoValida = true;
                break;
        }

        if (!transicaoValida) {
            throw new IllegalArgumentException("Transição inválida! Não pode passar de " + statusAtual + " para " + novoStatus);
        }

        pedido.setStatus(novoStatus);
        Pedido salvo = pedidoRepository.save(pedido);

        // Registra a mudança de estado na auditoria
        auditoriaService.registrarAuditoria("ALTERAÇÃO DE STATUS", "Pedido", salvo.getId(),
                "Status: " + statusAtual, "Status: " + novoStatus, "Avanço no fluxo");

        return salvo;
    }

    public Pedido cancelarPedido(String id, String justificativa) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Pedido não encontrado."));

        String statusAtual = pedido.getStatus();

        if ("SAIU PARA ENTREGA".equals(statusAtual) || "RETIRADO".equals(statusAtual) || "FINALIZADO".equals(statusAtual)) {
            throw new IllegalArgumentException("Não é possível cancelar um pedido que já saiu para entrega ou foi finalizado.");
        }
        if ("CANCELADO".equals(statusAtual)) {
            throw new IllegalArgumentException("O pedido já está cancelado.");
        }

        if (justificativa == null || justificativa.trim().isEmpty()) {
            throw new IllegalArgumentException("A justificativa é obrigatória para o cancelamento.");
        }

        pedido.setStatus("CANCELADO");
        pedido.setJustificativaCancelamento(justificativa);

        Pedido salvo = pedidoRepository.save(pedido);

        // Registra o cancelamento na auditoria
        auditoriaService.registrarAuditoria("CANCELAMENTO DE PEDIDO", "Pedido", salvo.getId(),
                "Status: " + statusAtual, "Status: CANCELADO", justificativa);

        return salvo;
    }

    public Pedido aplicarDesconto(String id, Double percentualDesconto) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Pedido não encontrado."));

        String statusAtual = pedido.getStatus();

        if ("SAIU PARA ENTREGA".equals(statusAtual) || "RETIRADO".equals(statusAtual) || "FINALIZADO".equals(statusAtual) || "CANCELADO".equals(statusAtual)) {
            throw new IllegalArgumentException("Não é possível aplicar desconto em pedidos já enviados, finalizados ou cancelados.");
        }

        if (percentualDesconto == null || percentualDesconto <= 0 || percentualDesconto > 20.0) {
            throw new IllegalArgumentException("O desconto deve ser maior que 0% e limitado a no máximo 20%.");
        }

        double preco = pedido.getPrecoUnitario() != null ? pedido.getPrecoUnitario() : 0.0;
        int qtd = pedido.getQuantidade() > 0 ? pedido.getQuantidade() : 1;
        double valorBruto = preco * qtd;

        double valorDesconto = valorBruto * (percentualDesconto / 100.0);

        Double valorAntigo = pedido.getValorTotal();

        pedido.setDesconto(valorDesconto);
        pedido.setValorTotal(valorBruto - valorDesconto);

        Pedido salvo = pedidoRepository.save(pedido);

        // Registra o desconto na auditoria
        auditoriaService.registrarAuditoria("APLICAÇÃO DE DESCONTO", "Pedido", salvo.getId(),
                "Valor Total Anterior: R$" + valorAntigo, "Novo Valor Total: R$" + salvo.getValorTotal() + " (Desconto de " + percentualDesconto + "%)", "Benefício ao cliente");

        return salvo;
    }

    private String getUsuarioAutenticado() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated()) {
            return auth.getName();
        }
        return "Desconhecido";
    }
}