/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME. */

package cv.inps.rh.funcionario.interfaces.rest;

import cv.igrp.framework.core.domain.CommandBus;
import cv.igrp.framework.core.domain.QueryBus;
import cv.igrp.framework.stereotype.IgrpController;
import cv.inps.rh.funcionario.application.commands.AdicionarNovoPagamentoCommand;
import cv.inps.rh.funcionario.application.commands.AdicionarNovoRemuneracaoCommand;
import cv.inps.rh.funcionario.application.commands.ValidarNovoPagamentoCommand;
import cv.inps.rh.funcionario.application.commands.ValidarNovoRemuneracaoCommand;
import cv.inps.rh.funcionario.application.dto.*;
import cv.inps.rh.funcionario.application.queries.GetListRenumeracoesQuery;
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
@RequestMapping(path = "api/v1/funcionarios")
@Tag(name = "Renumeracao", description = "gest abonos subsidios")
public class RenumeracaoController {


  private final QueryBus queryBus;
  private final CommandBus commandBus;

  public RenumeracaoController(QueryBus queryBus, CommandBus commandBus) {
          this.queryBus = queryBus;
          this.commandBus = commandBus;
  }
   @GetMapping(
   value = "renumeracoes"
  )
  @Operation(
    summary = "Get list renumeracoes",
    description = "Get list renumeracoes",
    responses = {
      @ApiResponse(
          responseCode = "200",
          description = "",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(
                  implementation = WrapperListRenumeracaoDTO.class,
                  type = "object")
          )
      )
    }
  )

  public ResponseEntity<WrapperListRenumeracaoDTO> getListRenumeracoes(
    @RequestParam(value = "idFuncionario") String idFuncionario,
    @RequestParam(value = "pageSize", defaultValue = "20") String pageSize,
    @RequestParam(value = "pageNumber", defaultValue = "0") String pageNumber,
    @RequestParam(value = "dataFim", required = false) String dataFim,
    @RequestParam(value = "dataInicio", required = false) String dataInicio,
    @RequestParam(value = "estado", required = false) String estado)
  {

      final var query = new GetListRenumeracoesQuery(idFuncionario, pageSize, pageNumber, dataFim, dataInicio, estado);

      ResponseEntity<WrapperListRenumeracaoDTO> response = queryBus.handle(query);

      return response;
  }

   @PostMapping(
   value = "{funcionarioId}/remuneracao"
  )
  @Operation(
    summary = "Adicionar novo remuneracao",
    description = "Adicionar novo remuneracao",
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

  public ResponseEntity<String> adicionarNovoRemuneracao(@Valid @RequestBody NovoRemuneracaoRequestDTO adicionarNovoRemuneracaoRequest
    , @PathVariable(value = "funcionarioId") String funcionarioId)
  {

      final var command = new AdicionarNovoRemuneracaoCommand(adicionarNovoRemuneracaoRequest, funcionarioId);

       ResponseEntity<String> response = commandBus.send(command);

       return response;
  }

   @PostMapping(
   value = "{funcionarioId}/pagamento"
  )
  @Operation(
    summary = "Adicionar novo pagamento",
    description = "Adicionar novo pagamento",
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

  public ResponseEntity<String> adicionarNovoPagamento(@Valid @RequestBody NovoPagamentoRequestDTO adicionarNovoPagamentoRequest
    , @PathVariable(value = "funcionarioId") String funcionarioId)
  {

      final var command = new AdicionarNovoPagamentoCommand(adicionarNovoPagamentoRequest, funcionarioId);

       ResponseEntity<String> response = commandBus.send(command);

       return response;
  }

   @PostMapping(
   value = "validar-remuneracao"
  )
  @Operation(
    summary = "Validar novo remuneracao",
    description = "Validar novo remuneracao",
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

  public ResponseEntity<String> validarNovoRemuneracao(@Valid @RequestBody ValidarRemuneracaoRequestDTO validarNovoRemuneracaoRequest
    )
  {

      final var command = new ValidarNovoRemuneracaoCommand(validarNovoRemuneracaoRequest);

       ResponseEntity<String> response = commandBus.send(command);

       return response;
  }

   @PostMapping(
   value = "validar-pagamento"
  )
  @Operation(
    summary = "Validar novo pagamento",
    description = "Validar novo pagamento",
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

  public ResponseEntity<String> validarNovoPagamento(@Valid @RequestBody ValidarPagamentoRequestDTO validarNovoPagamentoRequest
    )
  {

      final var command = new ValidarNovoPagamentoCommand(validarNovoPagamentoRequest);

       ResponseEntity<String> response = commandBus.send(command);

       return response;
  }

}
