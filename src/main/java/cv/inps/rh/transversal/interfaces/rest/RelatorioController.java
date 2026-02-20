/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME */

package cv.inps.rh.transversal.interfaces.rest;

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
import cv.inps.rh.transversal.application.queries.*;

import cv.inps.rh.transversal.application.dto.DossierColaboradorListDTO;
import cv.inps.rh.transversal.application.dto.AssiduidadeListDTO;

@IgrpController
@RestController
@RequestMapping(path = "api/v1/relatorios")
@Tag(
    name = "Transversal",
    description = "Gestão de Relatórios"
)
public class RelatorioController {

  
  private final QueryBus queryBus;

  public RelatorioController(QueryBus queryBus) {
          this.queryBus = queryBus;
          
  }
   @GetMapping(
   value = "funcionarios"
  )
  @Operation(
    summary = "Relatorio dossier colaborador",
    description = "Relatorio dossier colaborador",
    responses = {
      @ApiResponse(
          responseCode = "200",
          
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(
                  implementation = DossierColaboradorListDTO.class,
                  type = "object")
          )
      )
    }
  )
  
  public ResponseEntity<DossierColaboradorListDTO> relatorioDossierColaborador(
    @RequestParam(value = "direccaoId", required = false) Long direccaoId,
    @RequestParam(value = "seccaoId", required = false) Long seccaoId,
    @RequestParam(value = "cargoId", required = false) Long cargoId,
    @RequestParam(value = "idade", required = false) String idade,
    @RequestParam(value = "genero", required = false) String genero,
    @RequestParam(value = "faixaEtaria", required = false) String faixaEtaria,
    @RequestParam(value = "localTrabalhoId", required = false) Long localTrabalhoId,
    @RequestParam(value = "carreiraId", required = false) Long carreiraId,
    @RequestParam(value = "escalaoId", required = false) Long escalaoId,
    @RequestParam(value = "categoriaId", required = false) Long categoriaId,
    @RequestParam(value = "grauEscolaridade", required = false) String grauEscolaridade,
    @RequestParam(value = "mobilidade", required = false) String mobilidade,
    @RequestParam(value = "vinculoId", required = false) Long vinculoId,
    @RequestParam(value = "situacaoLaboralId", required = false) Long situacaoLaboralId,
    @RequestParam(value = "pageNumber", required = false, defaultValue = "0") String pageNumber,
    @RequestParam(value = "pageSize", required = false, defaultValue = "20") String pageSize)
  {

      final var query = new RelatorioDossierColaboradorQuery(direccaoId, seccaoId, cargoId, idade, genero, faixaEtaria, localTrabalhoId, carreiraId, escalaoId, categoriaId, grauEscolaridade, mobilidade, vinculoId, situacaoLaboralId, pageNumber, pageSize);

      return queryBus.handle(query);

  }

   @GetMapping(
   value = "assiduidade"
  )
  @Operation(
    summary = "Relatorio assiduidade",
    description = "Relatorio assiduidade",
    responses = {
      @ApiResponse(
          responseCode = "200",
          
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(
                  implementation = AssiduidadeListDTO.class,
                  type = "object")
          )
      )
    }
  )
  
  public ResponseEntity<AssiduidadeListDTO> relatorioAssiduidade(
    @RequestParam(value = "search", required = false) String search,
    @RequestParam(value = "dataInicio", required = false) String dataInicio,
    @RequestParam(value = "dataFim", required = false) String dataFim,
    @RequestParam(value = "direccaoId", required = false) String direccaoId,
    @RequestParam(value = "seccaoId", required = false) String seccaoId,
    @RequestParam(value = "tipoRelatorio", required = false) String tipoRelatorio,
    @RequestParam(value = "pageNumber", required = false, defaultValue = "0") String pageNumber,
    @RequestParam(value = "pageSize", required = false, defaultValue = "20") String pageSize)
  {

      final var query = new RelatorioAssiduidadeQuery(search, dataInicio, dataFim, direccaoId, seccaoId, tipoRelatorio, pageNumber, pageSize);

      return queryBus.handle(query);

  }

   @GetMapping(
   value = "{id}/download"
  )
  @Operation(
    summary = "Download relatorio",
    description = "Download relatorio",
    responses = {
      @ApiResponse(
          responseCode = "200",
          
          content = @Content(
              mediaType = "application/octet-stream",
              schema = @Schema(
                  implementation = String.class,
                  type = "String")
          )
      )
    }
  )
  
  public ResponseEntity<?> downloadRelatorio(
    @PathVariable(value = "id") String id)
  {

      final var query = new DownloadRelatorioQuery(id);

      return queryBus.handle(query);

  }

}