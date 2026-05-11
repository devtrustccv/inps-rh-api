/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME */

package cv.inps.rh.shared.infrastructure.persistence.entity;

import cv.igrp.framework.stereotype.IgrpEntity;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.config.AuditEntity;
import jakarta.persistence.*;
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
@Table(name = "RH_T_DEF_REMUNERACOES")
public class DefinicaoRemuneracaoEntity extends AuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_def_pag")
    @SequenceGenerator(name = "seq_def_pag", sequenceName = "SEQ_DEF_PAG", allocationSize = 1)
    @Column(name = "id", unique = true, nullable = false)
    private Long id;


  @Column(name="percentagem")
    private BigDecimal percentagem;


  @Column(name="valor")
    private BigDecimal valor;


  @Enumerated(EnumType.STRING)
    @Column(name="estado")
    private Estado estado;


  @Column(name="obs", length=4000)
    private String obs;


  @Column(name="uuid")
    private UUID uuid;


  @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tm_id", referencedColumnName = "id")
    private TipoMovimentoEntity tmId;
    @Column(name="moeda")
    private String moeda;


  @Column(name="data_inicio")
    private LocalDate dataInicio;


  @Column(name="data_fim")
    private LocalDate dataFim;


  @Column(name="data_ultimo_proc")
    private LocalDate dataUltimoProc;

     @ManyToOne(fetch = FetchType.LAZY)
   @JoinColumn(name = "fun_id")
   private FuncionarioEntity funId;


}
