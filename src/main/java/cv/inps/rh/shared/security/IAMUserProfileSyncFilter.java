package cv.inps.rh.shared.security;

import cv.inps.rh.shared.application.services.IAMUserProfileService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Sincroniza o perfil IAM em cada request autenticado. A lógica (e todo o acesso ao repositório)
 * está centralizada em {@link IAMUserProfileService#syncFromJwt}; este filtro apenas extrai o JWT
 * do contexto de segurança e delega.
 */
@Component
public class IAMUserProfileSyncFilter extends OncePerRequestFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(IAMUserProfileSyncFilter.class);

    private final IAMUserProfileService iamUserProfileService;

    public IAMUserProfileSyncFilter(IAMUserProfileService iamUserProfileService) {
        this.iamUserProfileService = iamUserProfileService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth instanceof JwtAuthenticationToken jwtAuth) {
            try {
                iamUserProfileService.syncFromJwt(jwtAuth.getToken());
            } catch (Exception e) {
                LOGGER.warn("Failed to sync IAM user profile. sub={}, error={}",
                    jwtAuth.getToken().getSubject(), e.getMessage(), e);
            }
        }

        chain.doFilter(request, response);
    }
}
