package com.umc.pediupatrao.config;

import com.umc.pediupatrao.entity.Usuario;
import com.umc.pediupatrao.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initDatabase(UsuarioRepository repo) {
        return args -> {
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
            //Cria o perfil ADMIN
            if (repo.findByUsername("admin").isEmpty()) {
                Usuario user = new Usuario();
                user.setUsername("admin");
                user.setPassword(encoder.encode("teste123"));
                user.setRole("ROLE_ADMIN");
                repo.save(user);
                System.out.println("Usuário admin criado!");
            }
            // Criando o GERENTE
            if (repo.findByUsername("gerente").isEmpty()){
                Usuario gerente = new Usuario();
                gerente.setUsername("gerente");
                gerente.setPassword(encoder.encode("123#@!"));
                gerente.setRole("ROLE_GERENTE");
                repo.save(gerente);
                System.out.println("Usuário GERENTE criado com sucesso.");
            }
            // Criando o ATENDENTE
            if(repo.findByUsername("atendente").isEmpty()){
                Usuario atendente = new Usuario();
                atendente.setUsername("atendente");
                atendente.setPassword(encoder.encode("123123"));
                atendente.setRole("ROLE_ATENDENTE");
                repo.save(atendente);
                System.out.println("Usuário ATENDENTE criado com sucesso.");
            }
        };
    }
}