package cv.inps.rh.shared.config;

import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class AuditEntityListener {

  @PrePersist
  public void setCreatedById(AuditEntity entity) {
    entity.setCreatedById(getCurrentUserId());

    // Os campos de alteração só fazem sentido em UPDATE. No registo (INSERT) devem ficar null.
    // Este listener corre depois do AuditingEntityListener (que preenche lastModified* na criação),
    // por isso limpamos aqui os três campos de alteração. Só o @PreUpdate os volta a preencher.
    entity.setLastModifiedById(null);
    entity.setLastModifiedBy(null);
    entity.setLastModifiedDate(null);
  }

  @PreUpdate
  public void setUpdatedById(AuditEntity entity) {
    entity.setLastModifiedById(getCurrentUserId());
  }

  private Long getCurrentUserId() {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();

    if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
      return 1L; // sistema/local
    }

    // Aqui podes extrair o ID do JWT ou do UserDetails
    return 2L; // valor default, substituir pelo real se tiver
  }

}
