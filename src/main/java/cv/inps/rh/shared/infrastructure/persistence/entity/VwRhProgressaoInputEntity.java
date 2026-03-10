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
@Table(name = "VW_RH_PROGRESSAO_INPUT")
public class VwRhProgressaoInputEntity {

  @Id
  @NotNull
  @Column(name = "CARREIRA_ID", nullable = false)
  private Long carreiraId;

  @Size(max = 30)
  @Column(name = "TIPO_CARREIRA", length = 30)
  private String tipoCarreira;

  @NotNull
  @Column(name = "FUNCIONARIO_ID", nullable = false)
  private Long funcionarioId;

  @Size(max = 255)
  @NotNull
  @Column(name = "NOME_FUNCIONARIO", nullable = false)
  private String nomeFuncionario;

  @Column(name = "DATA_INICIO_CARREIRA")
  private LocalDate dataInicioCarreira;

  @Column(name = "CARGO_ID")
  private Long cargoId;

  @Column(name = "NIVEL_REFERENCIA")
  private Long nivelReferencia;

  @Size(max = 100)
  @Column(name = "ESCALAO", length = 100)
  private String escalao;

  @Column(name = "MEDIA_AVALIACOES")
  private Long mediaAvaliacoes;

  @Column(name = "EXISTE_EVOLUCAO")
  private Long existeEvolucao;

  @Column(name = "ATINGIU_PRIMEIRA_PROGRESSAO")
  private Long atingiuPrimeiraProgressao;

  @Column(name = "TEMPO_MIN_PROGRESSAO_ANOS")
  private Long tempoMinProgressaoAnos;

  @Column(name = "DATA_PROGRESSAO")
  private LocalDate dataProgressao;

  @Column(name = "ATINGIU_TEMP_MIN_PROGRESSAO")
  private Long atingiuTempMinProgressao;


}
