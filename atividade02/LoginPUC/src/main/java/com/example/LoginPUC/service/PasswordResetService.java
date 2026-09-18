package com.example.LoginPUC.service;

import com.example.LoginPUC.model.User;
import com.example.LoginPUC.repository.UserRepository;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.HexFormat;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
 *
 * O token em claro só existe dentro do email. No banco fica apenas o hash
 * SHA-256 dele.
 */
@Service
public class PasswordResetService {

    private static final Logger log = LoggerFactory.getLogger(PasswordResetService.class);

    private static final SecureRandom RANDOM = new SecureRandom();
    private static final int TOKEN_BYTES = 32;

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final SendEmailService sendEmailService;
    private final SessionInvalidationService sessionInvalidationService;
    private final String appName;
    private final String baseUrl;
    private final long tokenTtlMinutes;
    private final long cooldownMinutes;

    public PasswordResetService(UserRepository userRepository,
                                PasswordEncoder passwordEncoder,
                                SendEmailService sendEmailService,
                                SessionInvalidationService sessionInvalidationService,
                                @Value("${app.name}") String appName,
                                @Value("${app.base-url}") String baseUrl,
                                @Value("${app.reset-token-ttl-minutes}") long tokenTtlMinutes,
                                @Value("${app.reset-token-cooldown-minutes}") long cooldownMinutes) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.sendEmailService = sendEmailService;
        this.sessionInvalidationService = sessionInvalidationService;
        this.appName = appName;
        this.baseUrl = baseUrl;
        this.tokenTtlMinutes = tokenTtlMinutes;
        this.cooldownMinutes = cooldownMinutes;
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
            if (emPeriodoDeEspera(user)) {
                // Sem isso, qualquer pessoa poderia inundar a caixa de entrada
                // da vítima pedindo recuperação em sequência.
                log.info("Pedido de recuperação ignorado: ainda dentro da espera de {} min", cooldownMinutes);
                return;
            }

            String token = generateToken();

            user.setResetTokenHash(hash(token));
            user.setResetTokenCreatedAt(LocalDateTime.now());
            user.setResetTokenExpiry(LocalDateTime.now().plusMinutes(tokenTtlMinutes));
            userRepository.save(user);

            sendEmailService.sendEmail(
                    user.getEmail(),
                    "Recuperação de senha - " + appName,
                    buildEmailBody(user, token));
        });
    }

    /** Devolve o usuário dono do token, desde que ele exista e ainda não tenha expirado. */
    public Optional<User> findUserByValidToken(String token) {
        if (!StringUtils.hasText(token)) {
            return Optional.empty();
        }
        return userRepository.findByResetTokenHash(hash(token))
                .filter(user -> user.getResetTokenExpiry() != null
                        && user.getResetTokenExpiry().isAfter(LocalDateTime.now()));
    }

    /**
     * Grava a nova senha, invalida o token e derruba as sessões abertas do usuário.
     *
     * @return false se o token for inválido ou já tiver expirado.
     */
    @Transactional
    public boolean resetPassword(String token, String newPassword) {
        return findUserByValidToken(token)
                .map(user -> {
                    user.setPassword(passwordEncoder.encode(newPassword));
                    // Limpar o token garante que o link só funcione uma vez.
                    user.setResetTokenHash(null);
                    user.setResetTokenCreatedAt(null);
                    user.setResetTokenExpiry(null);
                    userRepository.save(user);

                    sessionInvalidationService.expireSessionsOf(user.getUsername());
                    return true;
                })
                .orElse(false);
    }

    private boolean emPeriodoDeEspera(User user) {
        LocalDateTime criadoEm = user.getResetTokenCreatedAt();
        return criadoEm != null && criadoEm.isAfter(LocalDateTime.now().minusMinutes(cooldownMinutes));
    }

    private String generateToken() {
        byte[] bytes = new byte[TOKEN_BYTES];
        RANDOM.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    /**
     * SHA-256, e não BCrypt, por dois motivos: precisamos procurar o usuário
     * pelo hash (BCrypt usa sal aleatório, então o mesmo token geraria hashes
     * diferentes a cada vez), e o token já tem 256 bits de aleatoriedade, o que
     * torna a força bruta inviável mesmo com um hash rápido. Senha escolhida
     * por humano é o caso oposto, e continua em BCrypt.
     */
    private String hash(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return HexFormat.of().formatHex(digest.digest(token.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 indisponível nesta JVM", e);
        }
    }

    private String buildEmailBody(User user, String token) {
        String link = baseUrl + "/resetpassword?token=" + token;
        return """
                Olá, %s!

                Recebemos um pedido para redefinir a senha da sua conta no %s.
                Acesse o link abaixo para cadastrar uma nova senha:

                %s

                O link é de uso único e expira em %d minutos.
                Se não foi você quem pediu, ignore este email: sua senha continua a mesma.
                """.formatted(user.getUsername(), appName, link, tokenTtlMinutes);
    }
}
