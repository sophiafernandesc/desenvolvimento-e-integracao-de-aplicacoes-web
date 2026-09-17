package com.example.LoginPUC.service;

import com.example.LoginPUC.model.User;
import com.example.LoginPUC.repository.UserRepository;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

/**
 * Regras da recuperação de senha.
 *
 * A senha nunca pode ser reenviada: ela está gravada como hash BCrypt, que é
 * irreversível. O que fazemos é gerar um token aleatório de uso único, mandar
 * por email um link contendo esse token e só então permitir cadastrar uma senha nova.
 */
@Service
public class PasswordResetService {

    private static final SecureRandom RANDOM = new SecureRandom();
    private static final int TOKEN_BYTES = 32;

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final SendEmailService sendEmailService;
    private final String baseUrl;
    private final long tokenTtlMinutes;

    public PasswordResetService(UserRepository userRepository,
                                PasswordEncoder passwordEncoder,
                                SendEmailService sendEmailService,
                                @Value("${app.base-url}") String baseUrl,
                                @Value("${app.reset-token-ttl-minutes}") long tokenTtlMinutes) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.sendEmailService = sendEmailService;
        this.baseUrl = baseUrl;
        this.tokenTtlMinutes = tokenTtlMinutes;
    }

    /**
     * Gera o token e dispara o email, se o endereço existir.
     *
     * Se o envio falhar, a exceção propaga e a transação é desfeita: nenhum token
     * fica gravado sem que o usuário tenha recebido o link correspondente.
     */
    @Transactional
    public void requestReset(String email) {
        userRepository.findByEmailIgnoreCase(email.trim()).ifPresent(user -> {
            String token = generateToken();

            user.setResetToken(token);
            user.setResetTokenExpiry(LocalDateTime.now().plusMinutes(tokenTtlMinutes));
            userRepository.save(user);

            sendEmailService.sendEmail(
                    user.getEmail(),
                    "Recuperação de senha - LoginPUC",
                    buildEmailBody(user, token));
        });
    }

    /** Devolve o usuário dono do token, desde que ele exista e ainda não tenha expirado. */
    public Optional<User> findUserByValidToken(String token) {
        if (!StringUtils.hasText(token)) {
            return Optional.empty();
        }
        return userRepository.findByResetToken(token)
                .filter(user -> user.getResetTokenExpiry() != null
                        && user.getResetTokenExpiry().isAfter(LocalDateTime.now()));
    }

    /**
     * Grava a nova senha e invalida o token.
     *
     * @return false se o token for inválido ou já tiver expirado.
     */
    @Transactional
    public boolean resetPassword(String token, String newPassword) {
        return findUserByValidToken(token)
                .map(user -> {
                    user.setPassword(passwordEncoder.encode(newPassword));
                    // Limpar o token garante que o link só funcione uma vez.
                    user.setResetToken(null);
                    user.setResetTokenExpiry(null);
                    userRepository.save(user);
                    return true;
                })
                .orElse(false);
    }

    private String generateToken() {
        byte[] bytes = new byte[TOKEN_BYTES];
        RANDOM.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private String buildEmailBody(User user, String token) {
        String link = baseUrl + "/resetpassword?token=" + token;
        return """
                Olá, %s!

                Recebemos um pedido para redefinir a senha da sua conta no LoginPUC.
                Acesse o link abaixo para cadastrar uma nova senha:

                %s

                O link é de uso único e expira em %d minutos.
                Se não foi você quem pediu, ignore este email: sua senha continua a mesma.
                """.formatted(user.getUsername(), link, tokenTtlMinutes);
    }
}
