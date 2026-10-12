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

    public Pedido criarPedido(Pedido pedido) {
        //Regista a entrada e o utilizador responsável automaticamente
        pedido.setDataHoraEntrada(LocalDateTime.now());
        pedido.setResponsavelRegistro(getUsuarioAutenticado());
        pedido.setStatus("PEDIDO RECEBIDO");

        // Previne valores nulos caso o frontend envie algo incompleto
        double preco = pedido.getPrecoUnitario() != null ? pedido.getPrecoUnitario() : 0.0;
        int qtd = pedido.getQuantidade() > 0 ? pedido.getQuantidade() : 1;
        double desconto = pedido.getDesconto() != null ? pedido.getDesconto() : 0.0;

        // RF02: Cálculo OBRIGATÓRIO no servidor
        pedido.setValorTotal((preco * qtd) - desconto);

        return pedidoRepository.save(pedido);
    }

    public List<Pedido> listarPedidos() {
        return pedidoRepository.findAll();
    }

    public Pedido atualizarStatus(String id, String novoStatus) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Pedido não encontrado."));

        String statusAtual = pedido.getStatus();

        // Bloqueia qualquer alteração se o pedido já estiver no fim do ciclo
        if ("CANCELADO".equals(statusAtual) || "FINALIZADO".equals(statusAtual)) {
            throw new IllegalArgumentException("Não é possível alterar o status de um pedido que já está " + statusAtual);
        }

        // Máquina de estados - Transições obrigatórias
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
                    // RF03: Regista obrigatoriamente a data e o responsável pela saída
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
        return pedidoRepository.save(pedido);
    }
//DESCONTO
    public Pedido cancelarPedido(String id, String justificativa) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Pedido não encontrado."));

        String statusAtual = pedido.getStatus();

        // Valida se o status permite cancelamento
        if ("SAIU PARA ENTREGA".equals(statusAtual) || "RETIRADO".equals(statusAtual) || "FINALIZADO".equals(statusAtual)) {
            throw new IllegalArgumentException("Não é possível cancelar um pedido que já saiu para entrega ou foi finalizado.");
        }
        if ("CANCELADO".equals(statusAtual)) {
            throw new IllegalArgumentException("O pedido já está cancelado.");
        }

        // Valida se a justificativa foi enviada
        if (justificativa == null || justificativa.trim().isEmpty()) {
            throw new IllegalArgumentException("A justificativa é obrigatória para o cancelamento.");
        }

        pedido.setStatus("CANCELADO");
        pedido.setJustificativaCancelamento(justificativa);

        return pedidoRepository.save(pedido);
    }

    public Pedido aplicarDesconto(String id, Double percentualDesconto) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Pedido não encontrado."));

        String statusAtual = pedido.getStatus();

        // Valida se o status permite desconto
        if ("SAIU PARA ENTREGA".equals(statusAtual) || "RETIRADO".equals(statusAtual) || "FINALIZADO".equals(statusAtual) || "CANCELADO".equals(statusAtual)) {
            throw new IllegalArgumentException("Não é possível aplicar desconto em pedidos já enviados, finalizados ou cancelados.");
        }

        // Valida a regra de limite de 20%
        if (percentualDesconto == null || percentualDesconto <= 0 || percentualDesconto > 20.0) {
            throw new IllegalArgumentException("O desconto deve ser maior que 0% e limitado a no máximo 20%.");
        }

        // Recalcula o valor bruto (Preço Unitário * Quantidade)
        double preco = pedido.getPrecoUnitario() != null ? pedido.getPrecoUnitario() : 0.0;
        int qtd = pedido.getQuantidade() > 0 ? pedido.getQuantidade() : 1;
        double valorBruto = preco * qtd;

        // Aplica o desconto matematicamente
        double valorDesconto = valorBruto * (percentualDesconto / 100.0);

        pedido.setDesconto(valorDesconto);
        pedido.setValorTotal(valorBruto - valorDesconto);

        return pedidoRepository.save(pedido);
    }
    // Metodo para ir buscar o nome do utilizador que está autenticado no momento
    private String getUsuarioAutenticado() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated()) {
            return auth.getName();
        }
        return "Desconhecido";
    }
}