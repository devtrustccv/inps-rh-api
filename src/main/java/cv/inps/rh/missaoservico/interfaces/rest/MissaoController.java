/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME */

package cv.inps.rh.missaoservico.interfaces.rest;

import cv.igrp.framework.stereotype.IgrpController;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import cv.igrp.framework.core.domain.CommandBus;
import cv.inps.rh.missaoservico.application.commands.*;
import cv.inps.rh.missaoservico.application.dto.MissaoSubmissaoRequestDTO;
import java.util.Map;

@IgrpController
@RestController
@RequestMapping(path = "api/v1/missao-servico")
@Tag(
    name = "Missaoservico",
    description = "gestao missao servico"
)
public class MissaoController {

  
  private final CommandBus commandBus;

  public MissaoController(CommandBus commandBus) {
          
          this.commandBus = commandBus;
  }
   @PostMapping(
   value = "submissao"
  )
  @Operation(
    summary = "Submeter missao servico",
    description = "Submeter missao servico",
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
  
  public ResponseEntity<Map<String, ?>> submeterMissaoServico(@Valid @RequestBody MissaoSubmissaoRequestDTO submeterMissaoServicoRequest
    )
  {

      final var command = new SubmeterMissaoServicoCommand(submeterMissaoServicoRequest);

      return commandBus.send(command);

  }

}