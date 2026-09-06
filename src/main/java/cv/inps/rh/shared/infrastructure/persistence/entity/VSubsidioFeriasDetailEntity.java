package cv.inps.rh.shared.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import org.hibernate.annotations.Immutable;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Entity
@Immutable
@Table(name = "RH_V_SUBSIDIO_FERIAS_DET")
public class VSubsidioFeriasDetailEntity {

  @Id
  @NotNull
  @Column(name = "FUN_ID", nullable = false)
  private Long funId;

  @NotNull
  @Column(name = "ANO_REFERENTE", nullable = false)
  private Integer anoReferente;

  @NotNull
  @Column(name = "ESCALAO_ID", nullable = false)
  private Long escalaoId;

  @NotNull
  @Column(name = "MES_TRAB", nullable = false)
  private Long mesTrab;

  @NotNull
  @Column(name = "DIAS_TRAB", nullable = false)
  private Long diasTrab;

  @NotNull
  @Column(name = "VALOR_SUBSIDIO", nullable = false)
  private Long valorSubsidio;

  @Size(max = 3)
  @NotNull
  @Column(name = "ESTADO", nullable = false, length = 3)
  private String estado;

  @Size(max = 255)
  @NotNull
  @Column(name = "FUN_NOME", nullable = false)
  private String funNome;

  @Size(max = 100)
  @NotNull
  @Column(name = "UUID", nullable = false, length = 100)
  private String uuid;

  @Column(name = "DATA_INICIO")
  private LocalDate dataInicio;

  @Size(max = 20)
  @Column(name = "DATA_FIM", length = 20)
  private String dataFim;

  @Column(name = "VALOR_MES")
  private Long valorMes;

  @Column(name = "VALOR_DIA")
  private Long valorDia;

  @Size(max = 100)
  @Column(name = "SITUACAO", length = 100)
  private String situacao;

  @Size(max = 140)
  @Column(name = "ESCALACAO_DESC", length = 140)
  private String escalacaoDesc;

  @Column(name = "VALOR_ESCALAO", precision = 38, scale = 2)
  private BigDecimal valorEscalao;

  @Column(name = "DIAS_TOTAL")
  private Long diasTotal;


}
