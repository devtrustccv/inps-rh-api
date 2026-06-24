package cv.inps.rh.shared.infrastructure.persistence.entity;

import cv.igrp.framework.stereotype.IgrpEntity;
import cv.inps.rh.shared.config.AuditEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@IgrpEntity
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "RH_T_PARAM_MANUAL_FUNC")
public class ParamManualFuncaoEntity extends AuditEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_manual_func")
  @SequenceGenerator(name = "seq_manual_func", sequenceName = "SEQ_PARAM_MANUAL_FUNC", allocationSize = 1)
  @Column(name = "ID")
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "CARGO_ID")
  private ParamCargoEntity cargo;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "CARR_PCCS_ID")
  private ParamCarreiraEntity carreira;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "INSTIT_ID")
  private DirecaoEntity institId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "SECCAO_ID")
  private SecaoEntity seccaoId;

  @Column(name = "DESCRICAO", length = 300)
  private String descricao;

  @Column(name = "ESTADO", length = 1)
  private String estado;

  @Column(name = "UUID")
  private UUID uuid;
}
