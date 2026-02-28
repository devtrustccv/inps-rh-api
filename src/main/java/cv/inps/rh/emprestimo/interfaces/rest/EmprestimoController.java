/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME */

package cv.inps.rh.emprestimo.interfaces.rest;

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
import cv.inps.rh.emprestimo.application.queries.*;
import cv.igrp.framework.core.domain.CommandBus;
import cv.inps.rh.emprestimo.application.commands.*;
import java.util.List;
import cv.inps.rh.emprestimo.application.dto.InformacaoEmprestimoRequestDTO;
import cv.inps.rh.emprestimo.application.dto.PedidoEmprestimoRequestDTO;
import cv.inps.rh.emprestimo.application.dto.IdDTO;
import cv.inps.rh.emprestimo.application.dto.DetalhesEmprestimoDTO;
import cv.inps.rh.emprestimo.application.dto.EmprestimoListDTO;
import cv.inps.rh.emprestimo.application.dto.AnaliseRhRequestDTO;
import cv.inps.rh.emprestimo.application.dto.AnaliseFinanceiroRequestDTO;
import cv.inps.rh.emprestimo.application.dto.AutorizacaoComissaoExecutivaDTO;
import cv.inps.rh.emprestimo.application.dto.ElaboracaoContratoRequestDTO;
import cv.inps.rh.emprestimo.application.dto.PlanoFinanceiroDTO;
import cv.inps.rh.emprestimo.application.dto.PlanoFinanceiroRowDTO;
import cv.inps.rh.emprestimo.application.dto.FundoSocialRequestDTO;
import cv.inps.rh.emprestimo.application.dto.HistoricoPagamentoDTO;
import cv.inps.rh.emprestimo.application.dto.PedidoAdiantamentoRequestDTO;
import cv.inps.rh.emprestimo.application.dto.AnaliseRhAdiantamentoRequestDTO;
import cv.inps.rh.emprestimo.application.dto.VerificarAdiantamentoRequestDTO;
import cv.inps.rh.emprestimo.application.dto.DocumentoDTO;

@IgrpController
@RestController
@RequestMapping(path = "emprestimo")
@Tag(
    name = "Emprestimo",
    description = "Módulo Empréstimo"
)
public class EmprestimoController {

  
  private final QueryBus queryBus;
  private final CommandBus commandBus;

  public EmprestimoController(QueryBus queryBus, CommandBus commandBus) {
          this.queryBus = queryBus;
          this.commandBus = commandBus;
  }
   @PostMapping(
   value = "info-emprestimo"
  )
  @Operation(
    summary = "Save configuracao info emprestimo",
    description = "Save configuracao info emprestimo",
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
  
  public ResponseEntity<String> saveConfiguracaoInfoEmprestimo(@Valid @RequestBody List<InformacaoEmprestimoRequestDTO> saveConfiguracaoInfoEmprestimoRequest
    )
  {

      final var command = new SaveConfiguracaoInfoEmprestimoCommand(saveConfiguracaoInfoEmprestimoRequest);

      return commandBus.send(command);

  }

   @GetMapping(
   value = "info-emprestimo"
  )
  @Operation(
    summary = "Get configuracao emprestimo",
    description = "Get configuracao emprestimo",
    responses = {
      @ApiResponse(
          responseCode = "200",
          
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(
                  implementation = InformacaoEmprestimoRequestDTO.class,
                  type = "object")
          )
      )
    }
  )
  
  public ResponseEntity<List<InformacaoEmprestimoRequestDTO>> getConfiguracaoEmprestimo(
    )
  {

      final var query = new GetConfiguracaoEmprestimoQuery();

      return queryBus.handle(query);

  }

   @PostMapping(
  )
  @Operation(
    summary = "Save emprestimo",
    description = "Save emprestimo",
    responses = {
      @ApiResponse(
          responseCode = "201",
          
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(
                  implementation = IdDTO.class,
                  type = "object")
          )
      )
    }
  )
  
  public ResponseEntity<IdDTO> saveEmprestimo(@Valid @RequestBody PedidoEmprestimoRequestDTO saveEmprestimoRequest
    , @RequestParam(value = "emprestimoId", required = false) String emprestimoId)
  {

      final var command = new SaveEmprestimoCommand(saveEmprestimoRequest, emprestimoId);

      return commandBus.send(command);

  }

   @GetMapping(
   value = "{emprestimoId}"
  )
  @Operation(
    summary = "Get emprestimo by id",
    description = "Get emprestimo by id",
    responses = {
      @ApiResponse(
          responseCode = "200",
          
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(
                  implementation = DetalhesEmprestimoDTO.class,
                  type = "object")
          )
      )
    }
  )
  
  public ResponseEntity<DetalhesEmprestimoDTO> getEmprestimoById(
    @PathVariable(value = "emprestimoId") String emprestimoId)
  {

      final var query = new GetEmprestimoByIdQuery(emprestimoId);

      return queryBus.handle(query);

  }

   @GetMapping(
  )
  @Operation(
    summary = "Listar emprestimos",
    description = "Listar emprestimos",
    responses = {
      @ApiResponse(
          responseCode = "200",
          
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(
                  implementation = EmprestimoListDTO.class,
                  type = "object")
          )
      )
    }
  )
  
  public ResponseEntity<EmprestimoListDTO> listarEmprestimos(
    @RequestParam(value = "tipoEmprestimo", required = false) String tipoEmprestimo,
    @RequestParam(value = "direccaoId", required = false) String direccaoId,
    @RequestParam(value = "dataInicio", required = false) String dataInicio,
    @RequestParam(value = "dataFim", required = false) String dataFim,
    @RequestParam(value = "estadoEmprestimo", required = false) String estadoEmprestimo,
    @RequestParam(value = "page", required = false, defaultValue = "0") String page,
    @RequestParam(value = "size", required = false, defaultValue = "20") String size,
    @RequestParam(value = "funcionarioId", required = false) String funcionarioId,
    @RequestParam(value = "estado", required = false) String estado)
  {

      final var query = new ListarEmprestimosQuery(tipoEmprestimo, direccaoId, dataInicio, dataFim, estadoEmprestimo, page, size, funcionarioId, estado);

      return queryBus.handle(query);

  }

   @PostMapping(
   value = "/{emprestimoId}/analise-rh-pedido"
  )
  @Operation(
    summary = "Save decisao analise",
    description = "Save decisao analise",
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
  
  public ResponseEntity<String> saveDecisaoAnalise(@Valid @RequestBody AnaliseRhRequestDTO saveDecisaoAnaliseRequest
    , @PathVariable(value = "emprestimoId") String emprestimoId)
  {

      final var command = new SaveDecisaoAnaliseCommand(saveDecisaoAnaliseRequest, emprestimoId);

      return commandBus.send(command);

  }

   @PostMapping(
   value = "/{emprestimoId}/analise-financeiro-pedido"
  )
  @Operation(
    summary = "Save decisao analise financeira",
    description = "Save decisao analise financeira",
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
  
  public ResponseEntity<String> saveDecisaoAnaliseFinanceira(@Valid @RequestBody AnaliseFinanceiroRequestDTO saveDecisaoAnaliseFinanceiraRequest
    , @PathVariable(value = "emprestimoId") String emprestimoId)
  {

      final var command = new SaveDecisaoAnaliseFinanceiraCommand(saveDecisaoAnaliseFinanceiraRequest, emprestimoId);

      return commandBus.send(command);

  }

   @PostMapping(
   value = "/{emprestimoId}/autorizar-comissao-executiva-pedido"
  )
  @Operation(
    summary = "Autorizar comissao executiva",
    description = "Autorizar comissao executiva",
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
  
  public ResponseEntity<String> autorizarComissaoExecutiva(@Valid @RequestBody AutorizacaoComissaoExecutivaDTO autorizarComissaoExecutivaRequest
    , @PathVariable(value = "emprestimoId") String emprestimoId)
  {

      final var command = new AutorizarComissaoExecutivaCommand(autorizarComissaoExecutivaRequest, emprestimoId);

      return commandBus.send(command);

  }

   @PostMapping(
   value = "/{emprestimoId}/elaborar-contrato-pedido"
  )
  @Operation(
    summary = "Elaborar contrato",
    description = "Elaborar contrato",
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
  
  public ResponseEntity<String> elaborarContrato(@Valid @RequestBody ElaboracaoContratoRequestDTO elaborarContratoRequest
    , @PathVariable(value = "emprestimoId") String emprestimoId)
  {

      final var command = new ElaborarContratoCommand(elaborarContratoRequest, emprestimoId);

      return commandBus.send(command);

  }

   @GetMapping(
   value = "{emprestimoId}/plano-financeiro"
  )
  @Operation(
    summary = "Get plano financeiro",
    description = "Get plano financeiro",
    responses = {
      @ApiResponse(
          responseCode = "200",
          
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(
                  implementation = PlanoFinanceiroDTO.class,
                  type = "object")
          )
      )
    }
  )
  
  public ResponseEntity<PlanoFinanceiroDTO> getPlanoFinanceiro(
    @PathVariable(value = "emprestimoId") String emprestimoId)
  {

      final var query = new GetPlanoFinanceiroQuery(emprestimoId);

      return queryBus.handle(query);

  }

   @PostMapping(
   value = "{emprestimoId}/plano-financeiro"
  )
  @Operation(
    summary = "Gerar plano financeiro",
    description = "Gerar plano financeiro",
    responses = {
      @ApiResponse(
          responseCode = "200",
          
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(
                  implementation = PlanoFinanceiroRowDTO.class,
                  type = "object")
          )
      )
    }
  )
  
  public ResponseEntity<List<PlanoFinanceiroRowDTO>> gerarPlanoFinanceiro(
    @PathVariable(value = "emprestimoId") String emprestimoId)
  {

      final var command = new GerarPlanoFinanceiroCommand(emprestimoId);

      return commandBus.send(command);

  }

   @PostMapping(
   value = "fundo-social"
  )
  @Operation(
    summary = "Save fundo social",
    description = "Save fundo social",
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
  
  public ResponseEntity<String> saveFundoSocial(@Valid @RequestBody List<FundoSocialRequestDTO> saveFundoSocialRequest
    )
  {

      final var command = new SaveFundoSocialCommand(saveFundoSocialRequest);

      return commandBus.send(command);

  }

   @GetMapping(
   value = "{emprestimoId}/historico-pagamento"
  )
  @Operation(
    summary = "Get historico pagamento",
    description = "Get historico pagamento",
    responses = {
      @ApiResponse(
          responseCode = "200",
          
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(
                  implementation = HistoricoPagamentoDTO.class,
                  type = "object")
          )
      )
    }
  )
  
  public ResponseEntity<HistoricoPagamentoDTO> getHistoricoPagamento(
    @PathVariable(value = "emprestimoId") String emprestimoId)
  {

      final var query = new GetHistoricoPagamentoQuery(emprestimoId);

      return queryBus.handle(query);

  }

   @PostMapping(
   value = "/pedido-adiantamento"
  )
  @Operation(
    summary = "Save pedidos adiantamento",
    description = "Save pedidos adiantamento",
    responses = {
      @ApiResponse(
          responseCode = "200",
          
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(
                  implementation = IdDTO.class,
                  type = "object")
          )
      )
    }
  )
  
  public ResponseEntity<IdDTO> savePedidosAdiantamento(@Valid @RequestBody PedidoAdiantamentoRequestDTO savePedidosAdiantamentoRequest
    )
  {

      final var command = new SavePedidosAdiantamentoCommand(savePedidosAdiantamentoRequest);

      return commandBus.send(command);

  }

   @PostMapping(
   value = "/{emprestimoId}/analise-rh-adiantamento"
  )
  @Operation(
    summary = "Save decisao analise rh adiantamento",
    description = "Save decisao analise rh adiantamento",
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
  
  public ResponseEntity<String> saveDecisaoAnaliseRhAdiantamento(@Valid @RequestBody AnaliseRhAdiantamentoRequestDTO saveDecisaoAnaliseRhAdiantamentoRequest
    , @PathVariable(value = "emprestimoId") String emprestimoId)
  {

      final var command = new SaveDecisaoAnaliseRhAdiantamentoCommand(saveDecisaoAnaliseRhAdiantamentoRequest, emprestimoId);

      return commandBus.send(command);

  }

   @PostMapping(
   value = "/{emprestimoId}/verificar-pedido-adiantamento"
  )
  @Operation(
    summary = "Verificar pedido adiantamento",
    description = "Verificar pedido adiantamento",
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
  
  public ResponseEntity<String> verificarPedidoAdiantamento(@Valid @RequestBody VerificarAdiantamentoRequestDTO verificarPedidoAdiantamentoRequest
    , @PathVariable(value = "emprestimoId") String emprestimoId)
  {

      final var command = new VerificarPedidoAdiantamentoCommand(verificarPedidoAdiantamentoRequest, emprestimoId);

      return commandBus.send(command);

  }

   @PostMapping(
   value = "{emprestimoId}/anexar-comprovativo"
  )
  @Operation(
    summary = "Anexar comprovativo pagamento",
    description = "Anexar comprovativo pagamento",
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
  
  public ResponseEntity<String> anexarComprovativoPagamento(@Valid @RequestBody DocumentoDTO anexarComprovativoPagamentoRequest
    , @PathVariable(value = "emprestimoId") String emprestimoId)
  {

      final var command = new AnexarComprovativoPagamentoCommand(anexarComprovativoPagamentoRequest, emprestimoId);

      return commandBus.send(command);

  }

   @PostMapping(
   value = "/{emprestimoId}/analise-rh-renegociacao"
  )
  @Operation(
    summary = "Save decisao analise renegociacao",
    description = "Save decisao analise renegociacao",
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
  
  public ResponseEntity<String> saveDecisaoAnaliseRenegociacao(@Valid @RequestBody AnaliseRhRequestDTO saveDecisaoAnaliseRenegociacaoRequest
    , @PathVariable(value = "emprestimoId") String emprestimoId)
  {

      final var command = new SaveDecisaoAnaliseRenegociacaoCommand(saveDecisaoAnaliseRenegociacaoRequest, emprestimoId);

      return commandBus.send(command);

  }

   @PostMapping(
   value = "/{emprestimoId}/analise-financeiro-renegociacao"
  )
  @Operation(
    summary = "Save decisao analise financeira renegociacao",
    description = "Save decisao analise financeira renegociacao",
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
  
  public ResponseEntity<String> saveDecisaoAnaliseFinanceiraRenegociacao(@Valid @RequestBody AnaliseFinanceiroRequestDTO saveDecisaoAnaliseFinanceiraRenegociacaoRequest
    , @PathVariable(value = "emprestimoId") String emprestimoId)
  {

      final var command = new SaveDecisaoAnaliseFinanceiraRenegociacaoCommand(saveDecisaoAnaliseFinanceiraRenegociacaoRequest, emprestimoId);

      return commandBus.send(command);

  }

   @PostMapping(
   value = "/{emprestimoId}/autorizar-comissao-executiva-renegociacao"
  )
  @Operation(
    summary = "Autorizar comissao executiva renegociacao",
    description = "Autorizar comissao executiva renegociacao",
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
  
  public ResponseEntity<String> autorizarComissaoExecutivaRenegociacao(@Valid @RequestBody AutorizacaoComissaoExecutivaDTO autorizarComissaoExecutivaRenegociacaoRequest
    , @PathVariable(value = "emprestimoId") String emprestimoId)
  {

      final var command = new AutorizarComissaoExecutivaRenegociacaoCommand(autorizarComissaoExecutivaRenegociacaoRequest, emprestimoId);

      return commandBus.send(command);

  }

   @PostMapping(
   value = "/{emprestimoId}/elaborar-contrato-renegociacao"
  )
  @Operation(
    summary = "Elaborar contrato renegociacao",
    description = "Elaborar contrato renegociacao",
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
  
  public ResponseEntity<String> elaborarContratoRenegociacao(@Valid @RequestBody ElaboracaoContratoRequestDTO elaborarContratoRenegociacaoRequest
    , @PathVariable(value = "emprestimoId") String emprestimoId)
  {

      final var command = new ElaborarContratoRenegociacaoCommand(elaborarContratoRenegociacaoRequest, emprestimoId);

      return commandBus.send(command);

  }

}