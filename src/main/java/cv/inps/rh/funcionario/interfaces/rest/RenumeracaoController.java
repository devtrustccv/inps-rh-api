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
import cv.inps.rh.funcionario.application.dto.WrapperListRenumeracaoDTO;
import cv.inps.rh.funcionario.application.dto.NovoRemuneracaoRequestDTO;
import cv.inps.rh.funcionario.application.dto.NovoPagamentoRequestDTO;
import cv.inps.rh.funcionario.application.dto.ValidarRemuneracaoRequestDTO;
import cv.inps.rh.funcionario.application.dto.ValidarPagamentoRequestDTO;

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
    @RequestParam(value = "estado", required = false) String estado)
  {

      final var query = new GetListRenumeracoesQuery(idFuncionario, pageSize, pageNumber, dataFim, dataInicio, estado);

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

      return commandBus.send(command);

  }

   @PutMapping(
   value = "{idFuncionario}/validar-remuneracao/{renumeracaoId}"
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
                  implementation = String.class,
                  type = "String")
          )
      )
    }
  )
  
  public ResponseEntity<String> validarNovoRemuneracao(@Valid @RequestBody ValidarRemuneracaoRequestDTO validarNovoRemuneracaoRequest
    , @PathVariable(value = "idFuncionario") String idFuncionario,@PathVariable(value = "renumeracaoId") String renumeracaoId)
  {

      final var command = new ValidarNovoRemuneracaoCommand(validarNovoRemuneracaoRequest, idFuncionario, renumeracaoId);

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
                  implementation = String.class,
                  type = "String")
          )
      )
    }
  )
  
  public ResponseEntity<String> validarNovoPagamento(@Valid @RequestBody ValidarPagamentoRequestDTO validarNovoPagamentoRequest
    , @PathVariable(value = "idFuncionario") String idFuncionario,@PathVariable(value = "pagamentoId") String pagamentoId)
  {

      final var command = new ValidarNovoPagamentoCommand(validarNovoPagamentoRequest, idFuncionario, pagamentoId);

      return commandBus.send(command);

  }

   @GetMapping(
   value = "{idFuncionario}/remuneracao/{renumeracaoId}"
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
    @PathVariable(value = "idFuncionario") String idFuncionario,@PathVariable(value = "renumeracaoId") String renumeracaoId)
  {

      final var query = new GetRenumeracaoByIdQuery(idFuncionario, renumeracaoId);

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

}