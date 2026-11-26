package cv.inps.rh.shared.infrastructure.persistence.entity;

import cv.inps.rh.shared.config.AuditEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "RH_T_IMPORTACAO_MOVIMENTO")
public class ImportacaoMovimentoEntity extends AuditEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "mov_seq")
  @SequenceGenerator(
      name = "mov_seq",
      sequenceName = "RH_SEQ_IMPORTACAO_MOVIMENTO",
      allocationSize = 1
  )
  @Column(name = "ID")
  private Long id;

  @NotNull
  @Column(name = "UUID")
  private UUID uuid;

  @NotBlank
  @Column(name = "ESTADO")
  private String estado;

  @NotBlank
  @Column(name = "NOME_FICHEIRO")
  private String nomeFicheiro;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "FUN_ID", nullable = false)
  private FuncionarioEntity funcionario;

  /** Id do tipo de movimento de retenção (INPSSIGOF.RH_TIPO_MOVIMENTOS.ID). */
  @Column(name = "TP_MOV_RETENCAO")
  private Long tpMovRetencao;

  /** Id do tipo de movimento de remuneração (INPSSIGOF.RH_TIPO_MOVIMENTOS.ID). */
  @Column(name = "TP_MOV_REM")
  private Long tpMovRem;

  @Column(name = "PERCENTAGEM")
  private BigDecimal percentagem;

  @Column(name = "VALOR")
  private BigDecimal valor;

  @Column(name = "DATA_INICIO")
  private LocalDate dataInicio;

  @Column(name = "DATA_FIM")
  private LocalDate dataFim;

  @Column(name = "SITUACAO")
  private String situacao;

  /** Identificador do ficheiro importado: igual em todas as linhas do mesmo upload (RH_SEQ_IMP_MOV_CODIGO). */
  @Column(name = "CODIGO")
  private Long codigo;

  /** Nome do colaborador tal como vem no ficheiro (coluna B). */
  @Column(name = "NOME_FUNCIONARIO", length = 300)
  private String nomeFuncionario;

  /** Vínculo atual do colaborador no momento da importação ("Vínculo (Situação)" da lista). */
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "TIPREL_ID")
  private TiposRelacionamentoEntity tiprel;
}
