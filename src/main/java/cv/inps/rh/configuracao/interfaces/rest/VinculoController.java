/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME */

package cv.inps.rh.configuracao.interfaces.rest;

import cv.igrp.framework.core.domain.CommandBus;
import cv.igrp.framework.core.domain.QueryBus;
import cv.igrp.framework.stereotype.IgrpController;
import cv.inps.rh.configuracao.application.commands.AssociarVinculoSituacaoCommand;
import cv.inps.rh.configuracao.application.commands.CriarVinculoMovimentoCommand;
import cv.inps.rh.configuracao.application.commands.EditarVinculoMovimentoCommand;
import cv.inps.rh.configuracao.application.commands.EditarVinculoSituacaoCommand;
import cv.inps.rh.configuracao.application.commands.EliminarVinculoMovimentoCommand;
import cv.inps.rh.configuracao.application.commands.EliminarVinculoSituacaoCommand;
import cv.inps.rh.configuracao.application.dto.VinculoMovimentoRequestDTO;
import cv.inps.rh.configuracao.application.dto.VinculoMovimentoResponseDTO;
import cv.inps.rh.configuracao.application.queries.GetMovimentosByVinculoQuery;
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

  @PostMapping(
      value = "vinculo/associar-situacao"
  )
  @Operation(
      summary = "Associar vinculo situacao",
      description = "Associar vinculo situacao",
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

  public ResponseEntity<String> associarVinculoSituacao(
      @RequestParam(value = "vinculoId") String vinculoId,
      @RequestParam(value = "situacaoId") String situacaoId) {

    final var command = new AssociarVinculoSituacaoCommand(vinculoId, situacaoId);

    return commandBus.send(command);

  }

  @PutMapping("vinculo/associar-situacao/{id}")
  @Operation(summary = "Editar associação vinculo situação laboral")
  public ResponseEntity<String> editarVinculoSituacao(
      @PathVariable Long id,
      @RequestParam(value = "vinculoId") String vinculoId,
      @RequestParam(value = "situacaoId") String situacaoId) {

    final var command = new EditarVinculoSituacaoCommand(id, vinculoId, situacaoId);

    return commandBus.send(command);
  }

  @DeleteMapping("vinculo/associar-situacao/{id}")
  @Operation(summary = "Eliminar associação vinculo situação laboral (soft delete)")
  public ResponseEntity<Void> eliminarVinculoSituacao(
      @PathVariable Long id) {

    final var command = new EliminarVinculoSituacaoCommand(id);

    return commandBus.send(command);
  }

  @GetMapping("vinculo/{vinculoId}/movimentos")
  @Operation(summary = "Listar tipos de movimento associados a um vínculo")
  public ResponseEntity<List<VinculoMovimentoResponseDTO>> getMovimentosByVinculo(
      @PathVariable Long vinculoId) {

    final var query = new GetMovimentosByVinculoQuery(vinculoId);

    return queryBus.handle(query);
  }

  @PostMapping("vinculo/{vinculoId}/movimentos")
  @Operation(summary = "Associar tipo de movimento a um vínculo")
  public ResponseEntity<VinculoMovimentoResponseDTO> criarVinculoMovimento(
      @PathVariable Long vinculoId,
      @Valid @RequestBody VinculoMovimentoRequestDTO dto) {

    final var command = new CriarVinculoMovimentoCommand(vinculoId, dto);

    return commandBus.send(command);
  }

  @PutMapping("vinculo/{vinculoId}/movimentos/{id}")
  @Operation(summary = "Editar associação tipo de movimento a um vínculo")
  public ResponseEntity<VinculoMovimentoResponseDTO> editarVinculoMovimento(
      @PathVariable Long vinculoId,
      @PathVariable Long id,
      @Valid @RequestBody VinculoMovimentoRequestDTO dto) {

    final var command = new EditarVinculoMovimentoCommand(vinculoId, id, dto);

    return commandBus.send(command);
  }

  @DeleteMapping("vinculo/{vinculoId}/movimentos/{id}")
  @Operation(summary = "Eliminar associação tipo de movimento de um vínculo (soft delete)")
  public ResponseEntity<Void> eliminarVinculoMovimento(
      @PathVariable Long vinculoId,
      @PathVariable Long id) {

    final var command = new EliminarVinculoMovimentoCommand(vinculoId, id);

    return commandBus.send(command);
  }

}
