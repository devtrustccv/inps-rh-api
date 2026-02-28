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
import cv.igrp.framework.core.domain.CommandBus;
import cv.inps.rh.transversal.application.commands.*;
import cv.inps.rh.transversal.application.dto.AssiduidadeListDTO;
import cv.inps.rh.transversal.application.dto.DossierRequestDTO;
import cv.inps.rh.transversal.application.dto.DossierResponseDTO;

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

}
