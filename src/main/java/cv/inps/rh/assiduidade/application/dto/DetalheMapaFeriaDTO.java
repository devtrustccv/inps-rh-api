/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME */

package cv.inps.rh.assiduidade.application.dto;

import cv.igrp.framework.stereotype.IgrpDTO;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor


@IgrpDTO
public class DetalheMapaFeriaDTO  {


  @Valid
  private List<FeriasAgendadasDTO> feriasAgendadas = new ArrayList<>();

  @Valid
  private List<FeriasPorAgendarDTO> feriasPorAgendar = new ArrayList<>();

}
