/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME */

package cv.inps.rh.assiduidade.interfaces.rest;

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
import cv.inps.rh.assiduidade.application.queries.*;
import cv.igrp.framework.core.domain.CommandBus;
import cv.inps.rh.assiduidade.application.commands.*;
import cv.inps.rh.assiduidade.application.dto.WrapperListaPicagemDTO;
import cv.inps.rh.assiduidade.application.dto.WrapperListaAssiduidadadeDTO;
import cv.inps.rh.assiduidade.application.dto.FaltaReqDTO;
import java.util.Map;
import cv.inps.rh.assiduidade.application.dto.WrapperListaFaltaDTO;
import cv.inps.rh.assiduidade.application.dto.WrapperListaDispensaDTO;

@IgrpController
@RestController
@RequestMapping(path = "api/v1/assiduidade")
@Tag(
    name = "Assiduidade",
    description = "gestao de assiduidade"
)
public class AssiduidadeController {

  
  private final QueryBus queryBus;
  private final CommandBus commandBus;

  public AssiduidadeController(QueryBus queryBus, CommandBus commandBus) {
          this.queryBus = queryBus;
          this.commandBus = commandBus;
  }
   @GetMapping(
   value = "picagens"
  )
  @Operation(
    summary = "Get lista picagem",
    description = "Get lista picagem",
    responses = {
      @ApiResponse(
          responseCode = "200",
          
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(
                  implementation = WrapperListaPicagemDTO.class,
                  type = "object")
          )
      )
    }
  )
  
  public ResponseEntity<WrapperListaPicagemDTO> getListaPicagem(
    @RequestParam(value = "pageSize", required = false, defaultValue = "20") String pageSize,
    @RequestParam(value = "pageNumber", required = false, defaultValue = "0") String pageNumber,
    @RequestParam(value = "nomeColaborador", required = false) String nomeColaborador,
    @RequestParam(value = "direcao", required = false) Long direcao,
    @RequestParam(value = "seccao", required = false) Long seccao,
    @RequestParam(value = "ups", required = false) Long ups,
    @RequestParam(value = "dataInicio", required = false) String dataInicio,
    @RequestParam(value = "dataFim", required = false) String dataFim)
  {

      final var query = new GetListaPicagemQuery(pageSize, pageNumber, nomeColaborador, direcao, seccao, ups, dataInicio, dataFim);

      return queryBus.handle(query);

  }

   @GetMapping(
   value = "movimento-resumos"
  )
  @Operation(
    summary = "Get lista movimentos resumidos",
    description = "Get lista movimentos resumidos",
    responses = {
      @ApiResponse(
          responseCode = "200",
          
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(
                  implementation = WrapperListaAssiduidadadeDTO.class,
                  type = "object")
          )
      )
    }
  )
  
  public ResponseEntity<WrapperListaAssiduidadadeDTO> getListaMovimentosResumidos(
    @RequestParam(value = "pageSize", required = false, defaultValue = "20") String pageSize,
    @RequestParam(value = "pageNumber", required = false, defaultValue = "0") String pageNumber,
    @RequestParam(value = "colaborador", required = false) String colaborador,
    @RequestParam(value = "dataInicio", required = false) String dataInicio,
    @RequestParam(value = "dataFim", required = false) String dataFim,
    @RequestParam(value = "estado", required = false) String estado,
    @RequestParam(value = "ilha", required = false) Long ilha,
    @RequestParam(value = "direcao", required = false) Long direcao,
    @RequestParam(value = "seccao", required = false) Long seccao)
  {

      final var query = new GetListaMovimentosResumidosQuery(pageSize, pageNumber, colaborador, dataInicio, dataFim, estado, ilha, direcao, seccao);

      return queryBus.handle(query);

  }

   @PostMapping(
   value = "falta"
  )
  @Operation(
    summary = "Marcar falta",
    description = "Marcar falta",
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
  
  public ResponseEntity<Map<String, ?>> marcarFalta(@Valid @RequestBody FaltaReqDTO marcarFaltaRequest
    )
  {

      final var command = new MarcarFaltaCommand(marcarFaltaRequest);

      return commandBus.send(command);

  }

   @PostMapping(
   value = "falta/{faltaId}"
  )
  @Operation(
    summary = "Validar falta",
    description = "Validar falta",
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
  
  public ResponseEntity<Map<String, ?>> validarFalta(@Valid @RequestBody FaltaReqDTO validarFaltaRequest
    , @PathVariable(value = "faltaId") String faltaId)
  {

      final var command = new ValidarFaltaCommand(validarFaltaRequest, faltaId);

      return commandBus.send(command);

  }

   @GetMapping(
   value = "falta"
  )
  @Operation(
    summary = "Get lista falta",
    description = "Get lista falta",
    responses = {
      @ApiResponse(
          responseCode = "200",
          
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(
                  implementation = WrapperListaFaltaDTO.class,
                  type = "object")
          )
      )
    }
  )
  
  public ResponseEntity<WrapperListaFaltaDTO> getListaFalta(
    @RequestParam(value = "pageNumber", required = false, defaultValue = "0") String pageNumber,
    @RequestParam(value = "pageSize", required = false, defaultValue = "20") String pageSize,
    @RequestParam(value = "colaborador", required = false) String colaborador,
    @RequestParam(value = "ilha", required = false) Long ilha,
    @RequestParam(value = "direcao", required = false) Long direcao,
    @RequestParam(value = "seccao", required = false) Long seccao,
    @RequestParam(value = "dataInicio", required = false) String dataInicio,
    @RequestParam(value = "dataFim", required = false) String dataFim,
    @RequestParam(value = "estado", required = false) String estado)
  {

      final var query = new GetListaFaltaQuery(pageNumber, pageSize, colaborador, ilha, direcao, seccao, dataInicio, dataFim, estado);

      return queryBus.handle(query);

  }

   @GetMapping(
   value = "dispensa"
  )
  @Operation(
    summary = "Get lista dispensa",
    description = "Get lista dispensa",
    responses = {
      @ApiResponse(
          responseCode = "200",
          
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(
                  implementation = WrapperListaDispensaDTO.class,
                  type = "object")
          )
      )
    }
  )
  
  public ResponseEntity<WrapperListaDispensaDTO> getListaDispensa(
    @RequestParam(value = "pageNumber", required = false, defaultValue = "0") String pageNumber,
    @RequestParam(value = "pageSize", required = false, defaultValue = "20") String pageSize,
    @RequestParam(value = "colaborador", required = false) String colaborador,
    @RequestParam(value = "ilha", required = false) Long ilha,
    @RequestParam(value = "direcao", required = false) Long direcao,
    @RequestParam(value = "seccao", required = false) Long seccao,
    @RequestParam(value = "dataInicio", required = false) String dataInicio,
    @RequestParam(value = "dataFim", required = false) String dataFim,
    @RequestParam(value = "estado", required = false) String estado)
  {

      final var query = new GetListaDispensaQuery(pageNumber, pageSize, colaborador, ilha, direcao, seccao, dataInicio, dataFim, estado);

      return queryBus.handle(query);

  }

}