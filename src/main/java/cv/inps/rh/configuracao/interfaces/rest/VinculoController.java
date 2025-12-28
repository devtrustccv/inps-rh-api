/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME */

package cv.inps.rh.configuracao.interfaces.rest;

import cv.igrp.framework.core.domain.CommandBus;
import cv.igrp.framework.stereotype.IgrpController;
import cv.inps.rh.configuracao.application.commands.AssociarVinculoSituacaoCommand;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@IgrpController
@RestController
@RequestMapping(path = "configuracao")
@Tag(
    name = "Configuracao",
    description = "Gestão de Vínculos"
)
public class VinculoController {


  private final CommandBus commandBus;

  public VinculoController(CommandBus commandBus) {

    this.commandBus = commandBus;
  }

  @PostMapping(
      value = "vinculo/associar-situacao"
  )
  @Operation(
      summary = "Associar vinculo situacao",
      description = "Associar vinculo situacao",
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

  public ResponseEntity<String> associarVinculoSituacao(
      @RequestParam(value = "vinculoId") String vinculoId,
      @RequestParam(value = "situacaoId") String situacaoId) {

    final var command = new AssociarVinculoSituacaoCommand(vinculoId, situacaoId);

    return commandBus.send(command);

  }

}
