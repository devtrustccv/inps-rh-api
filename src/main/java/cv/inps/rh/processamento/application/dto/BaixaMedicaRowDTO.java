/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME */

package cv.inps.rh.processamento.application.dto;

import cv.igrp.framework.stereotype.IgrpDTO;
import cv.inps.rh.shared.application.constants.Estado;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor


@IgrpDTO
public class BaixaMedicaRowDTO {


  private Estado estado;


  private String direcao;


  private String seccao;


  private String nome;


  private String vinculo;


  private String categoria;


  private String tipoLicensa;


  private String motivo;


  private LocalDate dataInicio;


  private LocalDate dataFim;

}
