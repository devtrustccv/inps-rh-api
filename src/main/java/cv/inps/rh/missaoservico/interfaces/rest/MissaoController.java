/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME */

package cv.inps.rh.missaoservico.interfaces.rest;

import cv.inps.rh.shared.application.dto.SuccessResponseDTO;
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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@IgrpController
@RestController
@RequestMapping(path = "api/v1/missao-servico")
@Tag(
    name = "Missaoservico",
    description = "gestao missao servico"
)
public class MissaoController {


  private final QueryBus queryBus;
  private final CommandBus commandBus;

  public MissaoController(QueryBus queryBus, CommandBus commandBus) {
          this.queryBus = queryBus;
          this.commandBus = commandBus;
  }
   @PostMapping(
   value = "submissao"
  )
  @Operation(
    summary = "Submeter missao servico",
    description = "Submeter missao servico",
    responses = {
      @ApiResponse(
          responseCode = "200",

          content = @Content(
              mediaType = "application/json",
              schema = @Schema(
                  implementation = MissaoSubmissaoGravadaResponseDTO.class,
                  type = "object")
          )
      )
    }
  )

   public ResponseEntity<MissaoSubmissaoGravadaResponseDTO> submeterMissaoServico(@Valid @RequestBody MissaoSubmissaoRequestDTO submeterMissaoServicoRequest
    )
  {

      final var command = new SubmeterMissaoServicoCommand(submeterMissaoServicoRequest);

      return commandBus.send(command);

  }

   @GetMapping(
  )
  @Operation(
    summary = "Get lista missao servico",
    description = "Get lista missao servico",
    responses = {
      @ApiResponse(
          responseCode = "200",

          content = @Content(
              mediaType = "application/json",
              schema = @Schema(
                  implementation = WrapperListMissaoServicoDTO.class,
                  type = "object")
          )
      )
    }
  )

   public ResponseEntity<WrapperListMissaoServicoDTO> getListaMissaoServico(
    @RequestParam(value = "nrMissao", required = false) String nrMissao,
    @RequestParam(value = "periodoDe", required = false) String periodoDe,
    @RequestParam(value = "periodoAte", required = false) String periodoAte,
    @RequestParam(value = "pageNumber", required = false, defaultValue = "0") String pageNumber,
    @RequestParam(value = "pageSize", required = false) String pageSize)
  {

      final var query = new GetListaMissaoServicoQuery(nrMissao, periodoDe, periodoAte, pageNumber, pageSize);

      return queryBus.handle(query);

  }

   @GetMapping(
   value = "{uuid}"
  )
  @Operation(
    summary = "Get detalhe missao servico",
    description = "Get detalhe missao servico",
    responses = {
      @ApiResponse(
          responseCode = "200",

          content = @Content(
              mediaType = "application/json",
              schema = @Schema(
                  implementation = MissaoServicoResponseDTO.class,
                  type = "object")
          )
      )
    }
  )

   public ResponseEntity<MissaoServicoResponseDTO> getDetalheMissaoServico(
    @PathVariable(value = "uuid") String uuid)
  {

      final var query = new GetDetalheMissaoServicoQuery(uuid);

      return queryBus.handle(query);

  }

   @PatchMapping(
   value = "{id}/cancelar"
  )
  @Operation(
    summary = "Cancelar missao servico",
    description = "Cancelar missao servico",
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

   public ResponseEntity<SuccessResponseDTO> cancelarMissaoServico(@Valid @RequestBody MissaoCancelarRequestDTO cancelarMissaoServicoRequest
    , @PathVariable(value = "id") String id)
  {

      final var command = new CancelarMissaoServicoCommand(cancelarMissaoServicoRequest, id);

      return commandBus.send(command);

  }

   @GetMapping(
   value = "{uuid}/submissao"
  )
  @Operation(
    summary = "Get submissao servico process",
    description = "Get submissao servico process",
    responses = {
      @ApiResponse(
          responseCode = "200",

          content = @Content(
              mediaType = "application/json",
              schema = @Schema(
                  implementation = MissaoSubmissaoResponseDTO.class,
                  type = "object")
          )
      )
    }
  )

   public ResponseEntity<MissaoSubmissaoResponseDTO> getSubmissaoServicoProcess(
    @PathVariable(value = "uuid") String uuid)
  {

      final var query = new GetSubmissaoServicoProcessQuery(uuid);

      return queryBus.handle(query);

  }

   @PutMapping(
   value = "{uuid}/submissao"
  )
  @Operation(
    summary = "Save submissao servico",
    description = "Save submissao servico",
    responses = {
      @ApiResponse(
          responseCode = "200",

          content = @Content(
              mediaType = "application/json",
              schema = @Schema(
                  implementation = MissaoSubmissaoGravadaResponseDTO.class,
                  type = "object")
          )
      )
    }
  )

   public ResponseEntity<MissaoSubmissaoGravadaResponseDTO> saveSubmissaoServico(@Valid @RequestBody MissaoSubmissaoRequestDTO saveSubmissaoServicoRequest
    , @PathVariable(value = "uuid") String uuid)
  {

      final var command = new SaveSubmissaoServicoCommand(saveSubmissaoServicoRequest, uuid);

      return commandBus.send(command);

  }

   @GetMapping(
   value = "{uuid}/cabimentos"
  )
  @Operation(
    summary = "Get cabimentos da missao",
    description = "Cabimentos dos quatro processos numa so resposta (consulta). Cabimentar continua em /processos/{tipoProcesso}/cabimento",
    responses = {
      @ApiResponse(
          responseCode = "200",

          content = @Content(
              mediaType = "application/json",
              schema = @Schema(
                  implementation = MissaoCabimentosResponseDTO.class,
                  type = "object")
          )
      )
    }
  )

   public ResponseEntity<MissaoCabimentosResponseDTO> getMissaoCabimentos(
    @PathVariable(value = "uuid") String uuid)
  {

      final var query = new GetMissaoCabimentosQuery(uuid);

      return queryBus.handle(query);

  }

   @GetMapping(
   value = "{uuid}/pagamento"
  )
  @Operation(
    summary = "Get missao servico pagamento",
    description = "Get missao servico pagamento",
    responses = {
      @ApiResponse(
          responseCode = "200",

          content = @Content(
              mediaType = "application/json",
              schema = @Schema(
                  implementation = MissaoPagamentoResponseDTO.class,
                  type = "object")
          )
      )
    }
  )

   public ResponseEntity<MissaoPagamentoResponseDTO> getMissaoServicoPagamento(
    @PathVariable(value = "uuid") String uuid)
  {

      final var query = new GetMissaoServicoPagamentoQuery(uuid);

      return queryBus.handle(query);

  }

   @PutMapping(
   value = "{uuid}/pagamento"
  )
  @Operation(
    summary = "Save missao servico pagamento",
    description = "Save missao servico pagamento",
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

   public ResponseEntity<SuccessResponseDTO> saveMissaoServicoPagamento(@Valid @RequestBody MissaoPagamentoRequestDTO saveMissaoServicoPagamentoRequest
    , @PathVariable(value = "uuid") String uuid)
  {

      final var command = new SaveMissaoServicoPagamentoCommand(saveMissaoServicoPagamentoRequest, uuid);

      return commandBus.send(command);

  }

}
