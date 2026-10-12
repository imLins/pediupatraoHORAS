package com.umc.pediupatrao.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;
@Document(collection = "auditoria")

public class Auditoria {

        @Id
        private String id;
        private String usuario;
        private String perfilUsuario;
        private LocalDateTime dataHora;
        private String operacao;
        private String entidadeAfetada;
        private String idEntidade;
        private String valoresAnteriores;
        private String valoresNovos;
        private String justificativa;

        // Construtor vazio (obrigatório para o Spring/MongoDB)
        public Auditoria() {}

        // Getters e Setters
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }

        public String getUsuario() { return usuario; }
        public void setUsuario(String usuario) { this.usuario = usuario; }

        public String getPerfilUsuario() { return perfilUsuario; }
        public void setPerfilUsuario(String perfilUsuario) { this.perfilUsuario = perfilUsuario; }

        public LocalDateTime getDataHora() { return dataHora; }
        public void setDataHora(LocalDateTime dataHora) { this.dataHora = dataHora; }

        public String getOperacao() { return operacao; }
        public void setOperacao(String operacao) { this.operacao = operacao; }

        public String getEntidadeAfetada() { return entidadeAfetada; }
        public void setEntidadeAfetada(String entidadeAfetada) { this.entidadeAfetada = entidadeAfetada; }

        public String getIdEntidade() { return idEntidade; }
        public void setIdEntidade(String idEntidade) { this.idEntidade = idEntidade; }

        public String getValoresAnteriores() { return valoresAnteriores; }
        public void setValoresAnteriores(String valoresAnteriores) { this.valoresAnteriores = valoresAnteriores; }

        public String getValoresNovos() { return valoresNovos; }
        public void setValoresNovos(String valoresNovos) { this.valoresNovos = valoresNovos; }

        public String getJustificativa() { return justificativa; }
        public void setJustificativa(String justificativa) { this.justificativa = justificativa; }
    }

