/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME */

package cv.inps.rh.shared.infrastructure.persistence.entity;

import cv.inps.rh.shared.config.AuditEntity;
import cv.igrp.framework.stereotype.IgrpEntity;
import jakarta.persistence.*;
import lombok.*;
import jakarta.validation.constraints.NotBlank;
import java.util.UUID;
import cv.inps.rh.shared.application.constants.Estado;


@Getter
@Setter
@IgrpEntity
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "RH_T_PARAM_SITUACAO")
public class ParamSituacaoEntity extends AuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_param_situacao")
    @SequenceGenerator(name = "seq_param_situacao", sequenceName = "SEQ_PARAM_SITUACAO", allocationSize = 1)
    @Column(name = "id", unique = true, nullable = false)
    private Long id;


    @Column(name="flg_falta_deconto_sal")
    private Integer flgFaltaDecontoSal;


    @NotBlank(message = "codigo is mandatory")
    @Column(name="codigo", nullable = false)
    private String codigo;


    @Column(name="nome")
    private String nome;


    @Column(name="tipo_situacao")
    private String tipoSituacao;


    @Column(name="flg_remuneracao")
    private Integer flgRemuneracao;

    @Column(name="flg_afeta_carreira")
    private Integer flgAfetaCarreira;


    @Column(name="flg_conta_temp_servico")
    private Integer flgContaTempServico;


    @Column(name="flg_cessa_progressao")
    private Integer flgCessaProgressao;


    @Column(name="flg_estado_contrato")
    private String  flgEstadoContrato;


    @Column(name="tipo_ausencia")
    private String tipoAusencia;


    @Column(name="classificacao_area")
    private String classificacaoArea;


    @Column(name="flg_abono_beneficio")
    private Integer flgAbonoBeneficio;


    @Column(name="flg_situacao_laboral")
    private Integer flgSituacaoLaboral;


    @Column(name="uuid")
    private UUID uuid;


    @Enumerated(EnumType.STRING)
    @Column(name="estado")
    private Estado estado;


    @Column(name="tipo_contagem_dias")
    private String tipoContagemDias;


    @Column(name="num_dias_abonos")
    private Integer numDiasAbonos;


    @Column(name="num_dias_desconto_rh")
    private Integer  numDiasDescontoRh;


    @Column(name="num_dias_ndesconto_rh")
    private Integer numDiasNdescontoRh;


    @Column(name="flg_ausencia")
    private Integer flgAusencia;


    @Column(name="flg_cessa_vinculo")
    private Integer flgCessaVinculo;


    @Column(name="flg_regressa_carreira")
    private Integer flgRegressaCarreira;


    @Column(name="tipo_falta")
    private String tipoFalta;


}
