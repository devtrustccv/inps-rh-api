package cv.inps.rh.shared.infrastructure.persistence.entity;

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
@Table(name = "RH_T_PAGAMENTOS")
public class RhPagamentoEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "RH_T_PAGAMENTOS_id_gen")
  @SequenceGenerator(name = "RH_T_PAGAMENTOS_id_gen", sequenceName = "SEQ_PAGAMENTOS", allocationSize = 1)
  @Column(name = "ID", nullable = false)
  private Long id;

  @NotNull
  @Column(name = "VALOR", nullable = false)
  private BigDecimal valor;

  @NotNull
  @Column(name = "DATA_REF", nullable = false)
  private LocalDate dataRef;

  @Size(max = 3)
  @NotNull
  @Column(name = "ESTADO", nullable = false, length = 3)
  private String estado;

  @NotNull
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @OnDelete(action = OnDeleteAction.RESTRICT)
  @JoinColumn(name = "PRSAL_ID", nullable = false)
  private ProcessamentoFuncionarioEntity prsal;

  @NotNull
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @OnDelete(action = OnDeleteAction.RESTRICT)
  @JoinColumn(name = "DEFP_ID", nullable = false)
  private DefPagamentoEntity defp;

  @Column(name = "PERCENTAGEM")
  private Long percentagem;

  @Column(name = "ID_UPLOAD")
  private Long idUpload;
}
