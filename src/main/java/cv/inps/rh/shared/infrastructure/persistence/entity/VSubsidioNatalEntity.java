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
@Table(name = "RH_V_SUBSIDIO_NATAL")
public class VSubsidioNatalEntity {
  @Id
  @NotNull
  @Column(name = "ID", nullable = false)
  private Long id;

  @Size(max = 100)
  @NotNull
  @Column(name = "UUID", nullable = false, length = 100)
  private String uuid;

  @NotNull
  @Column(name = "FUN_ID", nullable = false)
  private Long funId;

  @Size(max = 255)
  @NotNull
  @Column(name = "NOME", nullable = false)
  private String nome;

  @Column(name = "SALARIO")
  private Long salario;

  @Column(name = "INSTIT_ID")
  private Long institId;

  @NotNull
  @Column(name = "ANO_REFERENTE", nullable = false)
  private Long anoReferente;

  @NotNull
  @Column(name = "MES_TRAB", nullable = false)
  private Long mesTrab;

  @Column(name = "PERC_SALARIO")
  private Long percSalario;

  @Column(name = "FALTAS")
  private Long faltas;

  @Column(name = "PERC_FALTA")
  private Long percFalta;

  @NotNull
  @Column(name = "VALOR_SUBSIDIO", nullable = false)
  private Long valorSubsidio;

  @Column(name = "VALOR_CHEQUE_BRINDE")
  private Long valorChequeBrinde;

  @Column(name = "VALOR_PRENDA_NATAL")
  private Long valorPrendaNatal;

  @Column(name = "OBSERVACAO")
  private String observacao;

  @Size(max = 20)
  @NotNull
  @Column(name = "FLG_ATIVO_INACTIVO", nullable = false, length = 20)
  private String flgAtivoInactivo;

  @Size(max = 3)
  @NotNull
  @Column(name = "ESTADO", nullable = false, length = 3)
  private String estado;


}
