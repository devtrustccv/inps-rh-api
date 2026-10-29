package cv.inps.rh.shared.application.services;

import cv.inps.rh.shared.infrastructure.persistence.entity.IAMUserProfileEntity;
import cv.inps.rh.shared.infrastructure.persistence.repository.IAMUserProfileEntityRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/** MS-005: nome do utilizador do login usado como "Autorizado por" por defeito. */
class AuthenticatedUserHelperTest {

  private final IAMUserProfileEntityRepository repo = mock(IAMUserProfileEntityRepository.class);
  private final AuthenticatedUserHelper helper = new AuthenticatedUserHelper(repo);

  @AfterEach
  void limpar() {
    SecurityContextHolder.clearContext();
  }

  private static void login(Map<String, Object> claims) {
    var jwt = new Jwt("t", Instant.now(), Instant.now().plusSeconds(60), Map.of("alg", "RS256"), claims);
    SecurityContextHolder.getContext().setAuthentication(new JwtAuthenticationToken(jwt));
  }

  @Test
  void semAutenticacaoDevolveNull() {
    assertNull(helper.getNomeUtilizadorAtual());
  }

  @Test
  void semTokenUsaOAnonimoComoOAuditor() {
    SecurityContextHolder.getContext().setAuthentication(new AnonymousAuthenticationToken(
        "k", "anonymousUser", AuthorityUtils.createAuthorityList("ROLE_ANONYMOUS")));
    assertEquals("anonymousUser", helper.getNomeUtilizadorAtual());
  }

  @Test
  void usaONomeDoPerfilIam() {
    var perfil = new IAMUserProfileEntity();
    perfil.setFullName("Tatiana Barbosa");
    when(repo.findBySub("u1")).thenReturn(Optional.of(perfil));
    login(Map.of("sub", "u1", "name", "Outro Nome"));
    assertEquals("Tatiana Barbosa", helper.getNomeUtilizadorAtual());
  }

  @Test
  void semPerfilUsaAsClaimsDoToken() {
    when(repo.findBySub(anyString())).thenReturn(Optional.empty());
    login(Map.of("sub", "u2", "name", "Ana Lima"));
    assertEquals("Ana Lima", helper.getNomeUtilizadorAtual());
    login(Map.of("sub", "u3", "given_name", "Rui", "family_name", "Silva"));
    assertEquals("Rui Silva", helper.getNomeUtilizadorAtual());
    login(Map.of("sub", "u4", "preferred_username", "rui.silva"));
    assertEquals("rui.silva", helper.getNomeUtilizadorAtual());
  }
}
