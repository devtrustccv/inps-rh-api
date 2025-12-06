/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME */

package cv.inps.rh.processamento.application.dto;

import cv.igrp.framework.stereotype.IgrpDTO;
import cv.inps.rh.shared.application.constants.Estado;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor


@IgrpDTO
public class ColaboradorResponseDTO {


  private String estadoSituacaoLaboralDesc;


  private Estado estadoSituacaoLaboral;


  private String direccao;


  private String seccao;


  private String vinculo;


  private String categoria;


  private String motivo;


  private LocalDate dataInicio;


  private LocalDate dataFim;


  private UUID idFuncionario;


  private String nomeFuncionario;

}
