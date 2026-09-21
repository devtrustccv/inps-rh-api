package cv.inps.rh.shared.infrastructure.persistence.entity;

import cv.inps.rh.shared.config.AuditEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "RH_T_EMPRESTIMO_OUTROS")
public class EmprestimoOutroEntity extends AuditEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "RH_T_EMPRESTIMO_OUTROS_id_gen")
  @SequenceGenerator(name = "RH_T_EMPRESTIMO_OUTROS_id_gen", sequenceName = "SEQ_EMPRESTIMO_OUTRO", allocationSize = 1)
  @Column(name = "ID", nullable = false)
  private Long id;

  @Size(max = 100)
  @NotNull
  @Column(name = "TIPOS_EMPRESTIMO", nullable = false, length = 100)
  private String tiposEmprestimo;

  @Column(name = "DATA_INICIO")
  private LocalDate dataInicio;

  @Column(name = "DATA_FIM")
  private LocalDate dataFim;

  @NotNull
  @Column(name = "VALOR_EMPRESTIMO", nullable = false)
  private BigDecimal valorEmprestimo;

  @NotNull
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @OnDelete(action = OnDeleteAction.RESTRICT)
  @JoinColumn(name = "REFERENCIA_ORIGEM_ID", nullable = false)
  private PedidoEntity referenciaOrigem;

  @NotNull
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @OnDelete(action = OnDeleteAction.RESTRICT)
  @JoinColumn(name = "FUN_ID", nullable = false)
  private FuncionarioEntity fun;

  @Size(max = 1)
  @NotNull
  @Column(name = "ESTADO", nullable = false, length = 1)
  private String estado;

  @Size(max = 100)
  @NotNull
  @Column(name = "UUID", nullable = false, length = 100)
  private String uuid;

  @Column(name = "VALOR_PRESTACAO")
  private BigDecimal valorPrestacao;
}
