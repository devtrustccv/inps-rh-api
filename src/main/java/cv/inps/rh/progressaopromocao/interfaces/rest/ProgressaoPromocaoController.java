/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME */

package cv.inps.rh.progressaopromocao.interfaces.rest;

import cv.igrp.framework.core.domain.CommandBus;
import cv.igrp.framework.core.domain.QueryBus;
import cv.igrp.framework.stereotype.IgrpController;
import cv.inps.rh.progressaopromocao.application.commands.*;
import cv.inps.rh.progressaopromocao.application.dto.AnexarOrdemServicoRequestDTO;
import cv.inps.rh.progressaopromocao.application.dto.HistoricoIdsDTO;
import cv.inps.rh.progressaopromocao.application.dto.ListaProgressaoPromocaoDTO;
import cv.inps.rh.progressaopromocao.application.queries.GetHistoricoProgressaPromocaoQuery;
import cv.inps.rh.progressaopromocao.application.queries.GetListaSimulacaoProgressaPromocaoQuery;
import cv.inps.rh.progressaopromocao.application.queries.GetListaValidacaoProgressaPromocaoQuery;
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
@RequestMapping(path = "progressao-promocao")
@Tag(name = "Progressaopromocao", description = "Data related t progression and promotion")
public class ProgressaoPromocaoController {

  private final QueryBus queryBus;
  private final CommandBus commandBus;

  public ProgressaoPromocaoController(QueryBus queryBus, CommandBus commandBus) {
    this.queryBus = queryBus;
    this.commandBus = commandBus;
  }

  @GetMapping(value = "historico")
  @Operation(
      summary = "Get historico progressa promocao",
      description = "Get historico progressa promocao",
      responses = {
          @ApiResponse(
              responseCode = "200",
              content = @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = ListaProgressaoPromocaoDTO.class)
              )
          ),
      }
  )
  public ResponseEntity<ListaProgressaoPromocaoDTO> getHistoricoProgressaPromocao(
      @RequestParam(value = "progressaoPromocao", required = false) String progressaoPromocao,
      @RequestParam(value = "colaborador", required = false) String colaborador,
      @RequestParam(value = "carreiraId", required = false) String carreiraId,
      @RequestParam(value = "dataDe", required = false) String dataDe,
      @RequestParam(value = "dataAte", required = false) String dataAte,
      @RequestParam(value = "page", required = false, defaultValue = "0") String page,
      @RequestParam(value = "size", required = false, defaultValue = "20") String size
  ) {
    final var query = new GetHistoricoProgressaPromocaoQuery(
        progressaoPromocao,
        colaborador,
        carreiraId,
        dataDe,
        dataAte,
        page,
        size
    );

    return queryBus.handle(query);
  }

  @GetMapping(value = "validacao")
  @Operation(
      summary = "Get lista validacao progressa promocao",
      description = "Get lista validacao progressa promocao",
      responses = {
          @ApiResponse(
              responseCode = "200",
              content = @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = ListaProgressaoPromocaoDTO.class)
              )
          ),
      }
  )
  public ResponseEntity<ListaProgressaoPromocaoDTO> getListaValidacaoProgressaPromocao(
      @RequestParam(value = "progressaoPromocao", required = false) String progressaoPromocao,
      @RequestParam(value = "colaborador", required = false) String colaborador,
      @RequestParam(value = "carreiraId", required = false) String carreiraId,
      @RequestParam(value = "dataDe", required = false) String dataDe,
      @RequestParam(value = "dataAte", required = false) String dataAte,
      @RequestParam(value = "page", required = false, defaultValue = "0") String page,
      @RequestParam(value = "size", required = false, defaultValue = "20") String size
  ) {
    final var query = new GetListaValidacaoProgressaPromocaoQuery(
        progressaoPromocao,
        colaborador,
        carreiraId,
        dataDe,
        dataAte,
        page,
        size
    );

    return queryBus.handle(query);
  }

  @PostMapping(value = "ordem-servico")
  @Operation(
      summary = "Anexar ordem servico",
      description = "Anexar ordem servico",
      responses = {
          @ApiResponse(
              responseCode = "200",
              content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))
          ),
      }
  )
  public ResponseEntity<String> anexarOrdemServico(
      @Valid @RequestBody AnexarOrdemServicoRequestDTO anexarOrdemServicoRequest
  ) {
    final var command = new AnexarOrdemServicoCommand(anexarOrdemServicoRequest);

    return commandBus.send(command);
  }

  @PostMapping(value = "simular")
  @Operation(
      summary = "Simular progressao promocao",
      description = "Simular progressao promocao",
      responses = {
          @ApiResponse(
              responseCode = "200",
              content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))
          ),
      }
  )
  public ResponseEntity<String> simularProgressaoPromocao() {
    final var command = new SimularProgressaoPromocaoCommand();

    return commandBus.send(command);
  }

  @GetMapping(value = "simulacao")
  @Operation(
      summary = "Get lista simulacao progressa promocao",
      description = "Get lista simulacao progressa promocao",
      responses = {
          @ApiResponse(
              responseCode = "200",
              content = @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = ListaProgressaoPromocaoDTO.class)
              )
          ),
      }
  )
  public ResponseEntity<ListaProgressaoPromocaoDTO> getListaSimulacaoProgressaPromocao(
      @RequestParam(value = "progressaoPromocao", required = false) String progressaoPromocao,
      @RequestParam(value = "colaborador", required = false) String colaborador,
      @RequestParam(value = "carreiraId", required = false) String carreiraId,
      @RequestParam(value = "dataDe", required = false) String dataDe,
      @RequestParam(value = "dataAte", required = false) String dataAte,
      @RequestParam(value = "page", required = false, defaultValue = "0") String page,
      @RequestParam(value = "size", required = false, defaultValue = "20") String size,
      @RequestParam(value = "colaboradorId", required = false) String colaboradorId
  ) {
    final var query = new GetListaSimulacaoProgressaPromocaoQuery(
        progressaoPromocao,
        colaborador,
        carreiraId,
        dataDe,
        dataAte,
        page,
        size,
        colaboradorId
    );

    return queryBus.handle(query);
  }

  @PostMapping(value = "enviar-historico")
  @Operation(
      summary = "Enviar historico progressao promocao",
      description = "Enviar historico progressao promocao",
      responses = {
          @ApiResponse(
              responseCode = "200",
              content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))
          ),
      }
  )
  public ResponseEntity<String> enviarHistoricoProgressaoPromocao(
      @Valid @RequestBody HistoricoIdsDTO enviarHistoricoProgressaoPromocaoRequest
  ) {
    final var command = new EnviarHistoricoProgressaoPromocaoCommand(enviarHistoricoProgressaoPromocaoRequest);

    return commandBus.send(command);
  }

  @PostMapping(value = "extrair-ordem-servico")
  @Operation(
      summary = "Extrair ordem servico",
      description = "Extrair ordem servico",
      responses = {
          @ApiResponse(
              responseCode = "200",
              content = @Content(mediaType = "application/json", schema = @Schema(implementation = byte[].class))
          ),
      }
  )
  public ResponseEntity<byte[]> extrairOrdemServico(@Valid @RequestBody HistoricoIdsDTO extrairOrdemServicoRequest) {
    final var command = new ExtrairOrdemServicoCommand(extrairOrdemServicoRequest);

    return commandBus.send(command);
  }

  @PostMapping(value = "validar")
  @Operation(
      summary = "Validar simulacao",
      description = "Validar simulacao",
      responses = {
          @ApiResponse(
              responseCode = "200",
              content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))
          ),
      }
  )
  public ResponseEntity<String> validarSimulacao(@Valid @RequestBody HistoricoIdsDTO validarSimulacaoRequest) {
    final var command = new ValidarSimulacaoCommand(validarSimulacaoRequest);

    return commandBus.send(command);
  }

  @PostMapping(value = "confirmar")
  @Operation(
      summary = "Confirmar progressao",
      description = "Confirmar progressao",
      responses = {
          @ApiResponse(
              responseCode = "200",
              content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))
          ),
      }
  )
  public ResponseEntity<String> confirmarProgressao(@RequestParam(value = "validacaoId") String validacaoId) {
    final var command = new ConfirmarProgressaoCommand(validacaoId);

    return commandBus.send(command);
  }
}
