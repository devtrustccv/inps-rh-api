/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME. */

package cv.inps.rh.shared.infrastructure.persistence.entity;

import cv.inps.rh.shared.config.AuditEntity;
import cv.igrp.framework.stereotype.IgrpEntity;
import jakarta.persistence.*;
import lombok.*;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDate;
import java.util.UUID;


@Getter
@Setter
@IgrpEntity
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "RH_T_SITUACAO_LABORAL")
public class SituacaoLaboralEntity extends AuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private Long id;

  
    @NotBlank(message = "situacaoLaboral is mandatory")
    @Column(name="situacao_laboral", nullable = false)
    private String situacaoLaboral;

  
    @Column(name="motivo_sit_lab")
    private String motivoSitLab;

  
    @Column(name="data_inicio")
    private LocalDate dataInicio;

  
    @Column(name="data_fim")
    private LocalDate dataFim;

  


  @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fun_id", referencedColumnName = "id")
    private FuncionarioEntity funId;


  @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contrato_id", referencedColumnName = "id")
    private ContratoEntity contratoId;
    @Column(name="estado")
    private String estado;

  
    @Column(name="obs", length=4000)
    private String obs;

  
    @Column(name="uuid")
    private UUID uuid;

  
}