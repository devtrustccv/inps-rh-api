package cv.inps.rh.shared.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import org.hibernate.annotations.Immutable;

@Getter
@Entity
@Immutable
@Table(name = "RH_V_SUBSIDIO_FERIAS")
public class VSubsidioFeriaEntity {

  @Id
  @NotNull
  @Column(name = "SUBSIDIO_ID", nullable = false)
  private Long subsidioId;

  @NotNull
  @Column(name = "FUN_ID", nullable = false)
  private Long funId;

  @NotNull
  @Column(name = "ANO_REFERENTE", nullable = false)
  private Integer anoReferente;

  @Size(max = 255)
  @NotNull
  @Column(name = "FUN_NOME", nullable = false)
  private String funNome;

  @Column(name = "VALOR_TOTAL")
  private Long valorTotal;

  @Column(name = "MESES_TOTAL")
  private Long mesesTotal;

  @Column(name = "DIAS_TOTAL")
  private Long diasTotal;

  @Column(name = "INSTIT_ID")
  private Long institId;

  @Size(max = 3)
  @NotNull
  @Column(name = "ESTADO", nullable = false, length = 3)
  private String estado;

  @Size(max = 20)
  @NotNull
  @Column(name = "FLG_ATIVO_INACTIVO", nullable = false, length = 20)
  private String flgAtivoInactivo;

  @Column(name = "SUMATORIA_DIAS_TOTAL")
  private Long sumatoriaDiasTotal;

  @Column(name = "VALOR_MES_TOTAL")
  private Long valorMesTotal;

  @Column(name = "DIAS_SUBSIDIO")
  private Long diasSubsidio;

  @Column(name = "VALOR_SUBSIDIO")
  private Long valorSubsidio;


}
