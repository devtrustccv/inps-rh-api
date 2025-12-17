/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME */

package cv.inps.rh.configuracao.interfaces.rest;

import cv.igrp.framework.core.domain.CommandBus;
import cv.igrp.framework.core.domain.QueryBus;
import cv.igrp.framework.stereotype.IgrpController;
import cv.inps.rh.configuracao.application.commands.SaveEquipamentosLocalTrabalhoCommand;
import cv.inps.rh.configuracao.application.dto.EquipamentoListRequestDTO;
import cv.inps.rh.configuracao.application.queries.GetEquipamentosLocalTrabalhoQuery;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@IgrpController
@RestController
@RequestMapping(path = "configuracao")
@Tag(
    name = "Configuracao",
    description = "Equipamento Handling"
)
public class EquipamentoController {


  private final QueryBus queryBus;
  private final CommandBus commandBus;

  public EquipamentoController(QueryBus queryBus, CommandBus commandBus) {
    this.queryBus = queryBus;
    this.commandBus = commandBus;
  }

  @PostMapping(
      value = "{localTrabalhoId}/equipamentos"
  )
  @Operation(
      summary = "Save equipamentos local trabalho",
      description = "Save equipamentos local trabalho",
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

  public ResponseEntity<String> saveEquipamentosLocalTrabalho(@Valid @RequestBody EquipamentoListRequestDTO saveEquipamentosLocalTrabalhoRequest
      , @PathVariable(value = "localTrabalhoId") String localTrabalhoId) {

    final var command = new SaveEquipamentosLocalTrabalhoCommand(saveEquipamentosLocalTrabalhoRequest, localTrabalhoId);

    return commandBus.send(command);

  }

  @GetMapping(
      value = "{localTrabalhoId}/equipamentos"
  )
  @Operation(
      summary = "Get equipamentos local trabalho",
      description = "Get equipamentos local trabalho",
      responses = {
          @ApiResponse(
              responseCode = "200",

              content = @Content(
                  mediaType = "application/json",
                  schema = @Schema(
                      implementation = EquipamentoListRequestDTO.class,
                      type = "object")
              )
          )
      }
  )

  public ResponseEntity<EquipamentoListRequestDTO> getEquipamentosLocalTrabalho(
      @PathVariable(value = "localTrabalhoId") String localTrabalhoId) {

    final var query = new GetEquipamentosLocalTrabalhoQuery(localTrabalhoId);

    return queryBus.handle(query);

  }

}
