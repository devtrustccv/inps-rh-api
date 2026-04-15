/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME */

package cv.inps.rh.processamento.interfaces.rest;

import cv.igrp.framework.core.domain.QueryBus;
import cv.igrp.framework.stereotype.IgrpController;
import cv.inps.rh.processamento.application.dto.ListaFosDTO;
import cv.inps.rh.processamento.application.queries.GetListaFosQuery;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@IgrpController
@RestController
@RequestMapping(path = "fos")
@Tag(
    name = "Processamento",
    description = "Operações Fos"
)
public class FosController {


  private final QueryBus queryBus;

  public FosController(QueryBus queryBus) {
    this.queryBus = queryBus;

  }

  @GetMapping(
  )
  @Operation(
      summary = "Get lista fos",
      description = "Get lista fos",
      responses = {
          @ApiResponse(
              responseCode = "200",

              content = @Content(
                  mediaType = "application/json",
                  schema = @Schema(
                      implementation = ListaFosDTO.class,
                      type = "object")
              )
          )
      }
  )

  public ResponseEntity<ListaFosDTO> getListaFos(
      @RequestParam(value = "dataInicio", required = false) String dataInicio,
      @RequestParam(value = "dataFim", required = false) String dataFim,
      @RequestParam(value = "page", required = false, defaultValue = "0") String page,
      @RequestParam(value = "size", required = false, defaultValue = "20") String size) {

    final var query = new GetListaFosQuery(dataInicio, dataFim, page, size);

    return queryBus.handle(query);

  }

}
