/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME */

package cv.inps.rh.processamento.interfaces.rest;

import cv.igrp.framework.core.domain.CommandBus;
import cv.igrp.framework.core.domain.QueryBus;
import cv.igrp.framework.stereotype.IgrpController;
import cv.inps.rh.processamento.application.commands.*;
import cv.inps.rh.processamento.application.dto.*;
import cv.inps.rh.processamento.application.queries.*;
import cv.inps.rh.shared.application.dto.ProcedureMsgDTO;
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
@Tag(name = "Processamento", description = "Processamento Salarial")
public class ProcessoSalarialController {

  private final QueryBus queryBus;
  private final CommandBus commandBus;

  public ProcessoSalarialController(QueryBus queryBus, CommandBus commandBus) {
    this.queryBus = queryBus;
    this.commandBus = commandBus;
  }

  @PostMapping(value = "/folha/funcionarios/excluir")
  @Operation(
      summary = "Remover funcionarios processamento salarial",
      description = "Remover funcionarios processamento salarial",
      responses = {
          @ApiResponse(
              responseCode = "200",
              content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))
          ),
      }
  )
  public ResponseEntity<String> removerFuncionariosProcessamentoSalarial(
      @Valid @RequestBody MarcarNaoProcessadoRequestDTO removerFuncionariosProcessamentoSalarialRequest
  ) {
    final var command = new RemoverFuncionariosProcessamentoSalarialCommand(
        removerFuncionariosProcessamentoSalarialRequest
    );

    return commandBus.send(command);
  }

  @GetMapping
  @Operation(
      summary = "Get processamento salarial",
      description = "Get processamento salarial",
      responses = {
          @ApiResponse(
              responseCode = "200",
              content = @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = WrapperProcessamentoSalarialDTO.class)
              )
          ),
      }
  )
  public ResponseEntity<WrapperProcessamentoSalarialDTO> getProcessamentoSalarial(
      @RequestParam(value = "dataInicio", required = false) String dataInicio,
      @RequestParam(value = "dataFim", required = false) String dataFim,
      @RequestParam(value = "direcaoId", required = false) String direcaoId,
      @RequestParam(value = "tipo", required = false) String tipo,
      @RequestParam(value = "estado", required = false) String estado,
      @RequestParam(value = "page", required = false, defaultValue = "0") String page,
      @RequestParam(value = "size", required = false, defaultValue = "20") String size
  ) {
    final var query = new GetProcessamentoSalarialQuery(dataInicio, dataFim, direcaoId, tipo, estado, page, size);

    return queryBus.handle(query);
  }

  @PostMapping(value = "processamento-acao")
  @Operation(
      summary = "Executar acao no processamento",
      description = "Executar acao no processamento",
      responses = {
          @ApiResponse(
              responseCode = "200",
              content = @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = ProcedureMsgDTO.class)
              )
          ),
      }
  )
  public ResponseEntity<ProcedureMsgDTO> executarAcaoNoProcessamento(
      @Valid @RequestBody ProcessamentoActionRequestDTO executarAcaoNoProcessamentoRequest
  ) {
    final var command = new ExecutarAcaoNoProcessamentoCommand(executarAcaoNoProcessamentoRequest);

    return commandBus.send(command);
  }

  @PostMapping(value = "processar")
  @Operation(
      summary = "Processar salario",
      description = "Processar salario",
      responses = {
          @ApiResponse(
              responseCode = "200",
              content = @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = ProcedureMsgDTO.class)
              )
          ),
      }
  )
  public ResponseEntity<ProcedureMsgDTO> processarSalario(
      @Valid @RequestBody ProcessamentoSalarioRequestDTO processarSalarioRequest
  ) {
    final var command = new ProcessarSalarioCommand(processarSalarioRequest);

    return commandBus.send(command);
  }

  @GetMapping(value = "resumo")
  @Operation(
      summary = "Get resumo processamento",
      description = "Get resumo processamento",
      responses = {
          @ApiResponse(
              responseCode = "200",
              content = @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = ResumoProcessamentoDTO.class)
              )
          ),
      }
  )
  public ResponseEntity<ResumoProcessamentoDTO> getResumoProcessamento(
      @RequestParam(value = "processamentoId") Long processamentoId
  ) {
    final var query = new GetResumoProcessamentoQuery(processamentoId);

    return queryBus.handle(query);
  }

  @GetMapping(value = "resumo/detalhes")
  @Operation(
      summary = "Get detalhes processamento",
      description = "Get detalhes processamento",
      responses = {
          @ApiResponse(
              responseCode = "200",
              content = @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = DetalhesProcessamentoDTO.class)
              )
          ),
      }
  )
  public ResponseEntity<DetalhesProcessamentoDTO> getDetalhesProcessamento(
      @RequestParam(value = "tipoMovimento") String tipoMovimento,
      @RequestParam(value = "procSalId") String procSalId,
      @RequestParam(value = "tipoDetalhe") String tipoDetalhe
  ) {
    final var query = new GetDetalhesProcessamentoQuery(tipoMovimento, procSalId, tipoDetalhe);

    return queryBus.handle(query);
  }

  @GetMapping(value = "processar/dados-validacao")
  @Operation(
      summary = "Get dados validacao",
      description = "Get dados validacao",
      responses = {
          @ApiResponse(
              responseCode = "200",
              content = @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = DadosValidacaoDTO.class)
              )
          ),
      }
  )
  public ResponseEntity<List<DadosValidacaoDTO>> getDadosValidacao(
      @RequestParam(value = "tipoValidacao", required = false) String tipoValidacao,
      @RequestParam(value = "mesAtual", required = false) String mesAtual,
      @RequestParam(value = "mesAnterior", required = false) String mesAnterior,
      @RequestParam(value = "processamentoIds", required = false) String processamentoIds
  ) {
    final var query = new GetDadosValidacaoQuery(tipoValidacao, mesAtual, mesAnterior, processamentoIds);

    return queryBus.handle(query);
  }

  @GetMapping(value = "subsidio-natal")
  @Operation(
      summary = "Get subsidio natal",
      description = "Get subsidio natal",
      responses = {
          @ApiResponse(
              responseCode = "200",
              content = @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = SubsidioResponseNatalDTO.class)
              )
          ),
      }
  )
  public ResponseEntity<List<SubsidioResponseNatalDTO>> getSubsidioNatal(
      @RequestParam(value = "direcaoId", required = false) Long direcaoId,
      @RequestParam(value = "funcionarioId") Long funcionarioId,
      @RequestParam(value = "valorBrinde", required = false) Long valorBrinde,
      @RequestParam(value = "anoProcessamento") Long anoProcessamento
  ) {
    final var query = new GetSubsidioNatalQuery(direcaoId, funcionarioId, valorBrinde, anoProcessamento);

    return queryBus.handle(query);
  }

  @GetMapping(value = "subsidio-feria")
  @Operation(
      summary = "Get subsidio ferias",
      description = "Get subsidio ferias",
      responses = {
          @ApiResponse(
              responseCode = "200",
              content = @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = SubsidioFeriasResponseDTO.class)
              )
          ),
      }
  )
  public ResponseEntity<List<SubsidioFeriasResponseDTO>> getSubsidioFerias(
      @RequestParam(value = "direcaoId", required = false) Long direcaoId,
      @RequestParam(value = "funcionarioId", required = false) Long funcionarioId,
      @RequestParam(value = "dataProcessamento", required = false) String dataProcessamento
  ) {
    final var query = new GetSubsidioFeriasQuery(direcaoId, funcionarioId, dataProcessamento);

    return queryBus.handle(query);
  }

  @PostMapping(value = "activar-inactivar-subsidio-natal")
  @Operation(
      summary = "Ativar inactivar subsidio natal",
      description = "Ativar inactivar subsidio natal",
      responses = {
          @ApiResponse(
              responseCode = "200",
              content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))
          ),
      }
  )
  public ResponseEntity<String> ativarInactivarSubsidioNatal(
      @Valid @RequestBody AtivarInativarSubsidioNatalDTO ativarInactivarSubsidioNatalRequest
  ) {
    final var command = new AtivarInactivarSubsidioNatalCommand(ativarInactivarSubsidioNatalRequest);

    return commandBus.send(command);
  }

  @GetMapping(value = "aumento-salarial")
  @Operation(
      summary = "Get lista aumento salarial",
      description = "Get lista aumento salarial",
      responses = {
          @ApiResponse(
              responseCode = "200",
              content = @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = AumentoListDTO.class)
              )
          ),
      }
  )
  public ResponseEntity<AumentoListDTO> getListaAumentoSalarial(
      @RequestParam(value = "ano", required = false) Integer ano,
      @RequestParam(value = "page", required = false, defaultValue = "0") Integer page,
      @RequestParam(value = "size", required = false, defaultValue = "20") Integer size
  ) {
    final var query = new GetListaAumentoSalarialQuery(ano, page, size);

    return queryBus.handle(query);
  }

  @PostMapping(value = "aumento-salarial")
  @Operation(
      summary = "Save aumento salarial",
      description = "Save aumento salarial",
      responses = {
          @ApiResponse(
              responseCode = "200",
              content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))
          ),
      }
  )
  public ResponseEntity<String> saveAumentoSalarial(
      @Valid @RequestBody AumentoSalarialRequestDTO saveAumentoSalarialRequest
  ) {
    final var command = new SaveAumentoSalarialCommand(saveAumentoSalarialRequest);

    return commandBus.send(command);
  }

  @PostMapping(value = "aumento-salarial/{aumentoSalarialId}/validar")
  @Operation(
      summary = "Validar aumento salarial",
      description = "Validar aumento salarial",
      responses = {
          @ApiResponse(
              responseCode = "200",
              content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))
          ),
      }
  )
  public ResponseEntity<String> validarAumentoSalarial(
      @PathVariable(value = "aumentoSalarialId") String aumentoSalarialId
  ) {
    final var command = new ValidarAumentoSalarialCommand(aumentoSalarialId);

    return commandBus.send(command);
  }

  @GetMapping(value = "aumento-salarial/{aumentoSalarialId}")
  @Operation(
      summary = "Get detalhes aumento salarial",
      description = "Get detalhes aumento salarial",
      responses = {
          @ApiResponse(
              responseCode = "200",
              content = @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = AumentoSalarialResponseDTO.class)
              )
          ),
      }
  )
  public ResponseEntity<AumentoSalarialResponseDTO> getDetalhesAumentoSalarial(
      @PathVariable(value = "aumentoSalarialId") String aumentoSalarialId
  ) {
    final var query = new GetDetalhesAumentoSalarialQuery(aumentoSalarialId);

    return queryBus.handle(query);
  }

  @PutMapping(value = "aumento-salarial/{aumentoSalarialId}")
  @Operation(
      summary = "Update aumento salarial",
      description = "Update aumento salarial",
      responses = {
          @ApiResponse(
              responseCode = "200",
              content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))
          ),
      }
  )
  public ResponseEntity<String> updateAumentoSalarial(
      @Valid @RequestBody AumentoSalarialRequestDTO updateAumentoSalarialRequest,
      @PathVariable(value = "aumentoSalarialId") String aumentoSalarialId
  ) {
    final var command = new UpdateAumentoSalarialCommand(updateAumentoSalarialRequest, aumentoSalarialId);

    return commandBus.send(command);
  }

  @GetMapping(value = "aumento-salarial/colaboradores")
  @Operation(
      summary = "Get colabores aumento salarial",
      description = "Get colabores aumento salarial",
      responses = {
          @ApiResponse(
              responseCode = "200",
              content = @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = ColaboradorAumentoDTO.class)
              )
          ),
      }
  )
  public ResponseEntity<ColaboradorAumentoDTO> getColaboresAumentoSalarial(
      @RequestParam(value = "direcaoId", required = false) Long direcaoId,
      @RequestParam(value = "organicaId", required = false) Long organicaId,
      @RequestParam(value = "page", required = false, defaultValue = "0") Integer page,
      @RequestParam(value = "size", required = false, defaultValue = "20") Integer size
  ) {
    final var query = new GetColaboresAumentoSalarialQuery(direcaoId, organicaId, page, size);

    return queryBus.handle(query);
  }
}
