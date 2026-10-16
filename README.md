# Pediu Patrão - Sistema de Gestão de Pedidos

Sistema web de gestão para pizzarias, desenvolvido por Antonio Luiz Lins Neto como projeto acadêmico para o curso de Engenharia de Software da Universidade de Mogi das Cruzes. O projeto garante o controle rigoroso do ciclo de vida dos pedidos, blindagem de acessos por perfis de usuário e um motor de auditoria imutável para rastreamento de ações críticas.

## As Tecnologias Utilizadas
- **Backend:** Java 21, Spring Boot 3.4.4, Spring Security
- **Frontend:** Thymeleaf, Bootstrap 5, HTML5/CSS3
- **Banco de Dados:** MongoDB (Auditoria) e Relacional (Dados Transacionais)
- **Gerenciamento de Dependências:** Maven

## 🎯 Os Requisitos Implementados (RF01 a RF05)
1. **RF01 - Controle de Perfis e Acessos:** Autenticação e autorização baseada em Roles (RBAC). Perfis implementados: `ADMIN`, `GERENTE` e `ATENDENTE`. Menus e botões adaptam-se dinamicamente ao perfil autenticado, escondendo opções não autorizadas.
2. **RF02 - Evolução do Registro de Pedidos:** A lógica de cálculo de valores e o registro de data/hora foram movidos integralmente para o servidor, garantindo blindagem contra manipulação de preços pelo Frontend.
3. **RF03 - Controle de Transições de Pedidos:** Implementação de uma máquina de estados rigorosa (RECEBIDO > EM PREPARAÇÃO > PRONTO > SAIU PARA ENTREGA > FINALIZADO).
4. **RF04 - Ações Exclusivas do Gerente:** Criação de modais e rotas no backend restritas ao perfil `GERENTE` para aplicação de descontos (limite de 20%) e cancelamentos (exigindo justificativa obrigatória).
5. **RF05 - Motor de Auditoria:** Rastreabilidade completa de todas as operações sensíveis (Criação de Pedidos, Alteração de Status, Descontos, Cancelamentos e Modificação de Clientes). O histórico é salvo de forma imutável, registrando usuário, data, hora, valores anteriores e novos.

## 🛠️ Principais Alterações Realizadas
- **Refatoração de Segurança:** Substituição de links `GET` vulneráveis por formulários `POST` nas ações de transição de status da tabela de pedidos.
- **Melhoria de UX/UI:** Implementação de formulários modernos com Bootstrap 5, validação visual de "Cálculo Automático Seguro" e badges de status dinâmicas com cores variadas.
- **Limpeza de Apresentação:** Utilização da biblioteca *Thymeleaf Extras Spring Security 6* e expressões nativas para ocultar componentes restritos e limpar os prefixos técnicos (`ROLE_`) das interfaces.

## ⚙️ Instruções de Execução

### Pré-requisitos
- Java Development Kit (JDK) 21 instalado.
- Maven instalado.
- MongoDB em execução (localmente na porta 27017 ou configurado no arquivo `application.properties`).

### Passos para executar a aplicação
1. Clone este repositório:
   - git clone

2. Acesse o diretório do projeto.
   - cd pediupatrao

3. Compile as dependencias do projeto
   - mvn clean install

4. Execute o projeto
   - mvn spring-boot:run
5. Acesse o navegador e procure o endereço http://localhost:8080
   - Credenciais de teste,
   - Admin: admin - teste123
   - Gerente: gerente - 123#@!
   - Atendente: atendente - 123123
   