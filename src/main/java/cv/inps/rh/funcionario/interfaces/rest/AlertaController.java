/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME */

package cv.inps.rh.funcionario.interfaces.rest;

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
import cv.inps.rh.funcionario.application.queries.*;

import cv.inps.rh.funcionario.application.dto.WrapperListAlertaDTO;

@IgrpController
@RestController
@RequestMapping(path = "api/v1/funcionarios")
@Tag(
    name = "Funcionario",
    description = "alerta"
)
public class AlertaController {

  
  private final QueryBus queryBus;

  public AlertaController(QueryBus queryBus) {
          this.queryBus = queryBus;
          
  }
   @GetMapping(
  )
  @Operation(
    summary = "Get list alerta",
    description = "Get list alerta",
    responses = {
      @ApiResponse(
          responseCode = "200",
          
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(
                  implementation = WrapperListAlertaDTO.class,
                  type = "object")
          )
      )
    }
  )
  
  public ResponseEntity<WrapperListAlertaDTO> getListAlerta(
    @RequestParam(value = "referencia", required = false) String referencia,
    @RequestParam(value = "tipoAlerta", required = false) String tipoAlerta,
    @RequestParam(value = "nomeColaborador", required = false) String nomeColaborador,
    @RequestParam(value = "direcaoId", required = false) Long direcaoId,
    @RequestParam(value = "seccaoId", required = false) Long seccaoId,
    @RequestParam(value = "estado", required = false) String estado,
    @RequestParam(value = "dataRegistoDe", required = false) String dataRegistoDe,
    @RequestParam(value = "dataRegistoAte", required = false) String dataRegistoAte,
    @RequestParam(value = "pageNumber", required = false, defaultValue = "0") String pageNumber,
    @RequestParam(value = "pageSize", required = false, defaultValue = "20") String pageSize)
  {

      final var query = new GetListAlertaQuery(referencia, tipoAlerta, nomeColaborador, direcaoId, seccaoId, estado, dataRegistoDe, dataRegistoAte, pageNumber, pageSize);

      return queryBus.handle(query);

  }

}