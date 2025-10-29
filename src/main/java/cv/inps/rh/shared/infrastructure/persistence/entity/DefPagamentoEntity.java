/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME. */

package cv.inps.rh.shared.infrastructure.persistence.entity;

import cv.inps.rh.shared.config.AuditEntity;
import cv.igrp.framework.stereotype.IgrpEntity;
import jakarta.persistence.*;
import lombok.*;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import cv.inps.rh.shared.application.constants.Estado;
import java.util.UUID;
import java.util.ArrayList;
import java.util.List;


@Getter
@Setter
@IgrpEntity
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "RH_T_DEF_PAGAMENTOS")
public class DefPagamentoEntity extends AuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private Long id;

  
    @NotNull(message = "tmId is mandatory")


  @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tm_id", referencedColumnName = "id")
    private TipoMovimentoEntity tmId;


  @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contrato_id", referencedColumnName = "id")
    private ContratoEntity contratoId;


  @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tiprel_id", referencedColumnName = "id")
    private TiposRelacionamentoEntity tiprelId;
    @Column(name="valor")
    private BigDecimal valor;

  
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

     @ManyToOne(fetch = FetchType.LAZY)
   @JoinColumn(name = "fun_id")
   private FuncionarioEntity funId;


}