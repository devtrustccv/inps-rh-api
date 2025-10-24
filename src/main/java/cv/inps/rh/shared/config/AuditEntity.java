package cv.inps.rh.shared.config;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@Setter
@MappedSuperclass
@EntityListeners({AuditingEntityListener.class, AuditEntityListener.class})
public abstract class AuditEntity {

  @CreatedDate
  @Column(name = "data_registo", nullable = false, updatable = false)
  private LocalDateTime createdDate;

  @CreatedBy
  @Column(name = "user_registo_name", nullable = false, updatable = false)
  private String createdBy;

  @Column(name = "user_registo_id", nullable = false, updatable = false)
  private Long createdById;

  @LastModifiedDate
  @Column(name = "data_alteracao")
  private LocalDateTime lastModifiedDate;

  @LastModifiedBy
  @Column(name = "user_alteracao_name")
  private String lastModifiedBy;

  @Column(name = "user_alteracao_id")
  private Long lastModifiedById;

}
