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

import cv.igrp.framework.core.domain.QueryBus;
import cv.inps.rh.configuracao.application.queries.*;
import cv.igrp.framework.core.domain.CommandBus;
import cv.inps.rh.configuracao.application.commands.*;
import cv.inps.rh.configuracao.application.dto.EscalaAvaliacaoRequestDTO;
import java.util.Map;
import cv.inps.rh.configuracao.application.dto.EscalaAvaliacaoResponseDTO;

@IgrpController
@RestController
@RequestMapping(path = "configuracao")
@Tag(
    name = "Configuracao",
    description = "desc"
)
public class EscalaAvaliacaoController {

  
  private final QueryBus queryBus;
  private final CommandBus commandBus;

  public EscalaAvaliacaoController(QueryBus queryBus, CommandBus commandBus) {
          this.queryBus = queryBus;
          this.commandBus = commandBus;
  }
   @PostMapping(
   value = "avaliacao-desempenho/escala"
  )
  @Operation(
    summary = "Create escala avaliacao",
    description = "Create escala avaliacao",
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
  
  public ResponseEntity<Map<String, ?>> createEscalaAvaliacao(@Valid @RequestBody EscalaAvaliacaoRequestDTO createEscalaAvaliacaoRequest
    )
  {

      final var command = new CreateEscalaAvaliacaoCommand(createEscalaAvaliacaoRequest);

      return commandBus.send(command);

  }

   @GetMapping(
   value = "avaliacao-desempenho/escala/{id}"
  )
  @Operation(
    summary = "Get escala avaliacao",
    description = "Get escala avaliacao",
    responses = {
      @ApiResponse(
          responseCode = "200",
          
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(
                  implementation = EscalaAvaliacaoResponseDTO.class,
                  type = "object")
          )
      )
    }
  )
  
  public ResponseEntity<EscalaAvaliacaoResponseDTO> getEscalaAvaliacao(
    @PathVariable(value = "id") String id)
  {

      final var query = new GetEscalaAvaliacaoQuery(id);

      return queryBus.handle(query);

  }

}