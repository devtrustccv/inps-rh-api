/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME. */

package cv.inps.rh.configuracao.application.dto;

import cv.igrp.framework.stereotype.IgrpDTO;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor


@IgrpDTO
public class EscalaoRequestDTO  {

  @NotBlank(message = "The field <escalao> is required")

  private String escalao ;
  @NotNull(message = "The field <nivelReferencia> is required")

  private Integer nivelReferencia ;
  @NotBlank(message = "The field <carreiraId> is required")

  private String carreiraId ;


  private String categoriaId ;
  @NotBlank(message = "The field <salario> is required")

  private String salario ;
  @NotNull(message = "The field <dataInicio> is required")

  private LocalDate dataInicio ;


  private LocalDate dataFim ;


  private String estado ;

}
