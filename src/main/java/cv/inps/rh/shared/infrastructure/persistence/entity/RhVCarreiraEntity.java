package cv.inps.rh.shared.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import org.hibernate.annotations.Immutable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Vista RH_V_CARREIRA — 1 linha por carreira (RH_T_CARREIRA). CARREIRA_ID e a
 * chave unica da linha (@Id). Nota: {@code situacaoLaboral} nao existe na vista
 * (vem do tiprel); sera adicionado ao entity quando a BD o expuser na vista.
 */
@Getter
@Entity
@Immutable
@Table(name = "RH_V_CARREIRA")
public class RhVCarreiraEntity {

  @Id
  @Column(name = "CARREIRA_ID")
  private Long carreiraId;

  @Column(name = "CARREIRA_UUID")
  private UUID carreiraUuid;

  @Column(name = "FUN_ID")
  private Long funId;

  @Column(name = "FUN_UUID")
  private UUID funUuid;

  @Column(name = "CONTRATO_ID")
  private Long contratoId;

  @Column(name = "TP_CONTRATO")
  private String tpContrato;

  @Column(name = "TIPO_CARREIRA")
  private String tipoCarreira;

  @Column(name = "VINCULO_DESC")
  private String vinculoDesc;

  @Column(name = "VINCULO_ID")
  private Long vinculoId;

  @Column(name = "CARREIRA_DESC")
  private String carreiraDesc;

  @Column(name = "CARGO_DESC")
  private String cargoDesc;

  @Column(name = "CARGO_ID")
  private Long cargoId;

  @Column(name = "ESCALAO_DESC")
  private String escalaoDesc;

  @Column(name = "ESCALAO_ID")
  private Long escalaoId;

  @Column(name = "SALARIO")
  private BigDecimal salario;

  @Column(name = "ESTADO_CARREIRA")
  private String estadoCarreira;

  @Column(name = "TIPO_SITUACAO")
  private String tipoSituacao;

  @Column(name = "TIPO_SITUACAO_DESC")
  private String tipoSituacaoDesc;

  @Column(name = "PROCESSAMENTO")
  private Integer processamento;

  @Column(name = "FLG_PROCESSA")
  private Integer flgProcessa;

  @Column(name = "ULTIMA_CARREIRA")
  private Integer ultimaCarreira;

  @Column(name = "DATA_INICIO")
  private LocalDate dataInicio;

  @Column(name = "DATA_FIM")
  private LocalDate dataFim;
}
