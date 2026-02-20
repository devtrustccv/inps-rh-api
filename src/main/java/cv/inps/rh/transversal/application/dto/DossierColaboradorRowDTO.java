package cv.inps.rh.transversal.application.dto;

import cv.igrp.framework.stereotype.IgrpDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@IgrpDTO
public class DossierColaboradorRowDTO {

    private String id;
    private String direccao;
    private String seccao;
    private String cargo;
    private Integer idade;
    private String genero;
    private String faixaEtaria;
    private String localTrabalho;
    private String carreira;
    private String escalao;
    private String categoria;
    private String grauEscolaridade;
    private String mobilidade;
    private String estruturaRemuneratoria;
    private String vinculo;
    private String situacaoLaboral;

}
