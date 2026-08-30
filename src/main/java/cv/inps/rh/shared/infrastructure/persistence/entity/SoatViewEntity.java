package cv.inps.rh.shared.infrastructure.persistence.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import org.hibernate.annotations.Immutable;

import java.time.LocalDate;

@Getter
@Entity
@Immutable
@Table(name = "RH_V_SOAT")
public class SoatViewEntity {

  @Id
  @Column(name = "ID")
  private Long id;

  @Size(max = 81)
  @Column(name = "DATA_PROC_PROVISORIO", length = 81)
  private String dataProcProvisorio;

  @NotNull
  @Column(name = "FUN_ID", nullable = false)
  private Long funId;

  @Size(max = 255)
  @NotNull
  @Column(name = "NOME", nullable = false)
  private String nome;

  @NotNull
  @Column(name = "TP_DOCUMENTO_ID", nullable = false)
  private Long tpDocumentoId;

  @Size(max = 255)
  @NotNull
  @Column(name = "NUM_DOCUMENTO", nullable = false)
  private String numDocumento;

  @Size(max = 255)
  @Column(name = "TP_DOCUMENTO")
  private String tpDocumento;

  @Column(name = "DATA_VALIDADE")
  private LocalDate dataValidade;

  @NotNull
  @Column(name = "NIF", nullable = false)
  private Long nif;

  @NotNull
  @Column(name = "DATA_NASCIMENTO", nullable = false)
  private LocalDate dataNascimento;

  @Size(max = 255)
  @NotNull
  @Column(name = "SEXO", nullable = false)
  private String sexo;

  @Size(max = 255)
  @Column(name = "CARGO_CARREIRA")
  private String cargoCarreira;

  @Size(max = 255)
  @NotNull
  @Column(name = "TIPO_CONTRATO", nullable = false)
  private String tipoContrato;

  @Column(name = "SALARIO_BASE")
  private Long salarioBase;

  @Column(name = "SALARIO_BASE_ANUAL")
  private Long salarioBaseAnual;

  @Column(name = "DIAS_TRAB_MES")
  private Long diasTrabMes;

  @Lob
  @Column(name = "DIAS_TRAB_SEMANA")
  private String diasTrabSemana;

  @Size(max = 4)
  @Column(name = "COLAB_NO_ESTRANGEIRO", length = 4)
  private String colabNoEstrangeiro;

  @Size(max = 500)
  @Column(name = "OBS", length = 500)
  private String obs;

  @NotNull
  @Column(name = "DIRECAO_ID", nullable = false)
  private Long direcaoId;

  @Size(max = 100)
  @NotNull
  @Column(name = "NOME_DIRECAO", nullable = false, length = 100)
  private String nomeDirecao;

  @Size(max = 100)
  @NotNull
  @Column(name = "SOAT_UUID", nullable = false, length = 100)
  private String soatUuid;

  @Size(max = 100)
  @NotNull
  @Column(name = "SOAT_DET_UUID", nullable = false, length = 100)
  private String soatDetUuid;


}
