/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME */

package cv.inps.rh.transversal.interfaces.rest;

import cv.igrp.framework.core.domain.CommandBus;
import cv.igrp.framework.core.domain.QueryBus;
import cv.igrp.framework.stereotype.IgrpController;
import cv.inps.rh.transversal.application.commands.ObterDossierColaboradorCommand;
import cv.inps.rh.transversal.application.dto.AssiduidadeListDTO;
import cv.inps.rh.transversal.application.dto.DossierRequestDTO;
import cv.inps.rh.transversal.application.dto.DossierResponseDTO;
import cv.inps.rh.transversal.application.queries.ExtrairFichaEfetividadeQuery;
import cv.inps.rh.transversal.application.queries.RelatorioAssiduidadeQuery;
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
@RequestMapping(path = "api/v1/relatorios")
@Tag(
    name = "Transversal",
    description = "Gestão de Relatórios"
)
public class RelatorioController {


  private final QueryBus queryBus;
  private final CommandBus commandBus;

  public RelatorioController(QueryBus queryBus, CommandBus commandBus) {
          this.queryBus = queryBus;
          this.commandBus = commandBus;
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
    @RequestParam(value = "direccaoId", required = false) Long direccaoId,
    @RequestParam(value = "seccaoId", required = false) Long seccaoId,
    @RequestParam(value = "colaborador", required = false) String colaborador,
    @RequestParam(value = "tipoAssiduidade") String tipoAssiduidade,
    @RequestParam(value = "dataInicio", required = false) String dataInicio,
    @RequestParam(value = "dataFim", required = false) String dataFim,
    @RequestParam(value = "pageNumber", required = false, defaultValue = "0") String pageNumber,
    @RequestParam(value = "pageSize", required = false, defaultValue = "20") String pageSize)
  {

      final var query = new RelatorioAssiduidadeQuery(direccaoId, seccaoId, colaborador, tipoAssiduidade, dataInicio, dataFim, pageNumber, pageSize);

      return queryBus.handle(query);

  }

   @PostMapping(
   value = "funcionarios"
  )
  @Operation(
    summary = "Obter dossier colaborador",
    description = "Obter dossier colaborador",
    responses = {
      @ApiResponse(
          responseCode = "200",

          content = @Content(
              mediaType = "application/json",
              schema = @Schema(
                  implementation = DossierResponseDTO.class,
                  type = "object")
          )
      )
    }
  )

  public ResponseEntity<DossierResponseDTO> obterDossierColaborador(@Valid @RequestBody DossierRequestDTO obterDossierColaboradorRequest
    )
  {

      final var command = new ObterDossierColaboradorCommand(obterDossierColaboradorRequest);

      return commandBus.send(command);

  }

   @GetMapping(
   value = "assiduidade/ficha-efectividade"
  )
  @Operation(
    summary = "Extrair ficha efetividade",
    description = "Extrair ficha efetividade",
    responses = {
      @ApiResponse(
          responseCode = "200",

          content = @Content(
              mediaType = "application/pdf",
              schema = @Schema(
                  implementation = byte[].class,
                  type = "byte[]")
          )
      )
    }
  )

  public ResponseEntity<?> extrairFichaEfetividade(
    @RequestParam(value = "ano", required = false) Integer ano,
    @RequestParam(value = "mes", required = false) Integer mes)
  {

      final var query = new ExtrairFichaEfetividadeQuery(ano, mes);

      return queryBus.handle(query);

  }

}
