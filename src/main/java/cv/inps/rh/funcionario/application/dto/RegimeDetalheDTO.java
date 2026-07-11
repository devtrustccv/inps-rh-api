/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME. */

package cv.inps.rh.funcionario.application.dto;

import cv.igrp.framework.stereotype.IgrpDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor


@IgrpDTO
public class RegimeDetalheDTO  {


  private Long id ;


  private String uuid ;


  private Long funcionarioId ;


  private String funcionarioUuid ;


  private String tipoRegime ;


  private LocalDate dataInicio ;


  private LocalDate dataFim ;


  private String estado ;


  private String estadoDesc ;


  private String obs ;


  private List<RegimeModalidadeDTO> regimeModalidade = new ArrayList<>();

}
