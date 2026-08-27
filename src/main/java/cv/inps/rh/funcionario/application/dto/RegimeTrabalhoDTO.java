/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME. */

package cv.inps.rh.funcionario.application.dto;

import cv.igrp.framework.stereotype.IgrpDTO;
import cv.inps.rh.shared.application.constants.EstadoValidacao;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
public class RegimeTrabalhoDTO  {



  private EstadoValidacao validar ;


  @NotBlank(message = "The field tipoRegime is required")
  private String tipoRegime ;


  @NotNull(message = "The field dataInicio is required")
  private LocalDate dataInicio ;


  @NotNull(message = "The field dataFim is required")
  private LocalDate dataFim ;


  private String estado ;

  @Valid
  private List<RegimeModalidadeDTO> regimeModalidade = new ArrayList<>();

  private String tipoOrdemServico ;

}
