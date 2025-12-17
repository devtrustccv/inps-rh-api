/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME. */

package cv.inps.rh.shared.infrastructure.persistence.entity;

import cv.igrp.framework.stereotype.IgrpEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;


@Getter
@Setter
@IgrpEntity
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "RH_TIPO_MOVIMENTOS")
public class TipoMovimentoEntity  {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private Long id;


    @NotBlank(message = "descricao is mandatory")
    @Column(name="descricao", nullable = false)
    private String descricao;


    @Column(name="valor")
    private BigDecimal valor;


    @Column(name="percentagem")
    private BigDecimal percentagem;


    @Column(name="tipo")
    private String tipo;


    @Column(name="tipo_processamento")
    private String tipoProcessamento;


    @Column(name="iac_iac_id")
    private Long iacIacId;


    @Column(name="cobre_imp")
    private String cobreImp;


    @Column(name="social")
    private String social;


    @Column(name="calculo")
    private String calculo;


    @Column(name="favor_estado")
    private String favorEstado;


    @Column(name="iac_iac_rec_id")
    private Long iacIacRecId;


    @Column(name="actualiza_conta_corrente")
    private String actualizaContaCorrente;


    @Column(name="tipo_iur")
    private String tipoIur;


    @Column(name="amb_apl_id")
    private Long ambAplId;


    @Column(name="retencao")
    private String retencao;


    @Column(name="cc_id")
    private Long ccId;


    @Column(name="ent_id")
    private String entId;


    @Column(name="tipo_sal")
    private String tipoSal;


    @Column(name="acumulado")
    private String acumulado;


    @Column(name="short_desc")
    private String shortDesc;


    @Column(name="estado")
    private String estado;


}
