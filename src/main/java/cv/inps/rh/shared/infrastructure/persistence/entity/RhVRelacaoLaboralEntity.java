package cv.inps.rh.shared.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import org.hibernate.annotations.Immutable;

import java.time.LocalDate;

@Getter
@Entity
@Immutable
@Table(name = "RH_V_RELACAO_LABORAL")
public class RhVRelacaoLaboralEntity {

  @Id
  @Size(max = 36)
  @Column(name = "FUNCIONARIO_UUID", length = 36)
  private String funcionarioUuid;

  @Size(max = 255)
  @NotNull
  @Column(name = "NOME_COLABORADOR", nullable = false)
  private String nomeColaborador;

  @Size(max = 255)
  @NotNull
  @Column(name = "CONTRATO_DESC", nullable = false)
  private String contratoDesc;

  @NotNull
  @Column(name = "CONTRATO_ID", nullable = false)
  private Long contratoId;

  @Size(max = 255)
  @Column(name = "VINCULO_DESC")
  private String vinculoDesc;

  @Column(name = "VINCULO_ID")
  private Long vinculoId;

  @Size(max = 150)
  @Column(name = "DIRECAO_DESC", length = 150)
  private String direcaoDesc;

  @Column(name = "DIRECAO_ID")
  private Long direcaoId;

  @Size(max = 255)
  @Column(name = "SECCAO_DESC")
  private String seccaoDesc;

  @Column(name = "SECCAO_ID")
  private Long seccaoId;

  @Size(max = 255)
  @Column(name = "CARREIRA_DESC")
  private String carreiraDesc;

  @Column(name = "CARREIRA_ID")
  private Long carreiraId;

  @Size(max = 36)
  @Column(name = "CARREIRA_UUID", length = 36)
  private String carreiraUuid;

  @Column(name = "EST_ACT_ADM")
  private Long estActAdm;

  @Size(max = 141)
  @Column(name = "ESCALAO_DESC", length = 141)
  private String escalaoDesc;

  @Column(name = "ESCALAO_ID")
  private Long escalaoId;

  @Size(max = 39)
  @Column(name = "DATA_CARREIRA", length = 39)
  private String dataCarreira;

  @Size(max = 39)
  @Column(name = "DATA_CONTRATO", length = 39)
  private String dataContrato;

  @Size(max = 255)
  @Column(name = "CARGO_DESC")
  private String cargoDesc;

  @Column(name = "CARGO_ID")
  private Long cargoId;

  @Size(max = 255)
  @Column(name = "SITUACAO_LABORAL_DESC")
  private String situacaoLaboralDesc;

  @Column(name = "SITUACAO_LABORAL_ID")
  private Long situacaoLaboralId;

  @Column(name = "DATA_INICIO_SITUACAO")
  private LocalDate dataInicioSituacao;

  @Column(name = "DATA_FIM_SITUACAO")
  private LocalDate dataFimSituacao;

  @Column(name = "DATA_INICIO_MOBILIDADE")
  private LocalDate dataInicioMobilidade;

  @Column(name = "DATA_FIM_MOBILIDADE")
  private LocalDate dataFimMobilidade;

  @Size(max = 255)
  @Column(name = "TIPO_SITUACAO_MOBILIDADE")
  private String tipoSituacaoMobilidade;

  @Size(max = 255)
  @Column(name = "TIPO_SITUACAO_LABORAL")
  private String tipoSituacaoLaboral;

  @Size(max = 255)
  @Column(name = "TIPO_SITUACAO_CARREIRA")
  private String tipoSituacaoCarreira;

  @Size(max = 100)
  @Column(name = "LOCAL_TRAB_ILHA", length = 100)
  private String localTrabIlha;

  @Column(name = "LOCAL_UPS")
  private Long localUps;

  @Column(name = "CONCELHO_UPS")
  private Long concelhoUps;

  @Column(name = "nivel_detalhe")
  private Long nivelDetalhe;
}
