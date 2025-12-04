/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME. */

package cv.inps.rh.shared.infrastructure.persistence.entity;

import cv.igrp.framework.stereotype.IgrpEntity;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.config.AuditEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;


@Getter
@Setter
@IgrpEntity
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "RH_T_PARAM_VINCULO")
public class ParamVinculoEntity extends AuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    // TODO 04/12/2025 14:18 chnage sequence
    @Column(name = "id", unique = true, nullable = false)
    private Long id;


    @Column(name="uuid")
    private UUID uuid;


    @NotBlank(message = "codigo is mandatory")
    @Column(name="codigo", nullable = false)
    private String codigo;


    @Column(name="nome")
    private String nome;


    @Column(name="flg_carreira")
    private Integer flgCarreira;


    @Column(name="flg_salario")
    private Integer flgSalario;


    @Column(name="flg_contrato")
    private Integer flgContrato;


    @Column(name="flg_tempo_servico")
    private Integer flgTempoServico;


    @Enumerated(EnumType.STRING)
    @Column(name="estado")
    private Estado estado;


}
