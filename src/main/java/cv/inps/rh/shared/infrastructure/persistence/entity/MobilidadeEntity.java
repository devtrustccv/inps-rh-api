/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME. */

package cv.inps.rh.shared.infrastructure.persistence.entity;

import cv.inps.rh.shared.config.AuditEntity;
import cv.igrp.framework.stereotype.IgrpEntity;
import jakarta.persistence.*;
import lombok.*;
import jakarta.validation.constraints.NotBlank;
import cv.inps.rh.shared.application.constants.Estado;
import java.util.UUID;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


@Getter
@Setter
@IgrpEntity
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "RH_T_MOBILIDADE")
public class MobilidadeEntity extends AuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_mobilidade")
    @SequenceGenerator(name = "seq_mobilidade", sequenceName = "SEQ_MOBILIDADE", allocationSize = 1)
    @Column(name = "id", unique = true, nullable = false)
    private Long id;

  
    @NotBlank(message = "tipoSituacao is mandatory")
    @Column(name="tipo_situacao", nullable = false)
    private String tipoSituacao;

  
    @Enumerated(EnumType.STRING)
    @Column(name="estado")
    private Estado estado;

  
    @Column(name="obs")
    private String obs;

  
    @Column(name="uuid")
    private UUID uuid;

  


  @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contrato_id", referencedColumnName = "id")
    private ContratoEntity contratoId;


  @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "local_trab_id", referencedColumnName = "id")
    private ParamLocalTrabEntity localTrabId;


  @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "secao_id", referencedColumnName = "id")
    private SecaoEntity secaoId;


  @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "instid_id", referencedColumnName = "id")
    private InstituicaoEntity instidId;
    @Column(name="data_inicio")
    private LocalDate dataInicio;

  
    @Column(name="data_fim")
    private LocalDate dataFim;

     @ManyToOne(fetch = FetchType.LAZY)
   @JoinColumn(name = "fun_id")
   private FuncionarioEntity funId;


}