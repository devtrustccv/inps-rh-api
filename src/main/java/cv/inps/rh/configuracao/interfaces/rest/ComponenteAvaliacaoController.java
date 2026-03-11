/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME */

package cv.inps.rh.configuracao.interfaces.rest;

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
import cv.inps.rh.configuracao.application.commands.*;
import cv.inps.rh.configuracao.application.dto.ComponenteAvaliacaoRequestDTO;
import java.util.Map;

@IgrpController
@RestController
@RequestMapping(path = "configuracao")
@Tag(
    name = "Configuracao",
    description = "gest componente avaliacao"
)
public class ComponenteAvaliacaoController {

  
  private final CommandBus commandBus;

  public ComponenteAvaliacaoController(CommandBus commandBus) {
          
          this.commandBus = commandBus;
  }
   @PostMapping(
   value = "avaliacao-desempenho/componentes"
  )
  @Operation(
    summary = "Create componentes avaliacao",
    description = "Create componentes avaliacao",
    responses = {
      @ApiResponse(
          responseCode = "201",
          
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(
                  implementation = String.class,
                  type = "String")
          )
      )
    }
  )
  
  public ResponseEntity<Map<String, ?>> createComponentesAvaliacao(@Valid @RequestBody ComponenteAvaliacaoRequestDTO createComponentesAvaliacaoRequest
    )
  {

      final var command = new CreateComponentesAvaliacaoCommand(createComponentesAvaliacaoRequest);

      return commandBus.send(command);

  }

}