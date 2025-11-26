/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME. */

package cv.inps.rh.funcionario.interfaces.rest;

import cv.igrp.framework.core.domain.QueryBus;
import cv.igrp.framework.stereotype.IgrpController;
import cv.inps.rh.funcionario.application.dto.WrapperHistLaboralResponseDTO;
import cv.inps.rh.funcionario.application.queries.GetHistoricoLaboralQuery;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@IgrpController
@RestController
@RequestMapping(path = "historico-laboral")
@Tag(name = "HistoricoLaboral", description = "Gestão de Histórico Laboral")
public class HistoricoLaboralController {


  private final QueryBus queryBus;

  public HistoricoLaboralController(QueryBus queryBus) {
          this.queryBus = queryBus;

  }
   @GetMapping(
   value = "{funcionarioId}"
  )
  @Operation(
    summary = "Get historico laboral",
    description = "Get historico laboral",
    responses = {
      @ApiResponse(
          responseCode = "200",
          description = "",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(
                  implementation = WrapperHistLaboralResponseDTO.class,
                  type = "object")
          )
      )
    }
  )

  public ResponseEntity<WrapperHistLaboralResponseDTO> getHistoricoLaboral(
    @RequestParam(value = "referencia", required = false) String referencia,
    @RequestParam(value = "tipoSituacao", required = false) String tipoSituacao,
    @RequestParam(value = "situacaoLaboral", required = false) String situacaoLaboral,
    @RequestParam(value = "dataInicio", required = false) String dataInicio,
    @RequestParam(value = "dataFim", required = false) String dataFim,
    @RequestParam(value = "tamanho", required = false, defaultValue = "20") String tamanho,
    @RequestParam(value = "pagina", required = false, defaultValue = "0") String pagina, @PathVariable(value = "funcionarioId") String funcionarioId)
  {

      final var query = new GetHistoricoLaboralQuery(referencia, tipoSituacao, situacaoLaboral, dataInicio, dataFim, tamanho, pagina, funcionarioId);

      ResponseEntity<WrapperHistLaboralResponseDTO> response = queryBus.handle(query);

      return response;
  }

}
