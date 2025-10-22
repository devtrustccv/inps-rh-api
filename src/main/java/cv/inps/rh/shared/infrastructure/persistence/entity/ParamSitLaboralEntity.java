/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME. */

package cv.inps.rh.shared.infrastructure.persistence.entity;

import cv.inps.rh.shared.config.AuditEntity;
import cv.igrp.framework.stereotype.IgrpEntity;
import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;
import jakarta.validation.constraints.NotBlank;


@Getter
@Setter
@IgrpEntity
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "RH_T_PARAM_SIT_LABORAL")
public class ParamSitLaboralEntity extends AuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private Long id;

  
    @Column(name="uuid")
    private UUID uuid;

  
    @NotBlank(message = "codigo is mandatory")
    @Column(name="codigo", nullable = false)
    private String codigo;

  
    @Column(name="nome")
    private String nome;

  
    @Column(name="tipo_situacao")
    private String tipoSituacao;

  
    @Column(name="flg_renumeracao")
    private Integer flgRenumeracao;

  
    @Column(name="flg_afeta_carreira")
    private Integer flgAfetaCarreira;

  
    @Column(name="flg_conta_temp_servico")
    private Integer flgContaTempServico;

  
    @Column(name="flg_cessa_progressao")
    private Integer flgCessaProgressao;

  
    @Column(name="flg_estado_contrato")
    private Integer flgEstadoContrato;

  
    @Column(name="estado")
    private String estado;

  
}