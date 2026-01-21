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

import cv.inps.rh.assiduidade.application.dto.WrapperListaPicagemDTO;
import cv.inps.rh.assiduidade.application.dto.WrapperListaAssiduidadadeDTO;

@IgrpController
@RestController
@RequestMapping(path = "api/v1/assiduidade")
@Tag(
    name = "Assiduidade",
    description = "gestao de assiduidade"
)
public class AssiduidadeController {

  
  private final QueryBus queryBus;

  public AssiduidadeController(QueryBus queryBus) {
          this.queryBus = queryBus;
          
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

}