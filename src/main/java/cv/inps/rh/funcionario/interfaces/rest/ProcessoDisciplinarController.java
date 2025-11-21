/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME. */

package cv.inps.rh.funcionario.interfaces.rest;

import cv.igrp.framework.core.domain.CommandBus;
import cv.igrp.framework.core.domain.QueryBus;
import cv.igrp.framework.stereotype.IgrpController;
import cv.inps.rh.funcionario.application.commands.NovoProcessoDisciplinarCommand;
import cv.inps.rh.funcionario.application.dto.ProcessoDisciplinarRequestDTO;
import cv.inps.rh.funcionario.application.dto.ProcessoDisciplinarResponseDTO;
import cv.inps.rh.funcionario.application.queries.GetProcessosFuncionarioQuery;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@IgrpController
@RestController
@RequestMapping(path = "processo-disciplinar")
@Tag(name = "ProcessoDisciplinar", description = "Gestão de Processos Disciplinares")
public class ProcessoDisciplinarController {


  private final QueryBus queryBus;
  private final CommandBus commandBus;

  public ProcessoDisciplinarController(QueryBus queryBus, CommandBus commandBus) {
          this.queryBus = queryBus;
          this.commandBus = commandBus;
  }
   @PostMapping(
   value = "{funcionarioId}"
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

   @GetMapping(
   value = "{funcionarioId}"
  )
  @Operation(
    summary = "GET method to handle operations for Get processos funcionario",
    description = "GET method to handle operations for Get processos funcionario",
    responses = {
      @ApiResponse(
          responseCode = "200",
          description = "",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(
                  implementation = ProcessoDisciplinarResponseDTO.class,
                  type = "object")
          )
      )
    }
  )

  public ResponseEntity<List<ProcessoDisciplinarResponseDTO>> getProcessosFuncionario(
    @PathVariable(value = "funcionarioId") String funcionarioId)
  {

      final var query = new GetProcessosFuncionarioQuery(funcionarioId);

      ResponseEntity<List<ProcessoDisciplinarResponseDTO>> response = queryBus.handle(query);

      return response;
  }

}
