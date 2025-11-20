/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME. */

package cv.inps.rh.funcionario.interfaces.rest;

import cv.igrp.framework.core.domain.CommandBus;
import cv.igrp.framework.stereotype.IgrpController;
import cv.inps.rh.funcionario.application.commands.NovoProcessoDisciplinarCommand;
import cv.inps.rh.funcionario.application.dto.ProcessoDisciplinarRequestDTO;
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
@RequestMapping(path = "processo-disciplinar")
@Tag(name = "ProcessoDisciplinar", description = "Gestão de Processos Disciplinares")
public class ProcessoDisciplinarController {


  private final CommandBus commandBus;

  public ProcessoDisciplinarController(CommandBus commandBus) {

          this.commandBus = commandBus;
  }
   @PostMapping(
   value = "processo-disciplinar/{funcionarioId}"
  )
  @Operation(
    summary = "POST method to handle operations for Novo processo disciplinar",
    description = "POST method to handle operations for Novo processo disciplinar",
    responses = {
      @ApiResponse(
          responseCode = "200",
          description = "",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(
                  implementation = String.class,
                  type = "String")
          )
      )
    }
  )

  public ResponseEntity<String> novoProcessoDisciplinar(@Valid @RequestBody ProcessoDisciplinarRequestDTO novoProcessoDisciplinarRequest
    , @PathVariable(value = "funcionarioId") String funcionarioId)
  {

      final var command = new NovoProcessoDisciplinarCommand(novoProcessoDisciplinarRequest, funcionarioId);

       ResponseEntity<String> response = commandBus.send(command);

       return response;
  }

}
