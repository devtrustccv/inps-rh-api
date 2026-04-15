/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME */

package cv.inps.rh.processamento.interfaces.rest;

import cv.igrp.framework.core.domain.CommandBus;
import cv.igrp.framework.core.domain.QueryBus;
import cv.igrp.framework.stereotype.IgrpController;
import cv.inps.rh.processamento.application.commands.ExecutarAcaoNoProcessamentoCommand;
import cv.inps.rh.processamento.application.commands.ProcessarSalarioCommand;
import cv.inps.rh.processamento.application.commands.RemoverFuncionariosProcessamentoSalarialCommand;
import cv.inps.rh.processamento.application.dto.*;
import cv.inps.rh.processamento.application.queries.GetDadosValidacaoQuery;
import cv.inps.rh.processamento.application.queries.GetDetalhesProcessamentoQuery;
import cv.inps.rh.processamento.application.queries.GetProcessamentoSalarialQuery;
import cv.inps.rh.processamento.application.queries.GetResumoProcessamentoQuery;
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
@RequestMapping(path = "processamento-salarial")
@Tag(
    name = "Processamento",
    description = "Processamento Salarial"
)
public class ProcessoSalarialController {


  private final QueryBus queryBus;
  private final CommandBus commandBus;

  public ProcessoSalarialController(QueryBus queryBus, CommandBus commandBus) {
    this.queryBus = queryBus;
    this.commandBus = commandBus;
  }

  @PostMapping(
      value = "/folha/funcionarios/excluir"
  )
  @Operation(
      summary = "Remover funcionarios processamento salarial",
      description = "Remover funcionarios processamento salarial",
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

  public ResponseEntity<String> removerFuncionariosProcessamentoSalarial(@Valid @RequestBody MarcarNaoProcessadoRequestDTO removerFuncionariosProcessamentoSalarialRequest
  ) {

    final var command = new RemoverFuncionariosProcessamentoSalarialCommand(removerFuncionariosProcessamentoSalarialRequest);

    return commandBus.send(command);

  }

  @GetMapping(
  )
  @Operation(
      summary = "Get processamento salarial",
      description = "Get processamento salarial",
      responses = {
          @ApiResponse(
              responseCode = "200",

              content = @Content(
                  mediaType = "application/json",
                  schema = @Schema(
                      implementation = WrapperProcessamentoSalarialDTO.class,
                      type = "object")
              )
          )
      }
  )

  public ResponseEntity<WrapperProcessamentoSalarialDTO> getProcessamentoSalarial(
      @RequestParam(value = "dataInicio", required = false) String dataInicio,
      @RequestParam(value = "dataFim", required = false) String dataFim,
      @RequestParam(value = "direcaoId", required = false) String direcaoId,
      @RequestParam(value = "tipo", required = false) String tipo,
      @RequestParam(value = "estado", required = false) String estado,
      @RequestParam(value = "page", required = false, defaultValue = "0") String page,
      @RequestParam(value = "size", required = false, defaultValue = "20") String size) {

    final var query = new GetProcessamentoSalarialQuery(dataInicio, dataFim, direcaoId, tipo, estado, page, size);

    return queryBus.handle(query);

  }

  @PostMapping(
      value = "processamento-acao"
  )
  @Operation(
      summary = "Executar acao no processamento",
      description = "Executar acao no processamento",
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

  public ResponseEntity<String> executarAcaoNoProcessamento(@Valid @RequestBody ProcessamentoActionRequestDTO executarAcaoNoProcessamentoRequest
  ) {

    final var command = new ExecutarAcaoNoProcessamentoCommand(executarAcaoNoProcessamentoRequest);

    return commandBus.send(command);

  }

  @PostMapping(
      value = "processar"
  )
  @Operation(
      summary = "Processar salario",
      description = "Processar salario",
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

  public ResponseEntity<String> processarSalario(@Valid @RequestBody ProcessamentoSalarioRequestDTO processarSalarioRequest
  ) {

    final var command = new ProcessarSalarioCommand(processarSalarioRequest);

    return commandBus.send(command);

  }

  @GetMapping(
      value = "resumo"
  )
  @Operation(
      summary = "Get resumo processamento",
      description = "Get resumo processamento",
      responses = {
          @ApiResponse(
              responseCode = "200",

              content = @Content(
                  mediaType = "application/json",
                  schema = @Schema(
                      implementation = ResumoProcessamentoDTO.class,
                      type = "object")
              )
          )
      }
  )

  public ResponseEntity<ResumoProcessamentoDTO> getResumoProcessamento(
      @RequestParam(value = "processamentoId") String processamentoId,
      @RequestParam(value = "ccId", required = false) String ccId,
      @RequestParam(value = "ano", required = false) String ano,
      @RequestParam(value = "mes", required = false) String mes) {

    final var query = new GetResumoProcessamentoQuery(processamentoId, ccId, ano, mes);

    return queryBus.handle(query);

  }

  @GetMapping(
      value = "resumo/detalhes"
  )
  @Operation(
      summary = "Get detalhes processamento",
      description = "Get detalhes processamento",
      responses = {
          @ApiResponse(
              responseCode = "200",

              content = @Content(
                  mediaType = "application/json",
                  schema = @Schema(
                      implementation = DetalhesProcessamentoDTO.class,
                      type = "object")
              )
          )
      }
  )

  public ResponseEntity<DetalhesProcessamentoDTO> getDetalhesProcessamento(
      @RequestParam(value = "tipoMovimento") String tipoMovimento,
      @RequestParam(value = "procSalId") String procSalId,
      @RequestParam(value = "tipoDetalhe") String tipoDetalhe) {

    final var query = new GetDetalhesProcessamentoQuery(tipoMovimento, procSalId, tipoDetalhe);

    return queryBus.handle(query);

  }

  @GetMapping(
      value = "processar/dados-validacao"
  )
  @Operation(
      summary = "Get dados validacao",
      description = "Get dados validacao",
      responses = {
          @ApiResponse(
              responseCode = "200",

              content = @Content(
                  mediaType = "application/json",
                  schema = @Schema(
                      implementation = DadosValidacaoDTO.class,
                      type = "object")
              )
          )
      }
  )

  public ResponseEntity<List<DadosValidacaoDTO>> getDadosValidacao(
      @RequestParam(value = "tipoValidacao", required = false) String tipoValidacao,
      @RequestParam(value = "mesAtual", required = false) String mesAtual,
      @RequestParam(value = "mesAnterior", required = false) String mesAnterior,
      @RequestParam(value = "processamentoIds", required = false) String processamentoIds) {

    final var query = new GetDadosValidacaoQuery(tipoValidacao, mesAtual, mesAnterior, processamentoIds);

    return queryBus.handle(query);

  }

}
