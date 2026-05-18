package cv.inps.rh.shared.infrastructure.persistence.entity;

import cv.inps.rh.shared.config.AuditEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "RH_T_AUMENTO_SALARIAL")
public class AumentoSalarialEntity extends AuditEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "RH_T_AUMENTO_SALARIAL_id_gen")
  @SequenceGenerator(name = "RH_T_AUMENTO_SALARIAL_id_gen", sequenceName = "SEQ_AUMENTO_SALARIAL", allocationSize = 1)
  @Column(name = "ID", nullable = false)
  private Long id;

  @Size(max = 200)
  @NotNull
  @Column(name = "DESCRICAO", nullable = false, length = 200)
  private String descricao;

  @Size(max = 200)
  @NotNull
  @Column(name = "MOTIVO", nullable = false, length = 200)
  private String motivo;

  @NotNull
  @Column(name = "DATA_REFERENTE", nullable = false)
  private LocalDate dataReferente;

  @NotNull
  @Column(name = "PERCENTAGEM", nullable = false)
  private Long percentagem;

  @Size(max = 1)
  @NotNull
  @Column(name = "ESTADO", nullable = false, length = 1)
  private String estado;

  @Size(max = 50)
  @Column(name = "UUID", length = 50)
  private String uuid;

  @NotNull
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "PCCS_ID", nullable = false)
  private ParamPccsEntity pccs;
}
