/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME */

package cv.inps.rh.configuracao.interfaces.rest;

import cv.igrp.framework.core.domain.CommandBus;
import cv.igrp.framework.core.domain.QueryBus;
import cv.igrp.framework.stereotype.IgrpController;
import cv.inps.rh.configuracao.application.commands.SyncVinculoMovimentosCommand;
import cv.inps.rh.configuracao.application.commands.SyncVinculoSituacoesCommand;
import cv.inps.rh.configuracao.application.dto.VinculoMovimentoRequestDTO;
import cv.inps.rh.configuracao.application.dto.VinculoMovimentoResponseDTO;
import cv.inps.rh.configuracao.application.dto.VinculoSituacaoLaboralRequestDTO;
import cv.inps.rh.configuracao.application.dto.VinculoSituacaoLaboralResponseDTO;
import cv.inps.rh.configuracao.application.queries.GetMovimentosByVinculoQuery;
import cv.inps.rh.configuracao.application.queries.GetSituacoesByVinculoQuery;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@IgrpController
@RestController
@RequestMapping(path = "configuracao")
@Tag(
    name = "Configuracao",
    description = "Gestão de Vínculos"
)
public class VinculoController {


  private final QueryBus queryBus;
  private final CommandBus commandBus;

  public VinculoController(QueryBus queryBus, CommandBus commandBus) {

    this.queryBus = queryBus;
    this.commandBus = commandBus;
  }

  // ── Vínculo ↔ Situação Laboral ──

  @GetMapping("vinculo/{vinculoId}/situacoes-laborais")
  @Operation(summary = "Listar situações laborais associadas a um vínculo")
  public ResponseEntity<List<VinculoSituacaoLaboralResponseDTO>> getSituacoesByVinculo(
      @PathVariable String vinculoId) {

    final var query = new GetSituacoesByVinculoQuery(vinculoId);

    return queryBus.handle(query);
  }

  @PutMapping("vinculo/{vinculoId}/situacoes-laborais")
  @Operation(summary = "Sincronizar situações laborais de um vínculo (add/update/delete)")
  public ResponseEntity<List<VinculoSituacaoLaboralResponseDTO>> syncVinculoSituacoes(
      @PathVariable String vinculoId,
      @Valid @RequestBody List<VinculoSituacaoLaboralRequestDTO> situacoes) {

    final var command = new SyncVinculoSituacoesCommand(vinculoId, situacoes);

    return commandBus.send(command);
  }

  // ── Vínculo ↔ Tipo Movimento ──

  @GetMapping("vinculo/{vinculoId}/movimentos")
  @Operation(summary = "Listar tipos de movimento associados a um vínculo")
  public ResponseEntity<List<VinculoMovimentoResponseDTO>> getMovimentosByVinculo(
      @PathVariable String vinculoId) {

    final var query = new GetMovimentosByVinculoQuery(vinculoId);

    return queryBus.handle(query);
  }

  @PutMapping("vinculo/{vinculoId}/movimentos")
  @Operation(summary = "Sincronizar tipos de movimento de um vínculo (add/update/delete)")
  public ResponseEntity<List<VinculoMovimentoResponseDTO>> syncVinculoMovimentos(
      @PathVariable String vinculoId,
      @Valid @RequestBody List<VinculoMovimentoRequestDTO> movimentos) {

    final var command = new SyncVinculoMovimentosCommand(vinculoId, movimentos);

    return commandBus.send(command);
  }

}
