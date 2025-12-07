package cv.inps.rh.shared.infrastructure.persistence.entity;

import cv.inps.rh.shared.config.AuditEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "RH_T_PROC_FUNCIONARIOS")
public class ProcessamentoFuncionarioEntity extends AuditEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "RH_T_PROC_FUNCIONARIOS_id_gen")
  @SequenceGenerator(name = "RH_T_PROC_FUNCIONARIOS_id_gen", sequenceName = "SEQ_PROC_FUNCIONARIO", allocationSize = 1)
  @Column(name = "ID", nullable = false)
  private Long id;

  @NotNull
  @Column(name = "DATA_PROCESSAMENTO", nullable = false)
  private LocalDate dataProcessamento;

  @NotNull
  @Column(name = "DATA_REFERENCIA_DE", nullable = false)
  private LocalDate dataReferenciaDe;

  @Column(name = "DATA_REFERENCIA_ATE")
  private LocalDate dataReferenciaAte;

  @Column(name = "TOTAL_REMUNERACOES")
  private Long totalRemuneracoes;

  @Column(name = "TOTAL_PAGAMENTOS")
  private Long totalPagamentos;

  @Size(max = 3)
  @NotNull
  @Column(name = "ESTADO", nullable = false, length = 3)
  private String estado;

  @NotNull
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "PRSALS_ID", nullable = false)
  private ProcessamentoSalarialEntity prsals;

  @NotNull
  @Column(name = "TOT_REMUN_COLLECT", nullable = false)
  private Long totRemunCollect;

  @Column(name = "TOT_LIQUIDO")
  private Long totLiquido;

  @Column(name = "TOT_REMUN_SOCIAL")
  private Long totRemunSocial;

  @NotNull
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "TIPREL_ID", nullable = false)
  private TiposRelacionamentoEntity tiprel;

  @NotNull
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "RHB_ID", nullable = false)
  private DadosBancariosEntity rhb;

  @NotNull
  @Column(name = "NU_CONTA", nullable = false)
  private Long nuConta;

  @Size(max = 21)
  @NotNull
  @Column(name = "NIB", nullable = false, length = 21)
  private String nib;

  @Column(name = "CAB_1_ID")
  private Long cab1Id;

}
