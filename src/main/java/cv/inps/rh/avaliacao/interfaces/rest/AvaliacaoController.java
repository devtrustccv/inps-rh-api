/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME */

package cv.inps.rh.avaliacao.interfaces.rest;

import cv.igrp.framework.core.domain.CommandBus;
import cv.igrp.framework.core.domain.QueryBus;
import cv.igrp.framework.stereotype.IgrpController;
import cv.inps.rh.avaliacao.application.commands.*;
import cv.inps.rh.avaliacao.application.dto.*;
import cv.inps.rh.avaliacao.application.queries.*;
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
@RequestMapping(path = "avaliacao-desempenho/avaliacoes")
@Tag(
    name = "Avaliacao",
    description = "gest avaliacao"
)
public class AvaliacaoController {


  private final QueryBus queryBus;
  private final CommandBus commandBus;

  public AvaliacaoController(QueryBus queryBus, CommandBus commandBus) {
          this.queryBus = queryBus;
          this.commandBus = commandBus;
  }
   @PostMapping(
   value = "objectivos"
  )
  @Operation(
    summary = "Definicao objetivo",
    description = "Definicao objetivo",
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

   public ResponseEntity<SuccessResponseDTO> definicaoObjetivo(@Valid @RequestBody DefinicaoObjectivoDTO definicaoObjetivoRequest
    )
  {

      final var command = new DefinicaoObjetivoCommand(definicaoObjetivoRequest);

      return commandBus.send(command);

  }

   @GetMapping(
   value = "objectivos"
  )
  @Operation(
    summary = "Get lista definicao objectivos",
    description = "Get lista definicao objectivos",
    responses = {
      @ApiResponse(
          responseCode = "200",

          content = @Content(
              mediaType = "application/json",
              schema = @Schema(
                  implementation = WrapperListaDefinicaoObjetivoDTO.class,
                  type = "object")
          )
      )
    }
  )

   public ResponseEntity<WrapperListaDefinicaoObjetivoDTO> getListaDefinicaoObjectivos(
    @RequestParam(value = "ano", required = false) Integer ano,
    @RequestParam(value = "periodicidade", required = false) String periodicidade,
    @RequestParam(value = "abrangencia", required = false) String abrangencia,
    @RequestParam(value = "estado", required = false) String estado,
    @RequestParam(value = "institId", required = false) Long institId,
    @RequestParam(value = "seccaoId", required = false) Long seccaoId,
    @RequestParam(value = "cargoId", required = false) Long cargoId,
    @RequestParam(value = "carreiraId", required = false) Long carreiraId,
    @RequestParam(value = "pageNumber", required = false, defaultValue = "0") String pageNumber,
    @RequestParam(value = "pageSize", required = false, defaultValue = "20") String pageSize)
  {

      final var query = new GetListaDefinicaoObjectivosQuery(ano, periodicidade, abrangencia, estado, institId, seccaoId, cargoId, carreiraId, pageNumber, pageSize);

      return queryBus.handle(query);

  }

   @GetMapping(
  )
  @Operation(
    summary = "Get lista avaliacao",
    description = "Get lista avaliacao",
    responses = {
      @ApiResponse(
          responseCode = "200",

          content = @Content(
              mediaType = "application/json",
              schema = @Schema(
                  implementation = WrapperListaAvaliacaoDTO.class,
                  type = "object")
          )
      )
    }
  )

   public ResponseEntity<WrapperListaAvaliacaoDTO> getListaAvaliacao(
    @RequestParam(value = "pageNumber", required = false, defaultValue = "0") String pageNumber,
    @RequestParam(value = "pageSize", required = false, defaultValue = "20") String pageSize,
    @RequestParam(value = "ano", required = false) Integer ano,
    @RequestParam(value = "direcao", required = false) Long direcao,
    @RequestParam(value = "cargo", required = false) Long cargo,
    @RequestParam(value = "colaborador", required = false) String colaborador,
    @RequestParam(value = "seccaoId", required = false) Long seccaoId,
    @RequestParam(value = "carreiraId", required = false) Long carreiraId,
    @RequestParam(value = "periodicidade", required = false) String periodicidade)
  {

      final var query = new GetListaAvaliacaoQuery(pageNumber, pageSize, ano, direcao, cargo, colaborador, seccaoId, carreiraId, periodicidade);

      return queryBus.handle(query);

  }

   @GetMapping(
   value = "objectivos/{uuid}"
  )
  @Operation(
    summary = "Get definicao objetivo",
    description = "Get definicao objetivo",
    responses = {
      @ApiResponse(
          responseCode = "200",

          content = @Content(
              mediaType = "application/json",
              schema = @Schema(
                  implementation = AvaliacaoDTO.class,
                  type = "object")
          )
      )
    }
  )

   public ResponseEntity<AvaliacaoDTO> getDefinicaoObjetivo(
    @PathVariable(value = "uuid") String uuid)
  {

      final var query = new GetDefinicaoObjetivoQuery(uuid);

      return queryBus.handle(query);

  }

   @GetMapping(
   value = "{uuid}"
  )
  @Operation(
    summary = "Get avaliacao",
    description = "Get avaliacao",
    responses = {
      @ApiResponse(
          responseCode = "200",

          content = @Content(
              mediaType = "application/json",
              schema = @Schema(
                  implementation = AvaliacaoResponseDTO.class,
                  type = "object")
          )
      )
    }
  )

   public ResponseEntity<AvaliacaoResponseDTO> getAvaliacao(
    @PathVariable(value = "uuid") String uuid,
    @RequestParam(value = "periodicidade", required = false) String periodicidade)
  {

      final var query = new GetAvaliacaoQuery(uuid, periodicidade);

      return queryBus.handle(query);

  }

   @PutMapping(
   value = "processos-avaliacao/{uuid}"
  )
  @Operation(
    summary = "Avaliacao",
    description = "Avaliacao",
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

   public ResponseEntity<SuccessResponseDTO> avaliacao(@Valid @RequestBody AvaliacaoDTO avaliacaoRequest
    , @PathVariable(value = "uuid") String uuid)
  {

      final var command = new AvaliacaoCommand(avaliacaoRequest, uuid);

      return commandBus.send(command);

  }

   @PutMapping(
   value = "{uuid}/auto-avaliacao"
  )
  @Operation(
    summary = "Auto avaliacao",
    description = "Auto avaliacao",
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

   public ResponseEntity<SuccessResponseDTO> autoAvaliacao(@Valid @RequestBody AvaliacaoDTO autoAvaliacaoRequest
    , @PathVariable(value = "uuid") String uuid)
  {

      final var command = new AutoAvaliacaoCommand(autoAvaliacaoRequest, uuid);

      return commandBus.send(command);

  }

   @PutMapping(
   value = "processos-avaliacao/{uuid}/observacao-geral"
  )
  @Operation(
    summary = "Processo observacao geral",
    description = "Processo observacao geral",
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

   public ResponseEntity<SuccessResponseDTO> processoObservacaoGeral(@Valid @RequestBody ObservacaoGeralDTO processoObservacaoGeralRequest
    , @PathVariable(value = "uuid") String uuid
    , @RequestParam(value = "periodicidade") String periodicidade)
  {

      final var command = new ProcessoObservacaoGeralCommand(processoObservacaoGeralRequest, uuid, periodicidade);

      return commandBus.send(command);

  }

   @PutMapping(
   value = "processos-avaliacao/{uuid}/parecer-colaborador"
  )
  @Operation(
    summary = "Processo parecer colaborador",
    description = "Processo parecer colaborador",
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

   public ResponseEntity<SuccessResponseDTO> processoParecerColaborador(@Valid @RequestBody ParecerColaboradorDTO processoParecerColaboradorRequest
    , @PathVariable(value = "uuid") String uuid
    , @RequestParam(value = "periodicidade") String periodicidade)
  {

      final var command = new ProcessoParecerColaboradorCommand(processoParecerColaboradorRequest, uuid, periodicidade);

      return commandBus.send(command);

  }

   @PutMapping(
   value = "processos-avaliacao/{uuid}/comissao-executiva"
  )
  @Operation(
    summary = "Processo comissao executiva",
    description = "Processo comissao executiva",
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

   public ResponseEntity<SuccessResponseDTO> processoComissaoExecutiva(@Valid @RequestBody ComissaoExecutivaDTO processoComissaoExecutivaRequest
    , @PathVariable(value = "uuid") String uuid
    , @RequestParam(value = "periodicidade") String periodicidade)
  {

      final var command = new ProcessoComissaoExecutivaCommand(processoComissaoExecutivaRequest, uuid, periodicidade);

      return commandBus.send(command);

  }

   @GetMapping(
   value = "{uuid}/avaliacao-final"
  )
  @Operation(
    summary = "Get avaliacao final",
    description = "Get avaliacao final",
    responses = {
      @ApiResponse(
          responseCode = "200",

          content = @Content(
              mediaType = "application/json",
              schema = @Schema(
                  implementation = AvaliacaoFinalDTO.class,
                  type = "object")
          )
      )
    }
  )

   public ResponseEntity<AvaliacaoFinalDTO> getAvaliacaoFinal(
    @PathVariable(value = "uuid") String uuid)
  {

      final var query = new GetAvaliacaoFinalQuery(uuid);

      return queryBus.handle(query);

  }

}
