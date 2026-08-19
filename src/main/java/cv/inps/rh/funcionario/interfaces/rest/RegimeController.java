/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME. */

package cv.inps.rh.funcionario.interfaces.rest;

import cv.igrp.framework.core.domain.CommandBus;
import cv.igrp.framework.core.domain.QueryBus;
import cv.igrp.framework.stereotype.IgrpController;
import cv.inps.rh.funcionario.application.commands.AdicionarRegimeTrabalhoCommand;
import cv.inps.rh.funcionario.application.commands.ValidarRegimeTrabalhoCommand;
import cv.inps.rh.funcionario.application.dto.RegimeDetalheDTO;
import cv.inps.rh.funcionario.application.dto.RegimeTrabalhoDTO;
import cv.inps.rh.funcionario.application.dto.WrapperRegimeListDTO;
import cv.inps.rh.funcionario.application.queries.GetListRegimesQuery;
import cv.inps.rh.funcionario.application.queries.GetRegimeByIdQuery;
import cv.inps.rh.shared.application.dto.SuccessResponseDTO;
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
                  implementation = SuccessResponseDTO.class,
                  type = "object")
          )
      )
    }
  )

  public ResponseEntity<SuccessResponseDTO> adicionarRegimeTrabalho(@Valid @RequestBody RegimeTrabalhoDTO adicionarRegimeTrabalhoRequest
    , @PathVariable(value = "idFuncionario") String idFuncionario)
  {

      final var command = new AdicionarRegimeTrabalhoCommand(adicionarRegimeTrabalhoRequest, idFuncionario);

       ResponseEntity<SuccessResponseDTO> response = commandBus.send(command);

       return response;
  }

   @PutMapping(
   value = "{idFuncionario}/regimes/{regimeId}"
  )
  @Operation(
    summary = "Validar regime trabalho",
    description = "Validar regime trabalho",
    responses = {
      @ApiResponse(
          responseCode = "200",
          description = "",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(
                  implementation = SuccessResponseDTO.class,
                  type = "object")
          )
      )
    }
  )

  public ResponseEntity<SuccessResponseDTO> validarRegimeTrabalho(@Valid @RequestBody RegimeTrabalhoDTO validarRegimeTrabalhoRequest
    , @PathVariable(value = "idFuncionario") String idFuncionario,@PathVariable(value = "regimeId") String regimeId)
  {

      final var command = new ValidarRegimeTrabalhoCommand(validarRegimeTrabalhoRequest, idFuncionario, regimeId);

       ResponseEntity<SuccessResponseDTO> response = commandBus.send(command);

       return response;
  }

   @GetMapping(
   value = "{idFuncionario}/regimes/{regimeId}"
  )
  @Operation(
    summary = "Get regime by id",
    description = "Get regime by id",
    responses = {
      @ApiResponse(
          responseCode = "200",
          description = "",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(
                  implementation = RegimeDetalheDTO.class,
                  type = "object")
          )
      )
    }
  )

  public ResponseEntity<RegimeDetalheDTO> getRegimeById(
    @PathVariable(value = "idFuncionario") String idFuncionario,@PathVariable(value = "regimeId") String regimeId)
  {

      final var query = new GetRegimeByIdQuery(idFuncionario, regimeId);

      ResponseEntity<RegimeDetalheDTO> response = queryBus.handle(query);

      return response;
  }

}
