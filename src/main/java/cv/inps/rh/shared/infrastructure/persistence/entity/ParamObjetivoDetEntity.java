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
import java.util.List;
import java.util.UUID;


@Getter
@Setter
@IgrpEntity
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "RH_T_PARAM_OBJETIVO_DET")
public class ParamObjetivoDetEntity extends AuditEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_param_obj_det")
  @SequenceGenerator(name = "seq_param_obj_det", sequenceName = "SEQ_PARAM_OBJETIVO_DET", allocationSize = 1)
  @Column(name = "ID")
  private Long id;

  @Column(name = "ANO", nullable = false)
  private Integer ano;

  @Column(name = "PESO_COMPORTAMENTAIS", precision = 5, scale = 2)
  private BigDecimal pesoComportamentais;

  @Column(name = "PESO_TECNICA", precision = 5, scale = 2)
  private BigDecimal pesoTecnica;

  @Column(name = "PONDERACAO_OBJETIVO", precision = 5, scale = 2)
  private BigDecimal ponderacaoObjetivo;

  @Column(name = "PONDERACAO_COMPETENCIA", precision = 5, scale = 2)
  private BigDecimal ponderacaoCompetencia;

  @Column(name = "PONDERACAO_ATITUDE_PESS", precision = 5, scale = 2)
  private BigDecimal ponderacaoAtitudePess;

  @Column(name = "ESTADO", length = 1)
  private String estado;

  @Column(name = "UUID")
  private UUID uuid;

  @Column(name = "VERSAO")
  private Integer versao;

  @OneToMany(mappedBy = "paramObjetivoDet", cascade = CascadeType.ALL)
  private List<ParamObjetivoEntity> objetivos;


}
