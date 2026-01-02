/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME */

package cv.inps.rh.funcionario.interfaces.rest;

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
import cv.inps.rh.funcionario.application.queries.*;
import cv.igrp.framework.core.domain.CommandBus;
import cv.inps.rh.funcionario.application.commands.*;
import cv.inps.rh.funcionario.application.dto.WrapperHistLaboralResponseDTO;
import cv.inps.rh.funcionario.application.dto.RelacaoLaboralDTO;

@IgrpController
@RestController
@RequestMapping(path = "api/v1/funcionarios")
@Tag(
    name = "Funcionario",
    description = "Gestão de Histórico Laboral"
)
public class HistoricoLaboralController {

  
  private final QueryBus queryBus;
  private final CommandBus commandBus;

  public HistoricoLaboralController(QueryBus queryBus, CommandBus commandBus) {
          this.queryBus = queryBus;
          this.commandBus = commandBus;
  }
   @GetMapping(
   value = "{funcionarioId}/historico-laboral"
  )
  @Operation(
    summary = "Get historico laboral",
    description = "Get historico laboral",
    responses = {
      @ApiResponse(
          responseCode = "200",
          
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(
                  implementation = WrapperHistLaboralResponseDTO.class,
                  type = "object")
          )
      )
    }
  )
  
  public ResponseEntity<WrapperHistLaboralResponseDTO> getHistoricoLaboral(
    @RequestParam(value = "referencia", required = false) String referencia,
    @RequestParam(value = "tipoSituacao", required = false) String tipoSituacao,
    @RequestParam(value = "situacaoLaboral", required = false) String situacaoLaboral,
    @RequestParam(value = "dataInicio", required = false) String dataInicio,
    @RequestParam(value = "dataFim", required = false) String dataFim,
    @RequestParam(value = "tamanho", required = false, defaultValue = "20") String tamanho,
    @RequestParam(value = "pagina", required = false, defaultValue = "0") String pagina, @PathVariable(value = "funcionarioId") String funcionarioId)
  {

      final var query = new GetHistoricoLaboralQuery(referencia, tipoSituacao, situacaoLaboral, dataInicio, dataFim, tamanho, pagina, funcionarioId);

      return queryBus.handle(query);

  }

   @PostMapping(
   value = "{idFuncionario}/relacao-laboral"
  )
  @Operation(
    summary = "Nova relacao laboral",
    description = "Nova relacao laboral",
    responses = {
      @ApiResponse(
          responseCode = "200",
          
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(
                  implementation = RelacaoLaboralDTO.class,
                  type = "object")
          )
      )
    }
  )
  
  public ResponseEntity<RelacaoLaboralDTO> novaRelacaoLaboral(@Valid @RequestBody RelacaoLaboralDTO novaRelacaoLaboralRequest
    , @PathVariable(value = "idFuncionario") String idFuncionario)
  {

      final var command = new NovaRelacaoLaboralCommand(novaRelacaoLaboralRequest, idFuncionario);

      return commandBus.send(command);

  }

   @PutMapping(
   value = "{idFuncionario}/relacao-laboral/{carreiraId}"
  )
  @Operation(
    summary = "Atualizar relacao laboral",
    description = "Atualizar relacao laboral",
    responses = {
      @ApiResponse(
          responseCode = "200",
          
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(
                  implementation = RelacaoLaboralDTO.class,
                  type = "object")
          )
      )
    }
  )
  
  public ResponseEntity<RelacaoLaboralDTO> atualizarRelacaoLaboral(@Valid @RequestBody RelacaoLaboralDTO atualizarRelacaoLaboralRequest
    , @PathVariable(value = "idFuncionario") String idFuncionario,@PathVariable(value = "carreiraId") String carreiraId)
  {

      final var command = new AtualizarRelacaoLaboralCommand(atualizarRelacaoLaboralRequest, idFuncionario, carreiraId);

      return commandBus.send(command);

  }

   @GetMapping(
   value = "relacao-laboral/{carreiraId}"
  )
  @Operation(
    summary = "Get relacao laboral by carreira id",
    description = "Get relacao laboral by carreira id",
    responses = {
      @ApiResponse(
          responseCode = "200",
          
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(
                  implementation = RelacaoLaboralDTO.class,
                  type = "object")
          )
      )
    }
  )
  
  public ResponseEntity<RelacaoLaboralDTO> getRelacaoLaboralByCarreiraId(
    @PathVariable(value = "carreiraId") String carreiraId)
  {

      final var query = new GetRelacaoLaboralByCarreiraIdQuery(carreiraId);

      return queryBus.handle(query);

  }

   @GetMapping(
   value = "{funcionarioId}/relacao-laboral"
  )
  @Operation(
    summary = "Get relacao laboral",
    description = "Get relacao laboral",
    responses = {
      @ApiResponse(
          responseCode = "200",
          
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(
                  implementation = WrapperHistLaboralResponseDTO.class,
                  type = "object")
          )
      )
    }
  )
  
  public ResponseEntity<WrapperHistLaboralResponseDTO> getRelacaoLaboral(
    @PathVariable(value = "funcionarioId") String funcionarioId)
  {

      final var query = new GetRelacaoLaboralQuery(funcionarioId);

      return queryBus.handle(query);

  }

}