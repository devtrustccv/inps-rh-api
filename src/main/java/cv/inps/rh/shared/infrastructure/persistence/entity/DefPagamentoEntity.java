/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME. */

package cv.inps.rh.shared.infrastructure.persistence.entity;

import cv.igrp.framework.stereotype.IgrpEntity;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.config.AuditEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;


@Getter
@Setter
@IgrpEntity
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "RH_T_DEF_PAGAMENTOS")
public class DefPagamentoEntity extends AuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_def_rem")
    @SequenceGenerator(name = "seq_def_rem", sequenceName = "SEQ_DEF_REM", allocationSize = 1)
    @Column(name = "id", unique = true, nullable = false)
    private Long id;


    @NotNull(message = "tmId is mandatory")


  @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tm_id", referencedColumnName = "id")
    private TipoMovimentoEntity tmId;
    @Column(name="valor")
    private BigDecimal valor;

  @Column(name="percentagem", nullable = false)
  private BigDecimal percentagem;


    @Column(name="data_inicio")
    private LocalDate dataInicio;


    @Column(name="data_fim")
    private LocalDate dataFim;


    @Enumerated(EnumType.STRING)
    @Column(name="estado")
    private Estado estado;


    @Column(name="obs")
    private String obs;


    @Column(name="uuid")
    private UUID uuid;

  @Column(name="nib")
  private String nib;

  @Column(name="banco")
  private String banco;

  @Column(name="nif")
  private Integer nif;

  @Column(name="entidade")
  private String entidade;

     @ManyToOne(fetch = FetchType.LAZY)
   @JoinColumn(name = "fun_id")
   private FuncionarioEntity funId;


}
