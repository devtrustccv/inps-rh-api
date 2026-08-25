/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME */

package cv.inps.rh.processamento.interfaces.rest;

import cv.igrp.framework.core.domain.CommandBus;
import cv.igrp.framework.core.domain.QueryBus;
import cv.igrp.framework.stereotype.IgrpController;
import cv.inps.rh.configuracao.application.services.model.WrapperListDTO;
import cv.inps.rh.processamento.application.queries.GetSoatListQuery;
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
@RequestMapping(path = "soat")
@Tag(
    name = "Processamento",
    description = "Operações SOAT"
)
public class SoatController {


  private final QueryBus queryBus;
  private final CommandBus commandBus;

  public SoatController(QueryBus queryBus, CommandBus commandBus) {
    this.queryBus = queryBus;
    this.commandBus = commandBus;
  }

  @GetMapping(
  )
  @Operation(
      summary = "Get lista SOAT",
      description = "Get lista SOAT",
      responses = {
          @ApiResponse(
              responseCode = "200",
              content = @Content(
                  mediaType = "application/json",
                  schema = @Schema(
                      implementation = WrapperListDTO.class,
                      type = "object")
              )
          )
      }
  )

  public ResponseEntity<WrapperListDTO> getListaFos(
      @RequestParam(value = "anoReferente", required = false) Integer anoReferente,
      @RequestParam(value = "mesReferente", required = false) Integer mesReferente,
      @RequestParam(value = "page", required = false, defaultValue = "0") String page,
      @RequestParam(value = "size", required = false, defaultValue = "20") String size
  ) {

    final var query = new GetSoatListQuery(anoReferente, mesReferente, Integer.valueOf(page), Integer.valueOf(size));

    return queryBus.handle(query);

  }
}
