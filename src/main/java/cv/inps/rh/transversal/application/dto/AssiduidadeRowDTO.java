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

    private String direccao;
    private String seccao;
    private String colaborador;

    // Ferias
    private Integer numDiasFerias;
    private String periodo;

    // Falta
    private Integer numFaltas;

    // Hora Extra
    private Long numHorasExtras;

    // Dispensa
    private Long horasDispensaGozadas;
    private Long horasDispensaPorGozar;

}
