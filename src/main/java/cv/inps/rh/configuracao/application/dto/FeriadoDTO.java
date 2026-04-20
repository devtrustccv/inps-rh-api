/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME */

package cv.inps.rh.configuracao.application.dto;

import cv.igrp.framework.stereotype.IgrpDTO;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor


@IgrpDTO
public class FeriadoDTO  {


  private String idFeriado ;
  @NotBlank(message = "The field <descricao> is required")

  private String descricao ;


  private LocalDate dataEspecifica ;


  private Integer anoReferente ;


  private Long geogrId ;


  private String tipoFeriado ;


  private String fixoAno ;


  private Integer dia ;


  private Integer mes ;


  private String estado ;

}
