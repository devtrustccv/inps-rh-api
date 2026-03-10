/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME */

package cv.inps.rh.shared.infrastructure.persistence.entity;

import cv.igrp.framework.stereotype.IgrpEntity;
import cv.inps.rh.shared.config.AuditEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;


@Getter
@Setter
@IgrpEntity
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "RH_T_PARAM_OBJETIVO")
public class ParamObjetivoEntity extends AuditEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_param_objetivo")
  @SequenceGenerator(name = "seq_param_objetivo", sequenceName = "SEQ_PARAM_OBJETIVO", allocationSize = 1)
  @Column(name = "id", unique = true, nullable = false)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "PARAM_OBJ_DET_ID", nullable = false)
  private ParamObjetivoDetEntity paramObjetivoDet;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "CARGO_ID")
  private ParamCargoEntity cargo;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "CARR_PCCS_IS")
  private ParamCarreiraEntity carreira;

  @Column(name = "NUMERO_ORDEM", nullable = false)
  private Integer numeroOrdem;

  @Column(name = "ABRAGENCIA", length = 100, nullable = false)
  private String abrangencia;              // INPS | DIRECAO | INDIVIDUAL

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "INSTIT_ID")
  private InstituicaoEntity institId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "SECCAO_ID")
  private SecaoEntity seccaoId;

  @Column(name = "DESCRICAO", length = 300)
  private String descricao;

  @Column(name = "KPI", length = 300)
  private String kpi;

  @Column(name = "PONDERACAO", nullable = false, precision = 5, scale = 2)
  private BigDecimal ponderacao;

  @Column(name = "COMPONENTE", length = 100, nullable = false)
  private String componente;               // OBJETIVO | COMPETENCIA_COMPORTAMENTAL | COMPETENCIA_TECNICA | ATITUDE_PESSOAL

  @Column(name = "ESTADO", length = 1)
  private String estado;

  @Column(name = "UUID")
  private UUID uuid;


}
