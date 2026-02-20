package cv.inps.rh.transversal.application.dto;

import cv.igrp.framework.stereotype.IgrpDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@IgrpDTO
public class AssiduidadeRowDTO {
    
    private String id;
    private String direccao;
    private String seccao;
    private String colaborador;
    
    // Ferias
    private Integer numDiasFerias;
    private String periodoFerias;
    private String quemEstaDeFerias;
    private String feriasSuspensas;
    private String feriasAcumuladas;
    
    // Falta
    private Integer numFaltas;
    private String falta;
    
    // Hora Extra
    private Double numHorasExtras;
    private String horasExtraAutoVsReal;
    private Double custoHorasExtra;
    
    // Dispensa
    private String horasDispensa;

}
