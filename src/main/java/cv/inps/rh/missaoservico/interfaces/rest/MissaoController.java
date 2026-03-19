/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME */

package cv.inps.rh.missaoservico.interfaces.rest;

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
import cv.inps.rh.missaoservico.application.queries.*;
import cv.igrp.framework.core.domain.CommandBus;
import cv.inps.rh.missaoservico.application.commands.*;
import cv.inps.rh.missaoservico.application.dto.MissaoSubmissaoRequestDTO;
import java.util.Map;
import cv.inps.rh.missaoservico.application.dto.MissaoAnaliseRequestDTO;
import cv.inps.rh.missaoservico.application.dto.WrapperListMissaoServicoDTO;
import cv.inps.rh.missaoservico.application.dto.MissaoServicoResponseDTO;
import cv.inps.rh.missaoservico.application.dto.MissaoCancelarRequestDTO;
import cv.inps.rh.missaoservico.application.dto.MissaoSubmissaoResponseDTO;
import cv.inps.rh.missaoservico.application.dto.MissaoAnaliseResponseDTO;
import cv.inps.rh.missaoservico.application.dto.MissaoEmissaoReqResponseDTO;
import cv.inps.rh.missaoservico.application.dto.MissaoEmissaoRequisicaoRequestDTO;
import cv.inps.rh.missaoservico.application.dto.MissaoLogisticaResponseDTO;
import cv.inps.rh.missaoservico.application.dto.MissaoLogisticaRequestDTO;
import cv.inps.rh.missaoservico.application.dto.MissaoCabimentoResponseDTO;
import cv.inps.rh.missaoservico.application.dto.MissaoCabimentoRequestDTO;
import cv.inps.rh.missaoservico.application.dto.MissaoAutorizacaoResponseDTO;
import cv.inps.rh.missaoservico.application.dto.MissaoAutorizacaoRequestDTO;
import cv.inps.rh.missaoservico.application.dto.MissaoPagamentoResponseDTO;

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
                  implementation = String.class,
                  type = "String")
          )
      )
    }
  )
  
  public ResponseEntity<Map<String, ?>> submeterMissaoServico(@Valid @RequestBody MissaoSubmissaoRequestDTO submeterMissaoServicoRequest
    )
  {

      final var command = new SubmeterMissaoServicoCommand(submeterMissaoServicoRequest);

      return commandBus.send(command);

  }

   @PutMapping(
   value = "{uuid}/analise"
  )
  @Operation(
    summary = "Save analise processo missao servico",
    description = "Save analise processo missao servico",
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
  
  public ResponseEntity<Map<String, ?>> saveAnaliseProcessoMissaoServico(@Valid @RequestBody MissaoAnaliseRequestDTO saveAnaliseProcessoMissaoServicoRequest
    , @PathVariable(value = "uuid") String uuid)
  {

      final var command = new SaveAnaliseProcessoMissaoServicoCommand(saveAnaliseProcessoMissaoServicoRequest, uuid);

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
                  implementation = String.class,
                  type = "String")
          )
      )
    }
  )
  
  public ResponseEntity<String> cancelarMissaoServico(@Valid @RequestBody MissaoCancelarRequestDTO cancelarMissaoServicoRequest
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

   @GetMapping(
   value = "{uuid}/analise"
  )
  @Operation(
    summary = "Get analise processo missao servico",
    description = "Get analise processo missao servico",
    responses = {
      @ApiResponse(
          responseCode = "200",
          
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(
                  implementation = MissaoAnaliseResponseDTO.class,
                  type = "object")
          )
      )
    }
  )
  
  public ResponseEntity<MissaoAnaliseResponseDTO> getAnaliseProcessoMissaoServico(
    @PathVariable(value = "uuid") String uuid)
  {

      final var query = new GetAnaliseProcessoMissaoServicoQuery(uuid);

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
                  implementation = String.class,
                  type = "String")
          )
      )
    }
  )
  
  public ResponseEntity<Map<String, ?>> saveSubmissaoServico(@Valid @RequestBody MissaoSubmissaoRequestDTO saveSubmissaoServicoRequest
    , @PathVariable(value = "uuid") String uuid)
  {

      final var command = new SaveSubmissaoServicoCommand(saveSubmissaoServicoRequest, uuid);

      return commandBus.send(command);

  }

   @GetMapping(
   value = "{uui}/emissao-requisicao"
  )
  @Operation(
    summary = "Get submissao servico emissao requisicao",
    description = "Get submissao servico emissao requisicao",
    responses = {
      @ApiResponse(
          responseCode = "200",
          
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(
                  implementation = MissaoEmissaoReqResponseDTO.class,
                  type = "object")
          )
      )
    }
  )
  
  public ResponseEntity<MissaoEmissaoReqResponseDTO> getSubmissaoServicoEmissaoRequisicao(
    @PathVariable(value = "uui") String uui)
  {

      final var query = new GetSubmissaoServicoEmissaoRequisicaoQuery(uui);

      return queryBus.handle(query);

  }

   @PutMapping(
   value = "{uui}/emissao-requisicao"
  )
  @Operation(
    summary = "Save submissao servico emissao requisicao",
    description = "Save submissao servico emissao requisicao",
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
  
  public ResponseEntity<Map<String, ?>> saveSubmissaoServicoEmissaoRequisicao(@Valid @RequestBody MissaoEmissaoRequisicaoRequestDTO saveSubmissaoServicoEmissaoRequisicaoRequest
    , @PathVariable(value = "uui") String uui)
  {

      final var command = new SaveSubmissaoServicoEmissaoRequisicaoCommand(saveSubmissaoServicoEmissaoRequisicaoRequest, uui);

      return commandBus.send(command);

  }

   @GetMapping(
   value = "{uuid}/logistica"
  )
  @Operation(
    summary = "Get missao servico logistica",
    description = "Get missao servico logistica",
    responses = {
      @ApiResponse(
          responseCode = "200",
          
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(
                  implementation = MissaoLogisticaResponseDTO.class,
                  type = "object")
          )
      )
    }
  )
  
  public ResponseEntity<MissaoLogisticaResponseDTO> getMissaoServicoLogistica(
    @PathVariable(value = "uuid") String uuid)
  {

      final var query = new GetMissaoServicoLogisticaQuery(uuid);

      return queryBus.handle(query);

  }

   @PutMapping(
   value = "{uuid}/logistica"
  )
  @Operation(
    summary = "Save missao servico logistica",
    description = "Save missao servico logistica",
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
  
  public ResponseEntity<Map<String, ?>> saveMissaoServicoLogistica(@Valid @RequestBody MissaoLogisticaRequestDTO saveMissaoServicoLogisticaRequest
    , @PathVariable(value = "uuid") String uuid)
  {

      final var command = new SaveMissaoServicoLogisticaCommand(saveMissaoServicoLogisticaRequest, uuid);

      return commandBus.send(command);

  }

   @GetMapping(
   value = "{uuid}/cabimento"
  )
  @Operation(
    summary = "Get missao servico cabimento",
    description = "Get missao servico cabimento",
    responses = {
      @ApiResponse(
          responseCode = "200",
          
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(
                  implementation = MissaoCabimentoResponseDTO.class,
                  type = "object")
          )
      )
    }
  )
  
  public ResponseEntity<MissaoCabimentoResponseDTO> getMissaoServicoCabimento(
    @PathVariable(value = "uuid") String uuid)
  {

      final var query = new GetMissaoServicoCabimentoQuery(uuid);

      return queryBus.handle(query);

  }

   @PutMapping(
   value = "{uuid}/cabimento"
  )
  @Operation(
    summary = "Save missao servico cabimento",
    description = "Save missao servico cabimento",
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
  
  public ResponseEntity<Map<String, ?>> saveMissaoServicoCabimento(@Valid @RequestBody MissaoCabimentoRequestDTO saveMissaoServicoCabimentoRequest
    , @PathVariable(value = "uuid") String uuid)
  {

      final var command = new SaveMissaoServicoCabimentoCommand(saveMissaoServicoCabimentoRequest, uuid);

      return commandBus.send(command);

  }

   @GetMapping(
   value = "{uuid}/autorizacao"
  )
  @Operation(
    summary = "Get missao servico autorizacao",
    description = "Get missao servico autorizacao",
    responses = {
      @ApiResponse(
          responseCode = "200",
          
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(
                  implementation = MissaoAutorizacaoResponseDTO.class,
                  type = "object")
          )
      )
    }
  )
  
  public ResponseEntity<MissaoAutorizacaoResponseDTO> getMissaoServicoAutorizacao(
    @PathVariable(value = "uuid") String uuid)
  {

      final var query = new GetMissaoServicoAutorizacaoQuery(uuid);

      return queryBus.handle(query);

  }

   @PutMapping(
   value = "{uuid}/autorizacao"
  )
  @Operation(
    summary = "Save missao servico autorizacao",
    description = "Save missao servico autorizacao",
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
  
  public ResponseEntity<Map<String, ?>> saveMissaoServicoAutorizacao(@Valid @RequestBody MissaoAutorizacaoRequestDTO saveMissaoServicoAutorizacaoRequest
    , @PathVariable(value = "uuid") String uuid)
  {

      final var command = new SaveMissaoServicoAutorizacaoCommand(saveMissaoServicoAutorizacaoRequest, uuid);

      return commandBus.send(command);

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

}