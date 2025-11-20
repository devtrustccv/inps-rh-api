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

import cv.inps.rh.funcionario.application.dto.WrapperListRenumeracaoDTO;

@IgrpController
@RestController
@RequestMapping(path = "api/v1/funcionarios")
@Tag(name = "Renumeracao", description = "gest abonos subsidios")
public class RenumeracaoController {

  
  private final QueryBus queryBus;

  public RenumeracaoController(QueryBus queryBus) {
          this.queryBus = queryBus;
          
  }
   @GetMapping(
   value = "renumeracoes"
  )
  @Operation(
    summary = "GET method to handle operations for Get list renumeracoes",
    description = "GET method to handle operations for Get list renumeracoes",
    responses = {
      @ApiResponse(
          responseCode = "200",
          description = "",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(
                  implementation = WrapperListRenumeracaoDTO.class,
                  type = "object")
          )
      )
    }
  )
  
  public ResponseEntity<WrapperListRenumeracaoDTO> getListRenumeracoes(
    @RequestParam(value = "idFuncionario") String idFuncionario,
    @RequestParam(value = "pageSize", defaultValue = "20") String pageSize,
    @RequestParam(value = "pageNumber", defaultValue = "0") String pageNumber,
    @RequestParam(value = "dataFim", required = false) String dataFim,
    @RequestParam(value = "dataInicio", required = false) String dataInicio,
    @RequestParam(value = "estado", required = false) String estado)
  {

      final var query = new GetListRenumeracoesQuery(idFuncionario, pageSize, pageNumber, dataFim, dataInicio, estado);

      ResponseEntity<WrapperListRenumeracaoDTO> response = queryBus.handle(query);

      return response;
  }

}