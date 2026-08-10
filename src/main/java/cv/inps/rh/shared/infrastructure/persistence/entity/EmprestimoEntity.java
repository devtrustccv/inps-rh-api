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
@Table(name = "RH_T_EMPRESTIMO")
public class EmprestimoEntity extends AuditEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "RH_T_EMPRESTIMO_id_gen")
  @SequenceGenerator(name = "RH_T_EMPRESTIMO_id_gen", sequenceName = "SEQ_EMPRESTIMO", allocationSize = 1)
  @Column(name = "ID", nullable = false)
  private Long id;

  @NotNull
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @OnDelete(action = OnDeleteAction.RESTRICT)
  @JoinColumn(name = "TIPREL_ID", nullable = false)
  private TiposRelacionamentoEntity tiprel;

  @Size(max = 100)
  @Column(name = "MARCA", length = 100)
  private String marca;

  @Column(name = "ANO_FABRICO")
  private Long anoFabrico;

  @Size(max = 100)
  @Column(name = "CILINCRADA", length = 100)
  private String cilincrada;

  @Size(max = 50)
  @Column(name = "TIPO_VIATURA", length = 50)
  private String tipoViatura;

  @Size(max = 50)
  @Column(name = "COMBUSTIVEL", length = 50)
  private String combustivel;

  @Size(max = 50)
  @Column(name = "ESTADO_VIATURA", length = 50)
  private String estadoViatura;

  @NotNull
  @Column(name = "VALOR_EMPRESTIMO", nullable = false)
  private BigDecimal valorEmprestimo;

  @Column(name = "VALOR_DIVIDA")
  private BigDecimal valorDivida;

  @NotNull
  @Column(name = "NR_PRESTACAO", nullable = false)
  private Long nrPrestacao;

  @Size(max = 100)
  @NotNull
  @Column(name = "TIPO_EMPRESTIMO", nullable = false, length = 100)
  private String tipoEmprestimo;

  @Column(name = "DATA_INICIO")
  private LocalDate dataInicio;

  @Column(name = "DATA_FIM")
  private LocalDate dataFim;

  @Column(name = "JURO")
  private BigDecimal juro;

  @Column(name = "VALOR_PRESTACAO")
  private BigDecimal valorPrestacao;

  @Size(max = 300)
  @Column(name = "DESC_CABIMENTACAO_ORCAMENTAL", length = 300)
  private String descCabimentacaoOrcamental;

  @Size(max = 500)
  @Column(name = "DESC_TAXA_ESFORCO", length = 500)
  private String descTaxaEsforco;

  @ManyToOne(fetch = FetchType.LAZY)
  @OnDelete(action = OnDeleteAction.RESTRICT)
  @JoinColumn(name = "EMPRESTIMO_ID")
  private EmprestimoEntity emprestimo;

  @Column(name = "VERSAO")
  private Long versao;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "PEDIDO_ID")
  private PedidoEntity pedido;

  @Size(max = 10)
  @Column(name = "RENOGOCIACAO", length = 10)
  private String renogociacao;

  @Column(name = "TM_ID")
  private Long tmId;

  @Size(max = 100)
  @Column(name = "FINALIDADE", length = 100)
  private String finalidade;

  @Column(name = "VALOR_ADIANTADO")
  private BigDecimal valorAdiantado;

  @Size(max = 100)
  @Column(name = "TIPO_RENOGOCIACAO", length = 100)
  private String tipoRenogociacao;

  @Size(max = 100)
  @Column(name = "MOTIVO_FECHO", length = 100)
  private String motivoFecho;

  @Column(name = "VALOR_REFORCO")
  private BigDecimal valorReforco;

  @Column(name = "VALOR_PAGO")
  private BigDecimal valorPago;

  @Size(max = 200)
  @Column(name = "MOTIVO", length = 200)
  private String motivo;

  @Size(max = 100)
  @Column(name = "TIPO_SITUACAO", length = 100)
  private String tipoSituacao;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "BANCO")
  private BancoEntity banco;

  @Size(max = 21)
  @Column(name = "NIB", length = 21)
  private String nib;

  @Size(max = 9)
  @Column(name = "NIF", length = 9)
  private String nif;

  @Size(max = 50)
  @Column(name = "SWIFT", length = 50)
  private String swift;

  @NotNull
  @Column(name = "ESTADO", nullable = false, length = 25)
  private String estado;

  @Size(max = 100)
  @NotNull
  @Column(name = "UUID", nullable = false, length = 100)
  private String uuid;

  @Column(name = "VALOR_JURO_TOTAL")
  private BigDecimal valorJuroTotal;
}
