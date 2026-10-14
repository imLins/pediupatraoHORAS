package com.umc.pediupatrao.service;

import com.umc.pediupatrao.repository.UsuarioRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UsuarioDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioDetailsService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return usuarioRepository.findByUsername(username)
                .map(usuario -> {
                    // Pega exatamente a string que o DataInitializer salvou (ex: ROLE_ADMIN)
                    String role = usuario.getRole() != null ? usuario.getRole().toUpperCase().trim() : "ROLE_USER";

                    // Se por acaso faltar o prefixo ROLE_, nós garantimos que ele tenha
                    if (!role.startsWith("ROLE_")) {
                        role = "ROLE_" + role;
                    }

                    return User.builder()
                            .username(usuario.getUsername())
                            .password(usuario.getPassword())
                            .authorities(role) // authorities() usa o nome exato da permissão, sem "mágicas"
                            .build();
                })
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado: " + username));
    }
}