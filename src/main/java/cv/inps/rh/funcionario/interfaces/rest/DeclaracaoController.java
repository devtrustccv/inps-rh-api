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
import cv.inps.rh.funcionario.application.dto.PedidoDeclaracaoDTO;
import java.util.Map;
import cv.inps.rh.funcionario.application.dto.WrapperListaPedidoDeclaracaoDTO;
import cv.inps.rh.funcionario.application.dto.PedidoDeclaracaoResponseDTO;
import cv.inps.rh.funcionario.application.dto.PedidoDeclaracaoAnaliseDTO;
import cv.inps.rh.funcionario.application.dto.PedidoDeclaracaoValidacaoDTO;
import cv.inps.rh.shared.application.dto.NotificacaoInfoDTO;

@IgrpController
@RestController
@RequestMapping(path = "api/v1/funcionarios")
@Tag(
    name = "Funcionario",
    description = "Emitir pedidos declaração"
)
public class DeclaracaoController {

  
  private final QueryBus queryBus;
  private final CommandBus commandBus;

  public DeclaracaoController(QueryBus queryBus, CommandBus commandBus) {
          this.queryBus = queryBus;
          this.commandBus = commandBus;
  }
   @PostMapping(
   value = "pedidos/declaracoes"
  )
  @Operation(
    summary = "Novo pedido declaracao",
    description = "Novo pedido declaracao",
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
  
  public ResponseEntity<Map<String, ?>> novoPedidoDeclaracao(@Valid @RequestBody PedidoDeclaracaoDTO novoPedidoDeclaracaoRequest
    )
  {

      final var command = new NovoPedidoDeclaracaoCommand(novoPedidoDeclaracaoRequest);

      return commandBus.send(command);

  }

   @GetMapping(
   value = "pedidos/declaracoes"
  )
  @Operation(
    summary = "Get pedido declaracoes",
    description = "Get pedido declaracoes",
    responses = {
      @ApiResponse(
          responseCode = "200",
          
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(
                  implementation = WrapperListaPedidoDeclaracaoDTO.class,
                  type = "object")
          )
      )
    }
  )
  
  public ResponseEntity<WrapperListaPedidoDeclaracaoDTO> getPedidoDeclaracoes(
    @RequestParam(value = "tipoDeclaracao", required = false) String tipoDeclaracao,
    @RequestParam(value = "dataPedidoDe", required = false) String dataPedidoDe,
    @RequestParam(value = "dataPedidoAte", required = false) String dataPedidoAte,
    @RequestParam(value = "pageNumber", required = false, defaultValue = "0") String pageNumber,
    @RequestParam(value = "pageSize", required = false, defaultValue = "20") String pageSize)
  {

      final var query = new GetPedidoDeclaracoesQuery(tipoDeclaracao, dataPedidoDe, dataPedidoAte, pageNumber, pageSize);

      return queryBus.handle(query);

  }

   @GetMapping(
   value = "pedidos/declaracoes/{id}"
  )
  @Operation(
    summary = "Get pedido declaracao",
    description = "Get pedido declaracao",
    responses = {
      @ApiResponse(
          responseCode = "200",
          
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(
                  implementation = PedidoDeclaracaoResponseDTO.class,
                  type = "object")
          )
      )
    }
  )
  
  public ResponseEntity<PedidoDeclaracaoResponseDTO> getPedidoDeclaracao(
    @PathVariable(value = "id") String id)
  {

      final var query = new GetPedidoDeclaracaoQuery(id);

      return queryBus.handle(query);

  }

   @PutMapping(
   value = "pedidos/declaracoes/{id}/analise"
  )
  @Operation(
    summary = "Submeter analise pedido declaracao",
    description = "Submeter analise pedido declaracao",
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
  
  public ResponseEntity<Map<String, ?>> submeterAnalisePedidoDeclaracao(@Valid @RequestBody PedidoDeclaracaoAnaliseDTO submeterAnalisePedidoDeclaracaoRequest
    , @PathVariable(value = "id") String id)
  {

      final var command = new SubmeterAnalisePedidoDeclaracaoCommand(submeterAnalisePedidoDeclaracaoRequest, id);

      return commandBus.send(command);

  }

   @PutMapping(
   value = "pedidos/declaracoes/{id}/validacao"
  )
  @Operation(
    summary = "Validacao pedido declaracao",
    description = "Validacao pedido declaracao",
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
  
  public ResponseEntity<Map<String, ?>> validacaoPedidoDeclaracao(@Valid @RequestBody PedidoDeclaracaoValidacaoDTO validacaoPedidoDeclaracaoRequest
    , @PathVariable(value = "id") String id)
  {

      final var command = new ValidacaoPedidoDeclaracaoCommand(validacaoPedidoDeclaracaoRequest, id);

      return commandBus.send(command);

  }

   @GetMapping(
   value = "pedidos/declaracoes/{id}/declaracao"
  )
  @Operation(
    summary = "Visualizar pedido declaracao",
    description = "Visualizar pedido declaracao",
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
  
  public ResponseEntity<String> visualizarPedidoDeclaracao(
    @PathVariable(value = "id") String id)
  {

      final var query = new VisualizarPedidoDeclaracaoQuery(id);

      return queryBus.handle(query);

  }

   @GetMapping(
   value = "pedidos/declaracoes/{id}/notificacao"
  )
  @Operation(
    summary = "Get notificacao pedido declaracao",
    description = "Get notificacao pedido declaracao",
    responses = {
      @ApiResponse(
          responseCode = "200",
          
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(
                  implementation = NotificacaoInfoDTO.class,
                  type = "object")
          )
      )
    }
  )
  
  public ResponseEntity<NotificacaoInfoDTO> getNotificacaoPedidoDeclaracao(
    @PathVariable(value = "id") String id)
  {

      final var query = new GetNotificacaoPedidoDeclaracaoQuery(id);

      return queryBus.handle(query);

  }

}