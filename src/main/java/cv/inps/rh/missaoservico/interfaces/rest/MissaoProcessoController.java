/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME */

package cv.inps.rh.missaoservico.interfaces.rest;

import cv.igrp.framework.core.domain.CommandBus;
import cv.igrp.framework.core.domain.QueryBus;
import cv.igrp.framework.stereotype.IgrpController;
import cv.inps.rh.missaoservico.application.commands.*;
import cv.inps.rh.missaoservico.application.dto.*;
import cv.inps.rh.missaoservico.application.queries.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import cv.inps.rh.missaoservico.application.dto.NotificacaoMissaoResponseDTO;
import cv.inps.rh.missaoservico.application.queries.GetNotificacoesMissaoQuery;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@IgrpController
@RestController
@RequestMapping(path = "api/v1/missao-servico")
@Tag(
    name = "MissaoProcesso",
    description = "etapas dos processos da missao servico"
)
public class MissaoProcessoController {


  private final QueryBus queryBus;
  private final CommandBus commandBus;

  public MissaoProcessoController(QueryBus queryBus, CommandBus commandBus) {
          this.queryBus = queryBus;
          this.commandBus = commandBus;
  }
   @GetMapping(
   value = "{uuid}/processos/{tipoProcesso}/prestadores"
  )
  @Operation(
    summary = "Get prestadores do processo",
    description = "Get prestadores do processo",
    responses = {
      @ApiResponse(
          responseCode = "200",

          content = @Content(
              mediaType = "application/json",
              schema = @Schema(
                  implementation = ProcessoPrestadoresResponseDTO.class,
                  type = "object")
          )
      )
    }
  )

   public ResponseEntity<ProcessoPrestadoresResponseDTO> getProcessoPrestadores(
    @PathVariable(value = "uuid") String uuid,
    @PathVariable(value = "tipoProcesso") String tipoProcesso)
  {

      final var query = new GetProcessoPrestadoresQuery(uuid, tipoProcesso);

      return queryBus.handle(query);

  }

   @PutMapping(
   value = "{uuid}/processos/{tipoProcesso}/prestadores"
  )
  @Operation(
    summary = "Save prestadores do processo",
    description = "Save prestadores do processo",
    responses = {
      @ApiResponse(
          responseCode = "200",

          content = @Content(
              mediaType = "application/json",
              schema = @Schema(
                  implementation = ProcessoEtapaGravadaResponseDTO.class,
                  type = "object")
          )
      )
    }
  )

   public ResponseEntity<ProcessoEtapaGravadaResponseDTO> saveProcessoPrestadores(@Valid @RequestBody ProcessoPrestadoresRequestDTO saveProcessoPrestadoresRequest
    , @PathVariable(value = "uuid") String uuid, @PathVariable(value = "tipoProcesso") String tipoProcesso)
  {

      final var command = new SaveProcessoPrestadoresCommand(saveProcessoPrestadoresRequest, uuid, tipoProcesso);

      return commandBus.send(command);

  }

   @GetMapping(
   value = "{uuid}/processos/{tipoProcesso}/requisicoes"
  )
  @Operation(
    summary = "Get requisicoes do processo",
    description = "Get requisicoes do processo",
    responses = {
      @ApiResponse(
          responseCode = "200",

          content = @Content(
              mediaType = "application/json",
              schema = @Schema(
                  implementation = ProcessoRequisicoesResponseDTO.class,
                  type = "object")
          )
      )
    }
  )

   public ResponseEntity<ProcessoRequisicoesResponseDTO> getProcessoRequisicoes(
    @PathVariable(value = "uuid") String uuid,
    @PathVariable(value = "tipoProcesso") String tipoProcesso)
  {

      final var query = new GetProcessoRequisicoesQuery(uuid, tipoProcesso);

      return queryBus.handle(query);

  }

   @PutMapping(
   value = "{uuid}/processos/{tipoProcesso}/requisicoes"
  )
  @Operation(
    summary = "Save requisicoes do processo",
    description = "Save requisicoes do processo",
    responses = {
      @ApiResponse(
          responseCode = "200",

          content = @Content(
              mediaType = "application/json",
              schema = @Schema(
                  implementation = ProcessoEtapaGravadaResponseDTO.class,
                  type = "object")
          )
      )
    }
  )

   public ResponseEntity<ProcessoEtapaGravadaResponseDTO> saveProcessoRequisicoes(@Valid @RequestBody ProcessoRequisicoesRequestDTO saveProcessoRequisicoesRequest
    , @PathVariable(value = "uuid") String uuid, @PathVariable(value = "tipoProcesso") String tipoProcesso)
  {

      final var command = new SaveProcessoRequisicoesCommand(saveProcessoRequisicoesRequest, uuid, tipoProcesso);

      return commandBus.send(command);

  }

   @GetMapping(
   value = "{uuid}/processos/{tipoProcesso}/requisicoes/{requisicaoUuid}/pdf"
  )
  @Operation(
    summary = "Extrair requisicao em PDF",
    description = "Extrair requisicao em PDF",
    responses = {
      @ApiResponse(
          responseCode = "200",

          content = @Content(
              mediaType = "application/pdf",
              schema = @Schema(
                  type = "string",
                  format = "binary")
          )
      )
    }
  )

   public ResponseEntity<byte[]> getRequisicaoPdf(
    @PathVariable(value = "uuid") String uuid,
    @PathVariable(value = "tipoProcesso") String tipoProcesso,
    @PathVariable(value = "requisicaoUuid") String requisicaoUuid)
  {

      final var query = new GetRequisicaoPdfQuery(uuid, tipoProcesso, requisicaoUuid);

      return queryBus.handle(query);

  }

   @GetMapping(
   value = "{uuid}/processos/{tipoProcesso}/logistica"
  )
  @Operation(
    summary = "Get logistica do processo",
    description = "Get logistica do processo",
    responses = {
      @ApiResponse(
          responseCode = "200",

          content = @Content(
              mediaType = "application/json",
              schema = @Schema(
                  implementation = ProcessoLogisticaResponseDTO.class,
                  type = "object")
          )
      )
    }
  )

   public ResponseEntity<ProcessoLogisticaResponseDTO> getProcessoLogistica(
    @PathVariable(value = "uuid") String uuid,
    @PathVariable(value = "tipoProcesso") String tipoProcesso)
  {

      final var query = new GetProcessoLogisticaQuery(uuid, tipoProcesso);

      return queryBus.handle(query);

  }

   @PutMapping(
   value = "{uuid}/processos/{tipoProcesso}/logistica"
  )
  @Operation(
    summary = "Save logistica do processo",
    description = "Save logistica do processo",
    responses = {
      @ApiResponse(
          responseCode = "200",

          content = @Content(
              mediaType = "application/json",
              schema = @Schema(
                  implementation = ProcessoEtapaGravadaResponseDTO.class,
                  type = "object")
          )
      )
    }
  )

   public ResponseEntity<ProcessoEtapaGravadaResponseDTO> saveProcessoLogistica(@Valid @RequestBody ProcessoLogisticaRequestDTO saveProcessoLogisticaRequest
    , @PathVariable(value = "uuid") String uuid, @PathVariable(value = "tipoProcesso") String tipoProcesso)
  {

      final var command = new SaveProcessoLogisticaCommand(saveProcessoLogisticaRequest, uuid, tipoProcesso);

      return commandBus.send(command);

  }

   @GetMapping(
   value = "{uuid}/processos/{tipoProcesso}/validacao-ugal"
  )
  @Operation(
    summary = "Get validacao UGAL do processo",
    description = "Get validacao UGAL do processo",
    responses = {
      @ApiResponse(
          responseCode = "200",

          content = @Content(
              mediaType = "application/json",
              schema = @Schema(
                  implementation = ProcessoValidacaoUgalResponseDTO.class,
                  type = "object")
          )
      )
    }
  )

   public ResponseEntity<ProcessoValidacaoUgalResponseDTO> getProcessoValidacaoUgal(
    @PathVariable(value = "uuid") String uuid,
    @PathVariable(value = "tipoProcesso") String tipoProcesso)
  {

      final var query = new GetProcessoValidacaoUgalQuery(uuid, tipoProcesso);

      return queryBus.handle(query);

  }

   @PutMapping(
   value = "{uuid}/processos/{tipoProcesso}/validacao-ugal"
  )
  @Operation(
    summary = "Save parecer UGAL do processo",
    description = "Save parecer UGAL do processo",
    responses = {
      @ApiResponse(
          responseCode = "200",

          content = @Content(
              mediaType = "application/json",
              schema = @Schema(
                  implementation = ProcessoEtapaGravadaResponseDTO.class,
                  type = "object")
          )
      )
    }
  )

   public ResponseEntity<ProcessoEtapaGravadaResponseDTO> saveProcessoValidacaoUgal(@Valid @RequestBody ParecerRequestDTO saveProcessoValidacaoUgalRequest
    , @PathVariable(value = "uuid") String uuid, @PathVariable(value = "tipoProcesso") String tipoProcesso)
  {

      final var command = new SaveProcessoParecerCommand(saveProcessoValidacaoUgalRequest, uuid, tipoProcesso, "VALIDACAO_UGAL");

      return commandBus.send(command);

  }

   @GetMapping(
   value = "{uuid}/processos/{tipoProcesso}/aprovacao-rh"
  )
  @Operation(
    summary = "Get aprovacao RH do processo",
    description = "Get aprovacao RH do processo",
    responses = {
      @ApiResponse(
          responseCode = "200",

          content = @Content(
              mediaType = "application/json",
              schema = @Schema(
                  implementation = ProcessoAprovacaoRhResponseDTO.class,
                  type = "object")
          )
      )
    }
  )

   public ResponseEntity<ProcessoAprovacaoRhResponseDTO> getProcessoAprovacaoRh(
    @PathVariable(value = "uuid") String uuid,
    @PathVariable(value = "tipoProcesso") String tipoProcesso)
  {

      final var query = new GetProcessoAprovacaoRhQuery(uuid, tipoProcesso);

      return queryBus.handle(query);

  }

   @PutMapping(
   value = "{uuid}/processos/{tipoProcesso}/aprovacao-rh"
  )
  @Operation(
    summary = "Save parecer da aprovacao RH do processo",
    description = "Save parecer da aprovacao RH do processo",
    responses = {
      @ApiResponse(
          responseCode = "200",

          content = @Content(
              mediaType = "application/json",
              schema = @Schema(
                  implementation = ProcessoEtapaGravadaResponseDTO.class,
                  type = "object")
          )
      )
    }
  )

   public ResponseEntity<ProcessoEtapaGravadaResponseDTO> saveProcessoAprovacaoRh(@Valid @RequestBody ParecerRequestDTO saveProcessoAprovacaoRhRequest
    , @PathVariable(value = "uuid") String uuid, @PathVariable(value = "tipoProcesso") String tipoProcesso)
  {

      final var command = new SaveProcessoParecerCommand(saveProcessoAprovacaoRhRequest, uuid, tipoProcesso, "APROVACAO_RH");

      return commandBus.send(command);

  }

   @GetMapping(
   value = "{uuid}/processos/{tipoProcesso}/prestadores/{missaoPrestUuid}/avaliacao"
  )
  @Operation(
    summary = "Get avaliacao do prestador",
    description = "Get avaliacao do prestador",
    responses = {
      @ApiResponse(
          responseCode = "200",

          content = @Content(
              mediaType = "application/json",
              schema = @Schema(
                  implementation = AvaliacaoPrestadorResponseDTO.class,
                  type = "object")
          )
      )
    }
  )

   public ResponseEntity<AvaliacaoPrestadorResponseDTO> getAvaliacaoPrestador(
    @PathVariable(value = "uuid") String uuid,
    @PathVariable(value = "tipoProcesso") String tipoProcesso,
    @PathVariable(value = "missaoPrestUuid") String missaoPrestUuid)
  {

      final var query = new GetAvaliacaoPrestadorQuery(uuid, tipoProcesso, missaoPrestUuid);

      return queryBus.handle(query);

  }

   @PutMapping(
   value = "{uuid}/processos/{tipoProcesso}/prestadores/{missaoPrestUuid}/avaliacao"
  )
  @Operation(
    summary = "Save avaliacao do prestador",
    description = "Save avaliacao do prestador",
    responses = {
      @ApiResponse(
          responseCode = "200",

          content = @Content(
              mediaType = "application/json",
              schema = @Schema(
                  implementation = AvaliacaoPrestadorGravadaResponseDTO.class,
                  type = "object")
          )
      )
    }
  )

   public ResponseEntity<AvaliacaoPrestadorGravadaResponseDTO> saveAvaliacaoPrestador(@Valid @RequestBody AvaliacaoPrestadorRequestDTO saveAvaliacaoPrestadorRequest
    , @PathVariable(value = "uuid") String uuid, @PathVariable(value = "tipoProcesso") String tipoProcesso,
    @PathVariable(value = "missaoPrestUuid") String missaoPrestUuid)
  {

      final var command = new SaveAvaliacaoPrestadorCommand(saveAvaliacaoPrestadorRequest, uuid, tipoProcesso, missaoPrestUuid);

      return commandBus.send(command);

  }

   @GetMapping(
   value = "{uuid}/processos/{tipoProcesso}/cabimento"
  )
  @Operation(
    summary = "Get cabimento do processo",
    description = "Get cabimento do processo",
    responses = {
      @ApiResponse(
          responseCode = "200",

          content = @Content(
              mediaType = "application/json",
              schema = @Schema(
                  implementation = ProcessoCabimentoResponseDTO.class,
                  type = "object")
          )
      )
    }
  )

   public ResponseEntity<ProcessoCabimentoResponseDTO> getProcessoCabimento(
    @PathVariable(value = "uuid") String uuid,
    @PathVariable(value = "tipoProcesso") String tipoProcesso)
  {

      final var query = new GetProcessoCabimentoQuery(uuid, tipoProcesso);

      return queryBus.handle(query);

  }

   @PutMapping(
   value = "{uuid}/processos/{tipoProcesso}/cabimento"
  )
  @Operation(
    summary = "Save cabimento do processo",
    description = "Save cabimento do processo",
    responses = {
      @ApiResponse(
          responseCode = "200",

          content = @Content(
              mediaType = "application/json",
              schema = @Schema(
                  implementation = ProcessoEtapaGravadaResponseDTO.class,
                  type = "object")
          )
      )
    }
  )

   public ResponseEntity<ProcessoEtapaGravadaResponseDTO> saveProcessoCabimento(@Valid @RequestBody ProcessoCabimentoRequestDTO saveProcessoCabimentoRequest
    , @PathVariable(value = "uuid") String uuid, @PathVariable(value = "tipoProcesso") String tipoProcesso)
  {

      final var command = new SaveProcessoCabimentoCommand(saveProcessoCabimentoRequest, uuid, tipoProcesso);

      return commandBus.send(command);

  }

   @GetMapping(
   value = "{uuid}/processos/{tipoProcesso}/autorizacao"
  )
  @Operation(
    summary = "Get autorizacao do processo",
    description = "Get autorizacao do processo",
    responses = {
      @ApiResponse(
          responseCode = "200",

          content = @Content(
              mediaType = "application/json",
              schema = @Schema(
                  implementation = ProcessoCabimentoResponseDTO.class,
                  type = "object")
          )
      )
    }
  )

   public ResponseEntity<ProcessoCabimentoResponseDTO> getProcessoAutorizacao(
    @PathVariable(value = "uuid") String uuid,
    @PathVariable(value = "tipoProcesso") String tipoProcesso)
  {

      final var query = new GetProcessoCabimentoQuery(uuid, tipoProcesso);

      return queryBus.handle(query);

  }

   @PutMapping(
   value = "{uuid}/processos/{tipoProcesso}/autorizacao"
  )
  @Operation(
    summary = "Save autorizacao do processo",
    description = "Save autorizacao do processo",
    responses = {
      @ApiResponse(
          responseCode = "200",

          content = @Content(
              mediaType = "application/json",
              schema = @Schema(
                  implementation = ProcessoEtapaGravadaResponseDTO.class,
                  type = "object")
          )
      )
    }
  )

   public ResponseEntity<ProcessoEtapaGravadaResponseDTO> saveProcessoAutorizacao(@Valid @RequestBody ProcessoEtapaActionRequestDTO saveProcessoAutorizacaoRequest
    , @PathVariable(value = "uuid") String uuid, @PathVariable(value = "tipoProcesso") String tipoProcesso)
  {

      final var command = new SaveProcessoAutorizacaoCommand(saveProcessoAutorizacaoRequest, uuid, tipoProcesso);

      return commandBus.send(command);

  }

   @GetMapping(
   value = "processos"
  )
  @Operation(
    summary = "Get lista de processos por etapa",
    description = "Get lista de processos por etapa",
    responses = {
      @ApiResponse(
          responseCode = "200",

          content = @Content(
              mediaType = "application/json",
              schema = @Schema(
                  implementation = WrapperListProcessoEtapaDTO.class,
                  type = "object")
          )
      )
    }
  )

   public ResponseEntity<WrapperListProcessoEtapaDTO> getListaProcessosEtapa(
    @RequestParam(value = "etapa", required = false) String etapa,
    @RequestParam(value = "tipoProcesso", required = false) String tipoProcesso,
    @RequestParam(value = "pageNumber", required = false, defaultValue = "0") String pageNumber,
    @RequestParam(value = "pageSize", required = false, defaultValue = "10") String pageSize)
  {

      final var query = new GetListaProcessosEtapaQuery(etapa, tipoProcesso, pageNumber, pageSize);

      return queryBus.handle(query);

  }


   @GetMapping(
   value = "{uuid}/notificacoes"
  )
  @Operation(
    summary = "Get notificacoes da missao",
    description = "Todas as notificacoes emitidas no ambito da missao (ecra Ver Notificacao)",
    responses = {
      @ApiResponse(
          responseCode = "200",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(
                  implementation = NotificacaoMissaoResponseDTO.class,
                  type = "array")
          )
      )
    }
  )
   public ResponseEntity<List<NotificacaoMissaoResponseDTO>> getNotificacoesMissao(
    @PathVariable("uuid") String uuid)
  {

      final var query = new GetNotificacoesMissaoQuery(uuid);

      return queryBus.handle(query);

  }

}
