package cv.inps.rh.shared.infrastructure.persistence.entity;

import cv.inps.rh.shared.config.AuditEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "RH_T_SOAT")
public class SoatEntity extends AuditEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "RH_T_SOAT_id_gen")
  @SequenceGenerator(name = "RH_T_SOAT_id_gen", sequenceName = "SEQ_SOAT", allocationSize = 1)
  @Column(name = "ID", nullable = false)
  private Long id;

  @NotNull
  @Column(name = "ANO_REFERENTE", nullable = false)
  private Integer anoReferente;

  @NotNull
  @Column(name = "MES_REFERENTE", nullable = false)
  private Integer mesReferente;

  @Size(max = 1)
  @NotNull
  @Column(name = "ESTADO", nullable = false, length = 1)
  private String estado;

  @Size(max = 100)
  @NotNull
  @Column(name = "UUID", nullable = false, length = 100)
  private String uuid;
}
