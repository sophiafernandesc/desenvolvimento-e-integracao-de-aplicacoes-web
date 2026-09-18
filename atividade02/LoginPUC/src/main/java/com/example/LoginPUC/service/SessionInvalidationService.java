package com.example.LoginPUC.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

/**
 * Derruba as sessões abertas de um usuário.
 *
 * Trocar a senha não expulsa ninguém por si só: quem já estava logado continua
 * logado, porque a sessão no servidor não tem relação com a senha no banco.
 * Se a senha foi trocada porque a conta estava comprometida, o invasor
 * continuaria dentro. Por isso, ao redefinir a senha, expiramos as sessões.
 */
@Service
public class SessionInvalidationService {

    private static final Logger log = LoggerFactory.getLogger(SessionInvalidationService.class);

    private final SessionRegistry sessionRegistry;

    public SessionInvalidationService(SessionRegistry sessionRegistry) {
        this.sessionRegistry = sessionRegistry;
    }

    public void expireSessionsOf(String username) {
        int expiradas = 0;

        for (Object principal : sessionRegistry.getAllPrincipals()) {
            if (!username.equals(usernameOf(principal))) {
                continue;
            }
            for (SessionInformation session : sessionRegistry.getAllSessions(principal, false)) {
                // Marca a sessão como expirada: na próxima requisição dela,
                // o Spring Security desloga e redireciona para /login?expired.
                session.expireNow();
                expiradas++;
            }
        }

        if (expiradas > 0) {
            log.info("Sessões expiradas após troca de senha de {}: {}", username, expiradas);
        }
    }

    private String usernameOf(Object principal) {
        return principal instanceof UserDetails userDetails
                ? userDetails.getUsername()
                : String.valueOf(principal);
    }
}
