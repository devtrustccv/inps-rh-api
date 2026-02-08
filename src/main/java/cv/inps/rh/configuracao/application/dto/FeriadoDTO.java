/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME */

package cv.inps.rh.configuracao.application.dto;

import java.time.LocalDate;

import cv.igrp.framework.stereotype.IgrpDTO;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor


@IgrpDTO
public class FeriadoDTO {


  private String idFeriado;
  @NotBlank(message = "The field <descricao> is required")

  private String descricao;
  @NotNull(message = "The field <dataEspecifica> is required")

  private LocalDate dataEspecifica;

  private Integer anoReferente;

  private Long geogrId;

  private String tipoFeriado;

  private String fixoAno;

  private Integer dia;

  private Integer mes;

  private String estado;

}
