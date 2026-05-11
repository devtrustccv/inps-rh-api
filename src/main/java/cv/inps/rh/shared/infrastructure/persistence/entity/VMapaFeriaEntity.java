/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME */

package cv.inps.rh.shared.infrastructure.persistence.entity;

import cv.igrp.framework.stereotype.IgrpEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@IgrpEntity
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "RH_V_FERIAS_MAPA")
public class VMapaFeriaEntity  {

    @Id
    @Column(name = "id", unique = true, nullable = false)
    private Long id;


    @NotNull(message = "anoReferente is mandatory")
    @Column(name="ano_referente", nullable = false)
    private Integer anoReferente;


    @Column(name="direcao_id")
    private Long direcaoId;


    @Column(name="direcao")
    private String direcao;


    @Column(name="secao_id")
    private Long secaoId;


    @Column(name="secao")
    private String secao;


    @Column(name="ilha")
    private String ilha;


    @Column(name="ilha_id")
    private Long ilhaId;


    @Column(name="total_colaborador")
    private Integer totalColaborador;


    @Column(name="total_ferias_agendadas")
    private Integer totalFeriasAgendadas;


    @Column(name="total_ferias_por_agendar")
    private Integer totalFeriasPorAgendar;


    @Column(name="estado")
    private String estado;


    @Column(name="estado_desc")
    private String estadoDesc;


}
