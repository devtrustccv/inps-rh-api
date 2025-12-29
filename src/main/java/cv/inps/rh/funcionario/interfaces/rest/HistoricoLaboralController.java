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
import cv.inps.rh.funcionario.application.dto.ValidarNovoHistoricoLaboralDTO;

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
   value = "{idFuncionario}/historico-laboral"
  )
  @Operation(
    summary = "Validar historico laboral",
    description = "Validar historico laboral",
    responses = {
      @ApiResponse(
          responseCode = "200",
          
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(
                  implementation = ValidarNovoHistoricoLaboralDTO.class,
                  type = "object")
          )
      )
    }
  )
  
  public ResponseEntity<ValidarNovoHistoricoLaboralDTO> validarHistoricoLaboral(@Valid @RequestBody ValidarNovoHistoricoLaboralDTO validarHistoricoLaboralRequest
    , @PathVariable(value = "idFuncionario") String idFuncionario)
  {

      final var command = new ValidarHistoricoLaboralCommand(validarHistoricoLaboralRequest, idFuncionario);

      return commandBus.send(command);

  }

   @PutMapping(
   value = "{idFuncionario}/historico-laboral/{historicoId}"
  )
  @Operation(
    summary = "Atualizar historico laboral",
    description = "Atualizar historico laboral",
    responses = {
      @ApiResponse(
          responseCode = "200",
          
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(
                  implementation = ValidarNovoHistoricoLaboralDTO.class,
                  type = "object")
          )
      )
    }
  )
  
  public ResponseEntity<ValidarNovoHistoricoLaboralDTO> atualizarHistoricoLaboral(@Valid @RequestBody ValidarNovoHistoricoLaboralDTO atualizarHistoricoLaboralRequest
    , @PathVariable(value = "idFuncionario") String idFuncionario,@PathVariable(value = "historicoId") String historicoId)
  {

      final var command = new AtualizarHistoricoLaboralCommand(atualizarHistoricoLaboralRequest, idFuncionario, historicoId);

      return commandBus.send(command);

  }

   @GetMapping(
   value = "historico-laboral/{historicoId}"
  )
  @Operation(
    summary = "Get historico laboral by id",
    description = "Get historico laboral by id",
    responses = {
      @ApiResponse(
          responseCode = "200",
          
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(
                  implementation = ValidarNovoHistoricoLaboralDTO.class,
                  type = "object")
          )
      )
    }
  )
  
  public ResponseEntity<ValidarNovoHistoricoLaboralDTO> getHistoricoLaboralById(
    @PathVariable(value = "historicoId") String historicoId)
  {

      final var query = new GetHistoricoLaboralByIdQuery(historicoId);

      return queryBus.handle(query);

  }

}