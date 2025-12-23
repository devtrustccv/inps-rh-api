/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME */

package cv.inps.rh.funcionario.interfaces.rest;

import cv.igrp.framework.core.domain.QueryBus;
import cv.igrp.framework.stereotype.IgrpController;
import cv.inps.rh.funcionario.application.queries.GetOrdemServicoQuery;
import cv.inps.rh.funcionario.application.queries.GetUrlTemporarioQuery;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;



@IgrpController
@RestController
@RequestMapping(path = "documento")
@Tag(
    name = "Funcionario",
    description = "Gerir documentos"
)
public class DocumentoController {


  private final QueryBus queryBus;

  public DocumentoController(QueryBus queryBus) {
    this.queryBus = queryBus;

  }

  @GetMapping(
   value = "{funcionarioId}"
  )
  @Operation(
      summary = "Get ordem servico",
      description = "Get ordem servico",
    responses = {
      @ApiResponse(
          responseCode = "200",

          content = @Content(
              mediaType = "application/json",
              schema = @Schema(
                  implementation = String.class,
                  type = "String")
          )
      )
    }
  )

  public ResponseEntity<String> getOrdemServico(
      @PathVariable(value = "funcionarioId") String funcionarioId)
  {

    final var query = new GetOrdemServicoQuery(funcionarioId);

    return queryBus.handle(query);

  }

  @GetMapping(
      value = "url-temporario"
  )
  @Operation(
      summary = "Get url temporario",
      description = "Get url temporario",
      responses = {
          @ApiResponse(
              responseCode = "200",

              content = @Content(
                  mediaType = "application/json",
                  schema = @Schema(
                      implementation = String.class,
                      type = "String")
              )
          )
      }
  )

  public ResponseEntity<String> getUrlTemporario(
  ) {

    final var query = new GetUrlTemporarioQuery();

    return queryBus.handle(query);

  }

}
