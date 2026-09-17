package com.example.LoginPUC.service;

import com.example.LoginPUC.model.User;
import com.example.LoginPUC.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

//Spring security e tabela de usuarios
//O Spring chama loadUserByUsername durante o login para validar as credenciais.

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {
        // Permite que o usuário faça login informando o nome de usuário OU o email.
        User user = userRepository.findByUsername(usernameOrEmail)
                .or(() -> userRepository.findByEmailIgnoreCase(usernameOrEmail))
                .orElseThrow(() -> new UsernameNotFoundException("Usuário ou senha inválidos"));

        // A comparação da senha (BCrypt) fica a cargo do próprio Spring Security.
        return org.springframework.security.core.userdetails.User
                .withUsername(user.getUsername())
                .password(user.getPassword())
                .roles("USER")
                .build();
    }
}
