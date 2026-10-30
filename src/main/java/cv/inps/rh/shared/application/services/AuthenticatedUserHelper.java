package cv.inps.rh.shared.application.services;

import cv.inps.rh.shared.infrastructure.persistence.entity.IAMUserProfileEntity;
import cv.inps.rh.shared.infrastructure.persistence.repository.IAMUserProfileEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class AuthenticatedUserHelper {

  public static final String SYSTEM_FALLBACK = "system-bot@nosi.cv";

  private final IAMUserProfileEntityRepository iamUserProfileRepository;

  public Optional<Jwt> getJwt() {
    var auth = SecurityContextHolder.getContext().getAuthentication();
    if (auth instanceof JwtAuthenticationToken jwtAuth) {
      return Optional.of(jwtAuth.getToken());
    }
    return Optional.empty();
  }

  public String getSub() {
    return getJwt().map(Jwt::getSubject).orElse(null);
  }

  public String getSubOrSystem() {
    var sub = getSub();
    return (sub != null && !sub.isBlank()) ? sub : SYSTEM_FALLBACK;
  }

  public String getUsername() {
    return getJwt()
        .map(jwt -> {
          String preferred = jwt.getClaimAsString("preferred_username");
          return (preferred != null && !preferred.isBlank()) ? preferred : jwt.getClaimAsString("email");
        })
        .orElse(null);
  }

  public String getFullName() {
    var sub = getSub();
    if (sub == null) return null;
    return iamUserProfileRepository.findBySub(sub)
        .map(p -> p.getFullName() != null ? p.getFullName() : p.getUsername())
        .orElse(sub);
  }

  /**
   * Nome do utilizador do login para mostrar/pré-preencher (ex.: "Autorizado por" da missão).
   *
   * <p>Vem do token validado pelo resource server: perfil IAM, senão as claims do token. Mesmo com
   * {@code app.security.enabled=false} um token enviado é validado (inválido → 401). Sem token (dev/QA sem
   * IAM) devolve o nome da autenticação corrente — "anonymousUser", o mesmo que o auditor grava em
   * USER_REGISTO_NAME; null se não houver autenticação nenhuma.</p>
   */
  public String getNomeUtilizadorAtual() {
    var jwt = getJwt();
    if (jwt.isEmpty()) {
      var auth = SecurityContextHolder.getContext().getAuthentication();
      return auth != null && auth.getName() != null && !auth.getName().isBlank() ? auth.getName() : null;
    }
    var perfil = getProfile().map(p -> p.getFullName() != null ? p.getFullName() : p.getUsername()).orElse(null);
    if (perfil != null && !perfil.isBlank()) return perfil;
    return nomeDasClaims(jwt.get().getClaimAsString("name"), jwt.get().getClaimAsString("given_name"),
        jwt.get().getClaimAsString("family_name"), jwt.get().getClaimAsString("preferred_username"),
        jwt.get().getClaimAsString("email"));
  }

  static String nomeDasClaims(String name, String given, String family, String preferred, String email) {
    if (name != null && !name.isBlank()) return name.trim();
    var completo = ((given != null ? given : "") + " " + (family != null ? family : "")).trim();
    if (!completo.isEmpty()) return completo;
    if (preferred != null && !preferred.isBlank()) return preferred.trim();
    return email != null && !email.isBlank() ? email.trim() : null;
  }

  public Optional<IAMUserProfileEntity> getProfile() {
    var sub = getSub();
    if (sub == null) return Optional.empty();
    return iamUserProfileRepository.findBySub(sub);
  }

}
