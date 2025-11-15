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
@Table(name = "RH_T_PARAM_SIT_LABORAL")
public class ParamSitLaboralEntity extends AuditEntity {

  @Id
  @SequenceGenerator(name = "seq_param_sit_laboral", sequenceName = "SEQ_PARAM_SIT_LABORAL", allocationSize = 1)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_param_sit_laboral")
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


    @Enumerated(EnumType.STRING)
    @Column(name="estado")
    private Estado estado;


}
