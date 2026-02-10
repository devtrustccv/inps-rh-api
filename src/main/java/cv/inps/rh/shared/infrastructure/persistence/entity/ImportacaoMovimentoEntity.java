package cv.inps.rh.shared.infrastructure.persistence.entity;

import cv.inps.rh.shared.config.AuditEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

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

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "FUN_ID", nullable = false)
  private FuncionarioEntity funcionario;

  @Column(name = "TP_MOV_RETENCAO")
  private String tpMovRetencao;

  @Column(name = "TP_MOV_REM")
  private String tpMovRem;

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
}
