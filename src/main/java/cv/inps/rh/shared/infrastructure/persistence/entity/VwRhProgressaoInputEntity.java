package cv.inps.rh.shared.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.ToString;
import org.hibernate.annotations.Immutable;

import java.time.LocalDate;

@Getter
@Entity
@Immutable
@Table(name = "VW_PROGRESSAO_PROMOCAO")
@ToString
public class VwRhProgressaoInputEntity {

  @Id
  @NotNull
  @Column(name = "CARREIRA_ID", nullable = false)
  private Long carreiraId;

  @NotNull
  @Column(name = "FUNCIONARIO_ID", nullable = false)
  private Long funcionarioId;

  @Size(max = 255)
  @NotNull
  @Column(name = "NOME_FUNCIONARIO", nullable = false)
  private String nomeFuncionario;

  @Column(name = "TIPO_CARREIRA")
  private String tipoCarreira;

  @Column(name = "DATA_INICIO_CARREIRA")
  private LocalDate dataInicioCarreira;

  @Column(name = "NIVEL_REFERENCIA")
  private Long nivelReferencia;

  @Size(max = 100)
  @Column(name = "ESCALAO", length = 100)
  private String escalao;

  @Column(name = "MEDIA_AVALIACOES_3ANOS")
  private Long mediaAvaliacoes3Anos;

  @Column(name = "MEDIA_AVALIACOES_2ANOS")
  private Long mediaAvaliacoes2Anos;

  @Column(name = "EXISTE_PROGRESSAO")
  private Long existeProgressao;

  @Column(name = "RELACIONAMENTO_ID")
  private Long relacionamentoId;

  @Column(name = "ATINGIU_PRIMEIRA_PROGRESSAO")
  private Long atingiuPrimeiraProgressao;

  @Column(name = "TEMPO_MIN_PROGRESSAO_ANOS")
  private Long tempoMinProgressaoAnos;

  @Column(name = "DATA_PROGRESSAO")
  private LocalDate dataProgressao;

  @Column(name = "ATINGIU_TEMP_MIN_PROGRESSAO")
  private Long atingiuTempMinProgressao;
}
