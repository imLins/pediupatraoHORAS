package com.umc.pediupatrao.controller;

import com.umc.pediupatrao.entity.Pedido;
import com.umc.pediupatrao.service.PedidoService;
import com.umc.pediupatrao.repository.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;

// Alteramos de @RestController para @Controller para permitir que ele devolva telas HTML
@Controller
public class PedidoController {

    @Autowired
    private PedidoService pedidoService;

    // Injetamos o repositório de clientes para podermos listar no formulário
    @Autowired
    private ClienteRepository clienteRepository;

    // ROTAS DE TELA (HTML / UI) - RF02 e RF03

    // 1. Rota para abrir a tela de Novo Pedido (Busca os clientes no banco)
    @GetMapping("/pedidos/novo")
    public String exibirFormularioPedido(Model model) {
        model.addAttribute("clientes", clienteRepository.findAll());
        return "pedido-form"; // Abre o arquivo pedido-form.html
    }

    // Rota para processar o botão de "Confirmar Pedido" e calcular valores
    @PostMapping("/api/pedidos/salvar")
    public String salvarNovoPedido(@ModelAttribute Pedido pedido, Principal principal) {

        System.out.println("==== ALARME: O BOTÃO CONFIRMAR FOI CLICADO! ====");
        System.out.println("Tipo de Pizza recebida: " + pedido.getTipoPizza());
        System.out.println("Quantidade recebida: " + pedido.getQuantidade());

        // O Service faz tudo: status inicial, data, responsável e a matemática (RF02 e RF03)
        pedidoService.criarPedido(pedido);

        System.out.println("==== PEDIDO SALVO COM SUCESSO NO BANCO ====");

        // Redireciona de volta para a lista com mensagem de sucesso
        return "redirect:/pedidos?sucesso=Pedido registrado com sucesso!";
    }
    // ==========================================
    // ROTAS EXCLUSIVAS DO GERENTE (RF04)
    // ==========================================

    @PostMapping("/api/pedidos/{id}/cancelar")
    public String cancelarPedidoGerente(@PathVariable String id, @RequestParam String justificativa) {
        pedidoService.cancelarPedido(id, justificativa);
        return "redirect:/pedidos?sucesso=Pedido cancelado com sucesso!";
    }

    @PostMapping("/api/pedidos/{id}/desconto")
    public String aplicarDescontoGerente(@PathVariable String id, @RequestParam Double percentualDesconto) {
        pedidoService.aplicarDesconto(id, percentualDesconto);
        return "redirect:/pedidos?sucesso=Desconto aplicado com sucesso!";
    }
    @PostMapping("/api/pedidos/{id}/status")
    public String atualizarStatusPedido(@PathVariable String id, @RequestParam String novoStatus) {
        try {
            pedidoService.atualizarStatus(id, novoStatus);
            return "redirect:/pedidos?sucesso=Status atualizado para " + novoStatus + " com sucesso!";
        } catch (IllegalArgumentException e) {
            return "redirect:/pedidos?erro=" + e.getMessage();
        }
    }

    // O @ResponseBody avisa que esses métodos continuam devolvendo JSON e não telas
    @ResponseBody
    @PostMapping("/api/pedidos")
    public Pedido criarPedidoAPI(@RequestBody Pedido pedido) {
        return pedidoService.criarPedido(pedido);
    }

    @ResponseBody
    @GetMapping("/api/pedidos")
    public List<Pedido> listarPedidosAPI() {
        return pedidoService.listarPedidos();
    }

    @ResponseBody
    @PutMapping("/api/pedidos/{id}/status")
    public Pedido atualizarStatusAPI(@PathVariable String id, @RequestParam String status) {
        return pedidoService.atualizarStatus(id, status);
    }
}