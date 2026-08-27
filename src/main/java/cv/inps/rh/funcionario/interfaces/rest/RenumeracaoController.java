/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME */

package cv.inps.rh.funcionario.interfaces.rest;

import cv.igrp.framework.core.domain.CommandBus;
import cv.igrp.framework.core.domain.QueryBus;
import cv.igrp.framework.stereotype.IgrpController;
import cv.inps.rh.funcionario.application.commands.AdicionarNovoPagamentoCommand;
import cv.inps.rh.funcionario.application.commands.AdicionarNovoRemuneracaoCommand;
import cv.inps.rh.funcionario.application.commands.ValidarNovoPagamentoCommand;
import cv.inps.rh.funcionario.application.commands.ValidarNovoRemuneracaoCommand;
import cv.inps.rh.funcionario.application.dto.*;
import cv.inps.rh.funcionario.application.queries.CalcularRemuneracaoQuery;
import cv.inps.rh.funcionario.application.queries.GetListRenumeracoesQuery;
import cv.inps.rh.funcionario.application.queries.GetPagamentosDescontosByIdQuery;
import cv.inps.rh.funcionario.application.queries.GetRenumeracaoByIdQuery;
import cv.inps.rh.shared.application.dto.SuccessResponseDTO;
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
@Tag(
    name = "Funcionario",
    description = "gest abonos subsidios"
)
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
    @RequestParam(value = "estado", required = false) String estado,
    @RequestParam(value = "tiprelUuid", required = false) String tiprelUuid,
    @RequestParam(value = "validacao", defaultValue = "false") boolean validacao,
    @RequestParam(value = "situacaoLaboral", required = false) String situacaoLaboral,
    @RequestParam(value = "contrVinculo", required = false) String contrVinculo)
  {

      final var query = new GetListRenumeracoesQuery(idFuncionario, pageSize, pageNumber, dataFim, dataInicio, estado, tiprelUuid, validacao, situacaoLaboral, contrVinculo);

      return queryBus.handle(query);

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

          content = @Content(
              mediaType = "application/json",
              schema = @Schema(
                  implementation = SuccessResponseDTO.class,
                  type = "object")
          )
      )
    }
  )

   public ResponseEntity<SuccessResponseDTO> adicionarNovoRemuneracao(@Valid @RequestBody NovoRemuneracaoRequestDTO adicionarNovoRemuneracaoRequest
    , @PathVariable(value = "funcionarioId") String funcionarioId)
  {

      final var command = new AdicionarNovoRemuneracaoCommand(adicionarNovoRemuneracaoRequest, funcionarioId);

      return commandBus.send(command);

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

          content = @Content(
              mediaType = "application/json",
              schema = @Schema(
                  implementation = SuccessResponseDTO.class,
                  type = "object")
          )
      )
    }
  )

   public ResponseEntity<SuccessResponseDTO> adicionarNovoPagamento(@Valid @RequestBody NovoPagamentoRequestDTO adicionarNovoPagamentoRequest
    , @PathVariable(value = "funcionarioId") String funcionarioId)
  {

      final var command = new AdicionarNovoPagamentoCommand(adicionarNovoPagamentoRequest, funcionarioId);

      return commandBus.send(command);

  }

   @PutMapping(
   value = "{idFuncionario}/validar-remuneracao/{remuneracaoId}"
  )
  @Operation(
    summary = "Validar novo remuneracao",
    description = "Validar novo remuneracao",
    responses = {
      @ApiResponse(
          responseCode = "200",

          content = @Content(
              mediaType = "application/json",
              schema = @Schema(
                  implementation = SuccessResponseDTO.class,
                  type = "object")
          )
      )
    }
  )

   public ResponseEntity<SuccessResponseDTO> validarNovoRemuneracao(@Valid @RequestBody ValidarRemuneracaoRequestDTO validarNovoRemuneracaoRequest
    , @PathVariable(value = "idFuncionario") String idFuncionario,@PathVariable(value = "remuneracaoId") String remuneracaoId)
  {

      final var command = new ValidarNovoRemuneracaoCommand(validarNovoRemuneracaoRequest, idFuncionario, remuneracaoId);

      return commandBus.send(command);

  }

   @PutMapping(
   value = "{idFuncionario}/validar-pagamento/{pagamentoId}"
  )
  @Operation(
    summary = "Validar novo pagamento",
    description = "Validar novo pagamento",
    responses = {
      @ApiResponse(
          responseCode = "200",

          content = @Content(
              mediaType = "application/json",
              schema = @Schema(
                  implementation = SuccessResponseDTO.class,
                  type = "object")
          )
      )
    }
  )

   public ResponseEntity<SuccessResponseDTO> validarNovoPagamento(@Valid @RequestBody ValidarPagamentoRequestDTO validarNovoPagamentoRequest
    , @PathVariable(value = "idFuncionario") String idFuncionario,@PathVariable(value = "pagamentoId") String pagamentoId)
  {

      final var command = new ValidarNovoPagamentoCommand(validarNovoPagamentoRequest, idFuncionario, pagamentoId);

      return commandBus.send(command);

  }

   @GetMapping(
   value = "{idFuncionario}/remuneracao/{remuneracaoId}"
  )
  @Operation(
    summary = "Get renumeracao by id",
    description = "Get renumeracao by id",
    responses = {
      @ApiResponse(
          responseCode = "200",

          content = @Content(
              mediaType = "application/json",
              schema = @Schema(
                  implementation = NovoRemuneracaoRequestDTO.class,
                  type = "object")
          )
      )
    }
  )

   public ResponseEntity<NovoRemuneracaoRequestDTO> getRenumeracaoById(
    @PathVariable(value = "idFuncionario") String idFuncionario,@PathVariable(value = "remuneracaoId") String remuneracaoId)
  {

      final var query = new GetRenumeracaoByIdQuery(idFuncionario, remuneracaoId);

      return queryBus.handle(query);

  }

   @GetMapping(
   value = "{idFuncionario}/pagamento/{pagamentoId}"
  )
  @Operation(
    summary = "Get pagamentos descontos by id",
    description = "Get pagamentos descontos by id",
    responses = {
      @ApiResponse(
          responseCode = "200",

          content = @Content(
              mediaType = "application/json",
              schema = @Schema(
                  implementation = NovoRemuneracaoRequestDTO.class,
                  type = "object")
          )
      )
    }
  )

  public ResponseEntity<NovoRemuneracaoRequestDTO> getPagamentosDescontosById(
    @PathVariable(value = "idFuncionario") String idFuncionario,@PathVariable(value = "pagamentoId") String pagamentoId)
  {

      final var query = new GetPagamentosDescontosByIdQuery(idFuncionario, pagamentoId);

      return queryBus.handle(query);

  }

   @PostMapping(
   value = "calcular-remuneracao"
  )
  @Operation(
    summary = "Calcular remuneracao",
    description = "Calcula Remuneracao Bruta, Total Desconto e Remuneracao Liquida para o formulario de registo de colaborador",
    responses = {
      @ApiResponse(
          responseCode = "200",

          content = @Content(
              mediaType = "application/json",
              schema = @Schema(
                  implementation = CalcularRemuneracaoResponseDTO.class,
                  type = "object")
          )
      )
    }
  )

  public ResponseEntity<CalcularRemuneracaoResponseDTO> calcularRemuneracao(
    @Valid @RequestBody CalcularRemuneracaoRequestDTO calcularRemuneracaoRequest)
  {

      final var query = new CalcularRemuneracaoQuery(calcularRemuneracaoRequest);

      return queryBus.handle(query);

  }

}
