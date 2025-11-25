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
import cv.igrp.framework.core.domain.CommandBus;
import cv.inps.rh.funcionario.application.commands.*;
import cv.inps.rh.funcionario.application.dto.WrapperRegimeListDTO;
import cv.inps.rh.funcionario.application.dto.RegimeTrabalhoDTO;

@IgrpController
@RestController
@RequestMapping(path = "api/v1/funcionarios")
@Tag(name = "Regime", description = "Gestao Regimes")
public class RegimeController {

  
  private final QueryBus queryBus;
  private final CommandBus commandBus;

  public RegimeController(QueryBus queryBus, CommandBus commandBus) {
          this.queryBus = queryBus;
          this.commandBus = commandBus;
  }
   @GetMapping(
   value = "regimes"
  )
  @Operation(
    summary = "Get list regimes",
    description = "Get list regimes",
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
    @RequestParam(value = "idFuncionario") String idFuncionario,
    @RequestParam(value = "pageSize", defaultValue = "20") String pageSize,
    @RequestParam(value = "pageNumber", defaultValue = "0") String pageNumber,
    @RequestParam(value = "estado", required = false) String estado,
    @RequestParam(value = "tipoRegime", required = false) String tipoRegime)
  {

      final var query = new GetListRegimesQuery(idFuncionario, pageSize, pageNumber, estado, tipoRegime);

      ResponseEntity<WrapperRegimeListDTO> response = queryBus.handle(query);

      return response;
  }

   @PostMapping(
   value = "{idFuncionario}/regimes"
  )
  @Operation(
    summary = "Adicionar regime trabalho",
    description = "Adicionar regime trabalho",
    responses = {
      @ApiResponse(
          responseCode = "200",
          description = "",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(
                  implementation = RegimeTrabalhoDTO.class,
                  type = "object")
          )
      )
    }
  )
  
  public ResponseEntity<RegimeTrabalhoDTO> adicionarRegimeTrabalho(@Valid @RequestBody RegimeTrabalhoDTO adicionarRegimeTrabalhoRequest
    , @PathVariable(value = "idFuncionario") String idFuncionario)
  {

      final var command = new AdicionarRegimeTrabalhoCommand(adicionarRegimeTrabalhoRequest, idFuncionario);

       ResponseEntity<RegimeTrabalhoDTO> response = commandBus.send(command);

       return response;
  }

}