package cv.inps.rh.shared.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import org.hibernate.annotations.Immutable;

import java.time.LocalDate;

/**
 * Vista RH_V_CONTRATO — 1 linha por situacao do contrato (registo de
 * RH_T_CONTRATO_HISTORICO). {@code HISTORICO_ID} e a chave unica da linha e nao
 * e exposta ao frontend; serve apenas de {@link Id} para o Hibernate nao
 * colapsar linhas com o mesmo CONTRATO_ID.
 */
@Getter
@Entity
@Immutable
@Table(name = "RH_V_CONTRATO")
public class RhVContratoEntity {

  @Id
  @Column(name = "HISTORICO_ID")
  private Long historicoId;

  @Column(name = "CONTRATO_ID")
  private Long contratoId;

  @Column(name = "CONTRATO_UUID")
  private String contratoUuid;

  @Column(name = "TIPO_CONTRATO")
  private String tipoContrato;

  @Column(name = "VINCULO")
  private String vinculo;

  @Column(name = "VINCULO_ID")
  private Long vinculoId;

  @Column(name = "DATA_INICIO")
  private LocalDate dataInicio;

  @Column(name = "DATA_FIM")
  private LocalDate dataFim;

  @Column(name = "FUN_ID")
  private Long funId;

  @Column(name = "FUN_UUID")
  private String funUuid;

  @Column(name = "ESTADO")
  private String estado;

  @Column(name = "DURACAO")
  private Integer duracao;

  @Column(name = "VERSAO")
  private Integer versao;

  @Column(name = "FLG_RENOVAVEL")
  private Integer flgRenovavel;

  @Column(name = "TIPO_SITUACAO")
  private String tipoSituacao;

  @Column(name = "TIPO_SITUACAO_DESC")
  private String tipoSituacaoDesc;

  @Column(name = "PROCESSAMENTO")
  private Integer processamento;
}
