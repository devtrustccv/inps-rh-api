/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME. */

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

import cv.inps.rh.funcionario.application.dto.WrapperRegimeListDTO;

@IgrpController
@RestController
@RequestMapping(path = "api/v1/funcionarios")
@Tag(name = "Regime", description = "Gestao Regimes")
public class RegimeController {

  
  private final QueryBus queryBus;

  public RegimeController(QueryBus queryBus) {
          this.queryBus = queryBus;
          
  }
   @GetMapping(
   value = "regimes"
  )
  @Operation(
    summary = "GET method to handle operations for getListRegimes",
    description = "GET method to handle operations for getListRegimes",
    responses = {
      @ApiResponse(
          responseCode = "200",
          description = "",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(
                  implementation = WrapperRegimeListDTO.class,
                  type = "object")
          )
      )
    }
  )
  
  public ResponseEntity<WrapperRegimeListDTO> getListRegimes(
    @RequestParam(value = "pageSize", defaultValue = "20") String pageSize,
    @RequestParam(value = "pageNumber", defaultValue = "0") String pageNumber,
    @RequestParam(value = "estado", required = false) String estado,
    @RequestParam(value = "tipoRegime", required = false) String tipoRegime)
  {

      final var query = new GetListRegimesQuery(pageSize, pageNumber, estado, tipoRegime);

      ResponseEntity<WrapperRegimeListDTO> response = queryBus.handle(query);

      return response;
  }

}