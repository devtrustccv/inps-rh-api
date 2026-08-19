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

  public Optional<IAMUserProfileEntity> getProfile() {
    var sub = getSub();
    if (sub == null) return Optional.empty();
    return iamUserProfileRepository.findBySub(sub);
  }

}
