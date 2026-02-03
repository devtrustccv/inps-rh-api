package cv.inps.rh.shared.infrastructure.persistence.entity;

import cv.inps.rh.shared.config.AuditEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "RH_T_PLANO_FINANCEIRO")
public class PlanoFinanceiroEntity extends AuditEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "RH_T_PLANO_FINANCEIRO_id_gen")
  @SequenceGenerator(name = "RH_T_PLANO_FINANCEIRO_id_gen", sequenceName = "SEQ_PLANO_FINANCEIRO", allocationSize = 1)
  @Column(name = "ID", nullable = false)
  private Long id;

  @NotNull
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "EMPRESTIMO_ID", nullable = false)
  private EmprestimoEntity emprestimo;

  @NotNull
  @Column(name = "NR_ORDEM_PRESTACAO", nullable = false)
  private Long nrOrdemPrestacao;

  @NotNull
  @Column(name = "DATA_PAGAMENTO", nullable = false)
  private LocalDate dataPagamento;

  @NotNull
  @Column(name = "VALOR_PRINCIPAL", nullable = false)
  private BigDecimal valorPrincipal;

  @Column(name = "VALOR_JUROS")
  private BigDecimal valorJuros;

  @Size(max = 10)
  @Column(name = "FLG_PAGO", length = 10)
  private String flgPago;

  @Column(name = "VALOR_PAGO")
  private BigDecimal valorPago;

  @Column(name = "DEFP_ID")
  private Long defpId;

  @Size(max = 1)
  @NotNull
  @Column(name = "ESTADO", nullable = false, length = 1)
  private String estado;

  @Size(max = 100)
  @NotNull
  @Column(name = "UUID", nullable = false, length = 100)
  private String uuid;

  @Column(name = "SALDO_INICIAL")
  private BigDecimal saldoInicial;

  @Column(name = "SALDO_FINAL")
  private BigDecimal saldoFinal;
}
