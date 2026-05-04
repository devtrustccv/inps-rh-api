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
@Table(name = "RH_V_DOSSIE")
public class RhVDossieEntity  {

  @Id
  @Column(name = "id")
  private Long id;

  @Column(name = "FUN_ID")
  private Long funId;

  @Column(name = "FUN_UUID")
  private UUID funUuid;

  @Column(name = "NOME")
  private String nome;

  @Column(name = "ULTIMO_VINCULO")
  private Integer ultimoVinculo;

  @Column(name = "ULTIMO_VINCULO_DESC")
  private String ultimoVinculoDesc;

  @Column(name = "ESTADO_COLABORADOR")
  private String estadoColaborador;

  @Column(name = "ID_COLABORADOR")
  private Long idColaborador;

  @Column(name = "CARGO_DESC")
  private String cargoDesc;

  @Column(name = "CARGO_ID")
  private Long cargoId;

  @Column(name = "CENTRO_CUSTO_DESC")
  private String centroCustoDesc;

  @Column(name = "CENTRO_CUSTO_ID")
  private Long centroCustoId;

  @Column(name = "DIRECAO_DESC")
  private String direcaoDesc;

  @Column(name = "DIRECAO_ID")
  private Long direcaoId;

  @Column(name = "SECCAO_DESC")
  private String seccaoDesc;

  @Column(name = "SECCAO_ID")
  private Long seccaoId;

  @Column(name = "CARREIRA_DESC")
  private String carreiraDesc;

  @Column(name = "CARREIRA_ID")
  private Long carreiraId;

  @Column(name = "ESCALAO_DESC")
  private String escalaoDesc;

  @Column(name = "ESCALAO_ID")
  private Long escalaoId;

  @Column(name = "CATEGORIA_DESC")
  private String categoriaDesc;

  @Column(name = "CATEGORIA_ID")
  private Long categoriaId;

  @Column(name = "VINCULO_DESC")
  private String vinculoDesc;

  @Column(name = "VINCULO_ID")
  private Long vinculoId;

  @Column(name = "DATA_INICIO_CONTRATO")
  private LocalDate dataInicioContrato;

  @Column(name = "DATA_FIM_CONTRATO")
  private LocalDate dataFimContrato;

  @Column(name = "DURACAO_CONTRATO")
  private String duracaoContrato;

  @Column(name = "LOCAL_TRABALHO_DESC")
  private String localTrabalhoDesc;

  @Column(name = "LOCAL_TRABALHO_ID")
  private Long localTrabalhoId;

  @Column(name = "PAIS_TRAB_DESC")
  private Long paisTrabDesc;

  @Column(name = "ILHA_TRAB_DESC")
  private Long ilhaTrabDesc;

  @Column(name = "TIPO_DOCUMENTO")
  private Long tipoDocumento;

  @Column(name = "NUM_DOCUMENTO")
  private String numDocumento;

  @Column(name = "DATA_NASCIMENTO")
  private LocalDate dataNascimento;

  @Column(name = "SEXO")
  private String sexo;

  @Column(name = "NM_MAE")
  private String nmMae;

  @Column(name = "NM_PAI")
  private String nmPai;

  @Column(name = "ESTADO_CIVIL")
  private String estadoCivil;

  @Column(name = "NACIONALIDADE")
  private String nacionalidade;

  @Column(name = "LOC_NASC_ID")
  private Long locNascId;

  @Column(name = "NIF")
  private String nif;

  @Column(name = "U_SEG_INPS")
  private String uSegInps;

  @Column(name = "CONTACTO")
  private String contacto;

  @Column(name = "ENDERECO")
  private String endereco;

  @Column(name = "REGIME_TRABALHO_DESC")
  private String regimeTrabalhoDesc;

  @Column(name = "VALOR")
  private BigDecimal valor;

  @Column(name = "MOEDA")
  private String moeda;

  @Column(name = "SITUACAO_LABORAL_ID")
  private Long situacaoLaboralId;

  @Column(name = "SITUACAO_LABORAL_DESC")
  private String situacaoLaboralDesc;

  @Column(name = "MOB_SITUACAO_DESC")
  private String mobSituacaoDesc;


}
