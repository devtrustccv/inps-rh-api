package cv.inps.rh.shared.infrastructure.persistence.entity;

import cv.inps.rh.shared.config.AuditEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "RH_T_PARAM_EMPRESTIMO")
public class ParamEmprestimoEntity extends AuditEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_param_emprestimo_seq")
  @SequenceGenerator(name = "seq_param_emprestimo_seq", sequenceName = "SEQ_PARAM_EMPRESTIMO", allocationSize = 1)
  @Column(name = "ID", nullable = false)
  private Long id;

  @NotNull
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "CARR_PCCS_ID", nullable = false)
  private ParamCarreiraEntity carrPccs;

  @Column(name = "VALOR_LIMITE")
  private BigDecimal valorLimite;

  @Column(name = "NUMERO_LIMITE")
  private Long numeroLimite;

  @Size(max = 1)
  @NotNull
  @Column(name = "ESTADO", nullable = false, length = 1)
  private String estado;

  @Size(max = 100)
  @NotNull
  @Column(name = "UUID", nullable = false, length = 100)
  private String uuid;
}
