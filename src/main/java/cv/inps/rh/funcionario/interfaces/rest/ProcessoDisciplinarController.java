/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME */

package cv.inps.rh.funcionario.interfaces.rest;

import cv.igrp.framework.core.domain.CommandBus;
import cv.igrp.framework.core.domain.QueryBus;
import cv.igrp.framework.stereotype.IgrpController;
import cv.inps.rh.funcionario.application.commands.DeleteProcessoDisciplinarCommand;
import cv.inps.rh.funcionario.application.commands.NovoProcessoDisciplinarCommand;
import cv.inps.rh.funcionario.application.commands.UpdateProcessoDisciplinarCommand;
import cv.inps.rh.funcionario.application.dto.ProcessoDisciplinarRequestDTO;
import cv.inps.rh.funcionario.application.dto.ProcessoDisciplinarResponseDTO;
import cv.inps.rh.funcionario.application.queries.GetProcessoDisciplinarByIdQuery;
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
@RequestMapping(path = "api/v1/funcionarios")
@Tag(name = "ProcessoDisciplinar", description = "Gestão de Processos Disciplinares")
public class ProcessoDisciplinarController {


  private final QueryBus queryBus;
  private final CommandBus commandBus;

  public ProcessoDisciplinarController(QueryBus queryBus, CommandBus commandBus) {
          this.queryBus = queryBus;
          this.commandBus = commandBus;
  }
   @PostMapping(
   value = "{funcionarioId}/processo-disciplinar"
  )
  @Operation(
    summary = "Novo processo disciplinar",
    description = "Novo processo disciplinar",
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

      return commandBus.send(command);

  }

   @GetMapping(
   value = "{funcionarioId}/processo-disciplinar"
  )
  @Operation(
    summary = "Get processos funcionario",
    description = "Get processos funcionario",
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
    @PathVariable(value = "funcionarioId") String funcionarioId,
    @RequestParam(value = "validacao", defaultValue = "false") boolean validacao)
  {

      final var query = new GetProcessosFuncionarioQuery(funcionarioId, validacao);

      return queryBus.handle(query);

  }

   @GetMapping(
   value = "processo-disciplinar/{processoDisciplinarId}"
  )
  @Operation(
    summary = "Get processo disciplinar by id",
    description = "Get processo disciplinar by id",
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

  public ResponseEntity<ProcessoDisciplinarResponseDTO> getProcessoDisciplinarById(
    @PathVariable(value = "processoDisciplinarId") String processoDisciplinarId)
  {

      final var query = new GetProcessoDisciplinarByIdQuery(processoDisciplinarId);

      return queryBus.handle(query);

  }

   @PutMapping(
   value = "processo-disciplinar/{processoDisciplinarId}"
  )
  @Operation(
    summary = "Update processo disciplinar",
    description = "Update processo disciplinar",
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

  public ResponseEntity<String> updateProcessoDisciplinar(@Valid @RequestBody ProcessoDisciplinarRequestDTO updateProcessoDisciplinarRequest
    , @PathVariable(value = "processoDisciplinarId") String processoDisciplinarId)
  {

      final var command = new UpdateProcessoDisciplinarCommand(updateProcessoDisciplinarRequest, processoDisciplinarId);

      return commandBus.send(command);

  }

   @DeleteMapping(
   value = "processo-disciplinar/{processoDisciplinarId}"
  )
  @Operation(
    summary = "Delete processo disciplinar",
    description = "Delete processo disciplinar",
    responses = {
      @ApiResponse(
          responseCode = "204",
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

  public ResponseEntity<String> deleteProcessoDisciplinar(
    @PathVariable(value = "processoDisciplinarId") String processoDisciplinarId)
  {

      final var command = new DeleteProcessoDisciplinarCommand(processoDisciplinarId);

      return commandBus.send(command);

  }

}
