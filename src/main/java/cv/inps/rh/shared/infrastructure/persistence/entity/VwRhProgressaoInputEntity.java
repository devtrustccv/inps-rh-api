package cv.inps.rh.shared.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.Immutable;

import java.time.LocalDate;

@Setter
@Getter
@Entity
@Immutable
@Table(name = "VW_PROGRESSAO_PROMOCAO")
@ToString
public class VwRhProgressaoInputEntity {

  @Id
  @NotNull
  @Column(name = "CARREIRA_ID")
  private Long carreiraId;
  @NotNull
  @Column(name = "FUNCIONARIO_ID")
  private Long funcionarioId;
  @Size(max = 255)
  @NotNull
  @Column(name = "NOME")
  private String nome;
  @Column(name = "DATA_INICIO")
  private LocalDate dataInicio;
  @Column(name = "NIVEL_REFERENCIA")
  private Long nivelReferencia;
  @Column(name = "ESCALAO")
  private String escalao;
  @Column(name = "MEDIA_3ANOS")
  private Long media3anos;
  @Column(name = "MEDIA_2ANOS")
  private Long media2anos;
  @Column(name = "RELACIONAMENTO_ID")
  private Long relacionamentoId;
  @Column(name = "TIPO_CARREIRA")
  private String tipoCarreira;
  @Column(name = "TEMPO_MIN_PROGRESSAO_ANOS")
  private int tempoMinProgressaoAnos;
  @Column(name = "EVOLUCAO_ATUAL")
  private String evolucaoAtual;
  @Column(name = "APTO_POR_FALTAS")
  private int aptoPorFaltas;
  @Column(name = "FALTAS_ANO_ATUAL")
  private int faltasAnoAtual;
  @Column(name = "FALTAS_ANO_ANTERIOR")
  private int faltasAnoAnterior;
  @Column(name = "PROC_ANO_ATUAL")
  private int processoAnoAtual;
  @Column(name = "PROC_ANO_ANTERIOR")
  private int processoAnoAnterior;
  @Column(name = "APTO_PROC_DISCIPLINAR")
  private int aptoPorProcessoDisciplinar;
}
