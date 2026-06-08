/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME */

package cv.inps.rh.shared.infrastructure.persistence.entity;

import cv.igrp.framework.stereotype.IgrpEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Immutable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;


@Getter
@Setter
@IgrpEntity
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Immutable
@Table(name = "RH_V_HIST_LABORAL")
public class RhVHistLaboralEntity  {

  @Id
  @Column(name = "tiprel_id", unique = true, nullable = false)
  private Long tiprelId;


  @Column(name="fun_id")
  private Long funId;


  @Column(name="fun_uuid")
  private UUID funUuid;


  @Column(name="contrato_id")
  private Long contratoId;


  @Column(name="carreira_id")
  private Long carreiraId;


  @Column(name="mob_id")
  private Long mobId;


  @Column(name="situacao_laboral_id")
  private Long situacaoLaboralId;


  @Column(name="regime_id")
  private Long regimeId;


  @Column(name="estado")
  private String estado;


  @Column(name="estado_desc")
  private String estadoDesc;


  @Column(name="tiprel_estado")
  private String tiprelEstado;


  @Column(name="vinculo_desc")
  private String vinculoDesc;


  @Column(name="vinculo_id")
  private Long vinculoId;


  @Column(name="tipo_contrato_desc")
  private String tipoContratoDesc;


  @Column(name="tipo_contrato_id")
  private Long tipoContratoId;


  @Column(name="tipo_situacao_desc")
  private String tipoSituacaoDesc;


  @Column(name="referencia_escalao_desc")
  private String referenciaEscalaoDesc;


  @Column(name="referencia_escalao_id")
  private Long referenciaEscalaoId;


  @Column(name="cargo_desc")
  private String cargoDesc;


  @Column(name="cargo_id")
  private Long cargoId;


  @Column(name="carreira_desc")
  private String carreiraDesc;


  @Column(name="carreira_pccs_id")
  private Long carreiraPccsId;


  @Column(name="categoria_desc")
  private String categoriaDesc;


  @Column(name="categoria_id")
  private Long categoriaId;


  @Column(name="seccao_desc")
  private String seccaoDesc;


  @Column(name="seccao_id")
  private Long seccaoId;


  @Column(name="direcao_desc")
  private String direcaoDesc;


  @Column(name="direcao_id")
  private Long direcaoId;


  @Column(name="centro_custo_desc")
  private String centroCustoDesc;


  @Column(name="centro_custo_id")
  private Long centroCustoId;


  @Column(name="data_inicio")
  private LocalDate dataInicio;


  @Column(name="data_fim")
  private LocalDate dataFim;


  @Column(name="estado_contrato")
  private String estadoContrato;


  @Column(name="tipo_mobilidade")
  private String tipoMobilidade;


  @Column(name="regime_trabalho_desc")
  private String regimeTrabalhoDesc;


  @Column(name="salario")
  private BigDecimal salario;


  @Column(name="moeda")
  private String moeda;


  @Column(name="duracao_contrato")
  private Long duracaoContrato;


  @Column(name="local_trabalho_desc")
  private String localTrabalhoDesc;


  @Column(name="local_trabalho_id")
  private Long localTrabalhoId;


  @Column(name="pais")
  private String pais;


  @Column(name="ilha")
  private String ilha;


  @Column(name="ultimo_proc")
  private Integer ultimoProc;


  @Column(name="ultimo_vinculo")
  private Integer ultimoVinculo;


  @Column(name="situacao_laboral_desc")
  private String situacaoLaboralDesc;


  @Column(name="referencia")
  private String referencia;


}
