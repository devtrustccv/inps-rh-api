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

import cv.inps.rh.funcionario.application.dto.WrapperListContratoDTO;

@IgrpController
@RestController
@RequestMapping(path = "api/v1/funcionarios")
@Tag(name = "Contrato", description = "Gestao Contratos")
public class ContratoController {


  private final QueryBus queryBus;

  public ContratoController(QueryBus queryBus) {
          this.queryBus = queryBus;

  }
   @GetMapping(
   value = "contratos"
  )
  @Operation(
    summary = "GET method to handle operations for getListContratos",
    description = "GET method to handle operations for getListContratos",
    responses = {
      @ApiResponse(
          responseCode = "200",
          description = "",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(
                  implementation = WrapperListContratoDTO.class,
                  type = "object")
          )
      )
    }
  )

  public ResponseEntity<WrapperListContratoDTO> getListContratos(
    @RequestParam(value = "idFuncionario") String idFuncionario,
    @RequestParam(value = "vinculo", required = false) Long vinculo,
    @RequestParam(value = "pageNumber", defaultValue = "0") String pageNumber,
    @RequestParam(value = "pageSize", defaultValue = "20") String pageSize)
  {

      final var query = new GetListContratosQuery(idFuncionario, vinculo, pageNumber, pageSize);

      ResponseEntity<WrapperListContratoDTO> response = queryBus.handle(query);

      return response;
  }

}
