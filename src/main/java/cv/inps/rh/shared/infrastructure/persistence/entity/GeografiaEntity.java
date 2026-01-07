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


@Getter
@Setter
@IgrpEntity
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "GLB_T_GEOGRAFIA", schema = "SIPSGLOBAL")
public class GeografiaEntity  {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private Long id;


    @NotBlank(message = "nome is mandatory")
    @Column(name="nome", nullable = false)
    private String nome;


    @Column(name="nacionalidade")
    private String nacionalidade;


    @Column(name="geogr_id")
    private Long geogrId;


    @Column(name="pais")
    private Long pais;


    @Column(name="nivel_detalhe")
    private Long nivelDetalhe;


    @Column(name="nome_oficial")
    private String nomeOficial;


    @Column(name="flag_alter")
    private String flagAlter;


    @Column(name="nome_norm")
    private String nomeNorm;


    @Column(name="tp_geog_cd")
    private String tpGeogCd;


    @Column(name="flg_situacao")
    private String flgSituacao;


}
