/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME */

package cv.inps.rh.transversal.application.dto;

import cv.igrp.framework.stereotype.IgrpDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor

@IgrpDTO
public class DossierRequestDTO {

  @Schema(description = "Mapa de filtros a aplicar. A chave é a dimensão e o valor é uma lista de valores para filtrar.", example = "{\"GENERO\": [\"F\"], \"DIRECAO\": [\"10\", \"12\"]}")
  private Map<String, List<String>> filtros;

  @Schema(description = "Lista ordenada de dimensões para agrupar o resultado.", example = "[\"DIRECAO\", \"GENERO\"]")
  private List<String> agrupadores;

}
