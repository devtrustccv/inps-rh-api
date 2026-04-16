/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME */

package cv.inps.rh.processamento.interfaces.rest;

import cv.igrp.framework.core.domain.CommandBus;
import cv.igrp.framework.core.domain.QueryBus;
import cv.igrp.framework.stereotype.IgrpController;
import cv.inps.rh.processamento.application.commands.NovoSeguradoCommand;
import cv.inps.rh.processamento.application.dto.ListaFosDTO;
import cv.inps.rh.processamento.application.queries.GetListaFosQuery;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@IgrpController
@RestController
@RequestMapping(path = "fos")
@Tag(
    name = "Processamento",
    description = "Operações Fos"
)
public class FosController {


  private final QueryBus queryBus;
  private final CommandBus commandBus;

  public FosController(QueryBus queryBus, CommandBus commandBus) {
    this.queryBus = queryBus;
    this.commandBus = commandBus;
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

  @PostMapping(
      value = "novo-segurado"
  )
  @Operation(
      summary = "Novo segurado",
      description = "Novo segurado",
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

  public ResponseEntity<String> novoSegurado(
      @RequestParam(value = "ano") Integer ano,
      @RequestParam(value = "mes") Integer mes) {

    final var command = new NovoSeguradoCommand(ano, mes);

    return commandBus.send(command);

  }

}
