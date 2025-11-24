/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME. */

package cv.inps.rh.funcionario.interfaces.rest;

import cv.igrp.framework.core.domain.CommandBus;
import cv.igrp.framework.core.domain.QueryBus;
import cv.igrp.framework.stereotype.IgrpController;
import cv.inps.rh.funcionario.application.commands.NovaCarreiraCommand;
import cv.inps.rh.funcionario.application.commands.ValidarCarreiraCommand;
import cv.inps.rh.funcionario.application.dto.DadosContratuaisReqDTO;
import cv.inps.rh.funcionario.application.dto.ValidacaoCarreiraDTO;
import cv.inps.rh.funcionario.application.dto.WrapperCarreiraListDTO;
import cv.inps.rh.funcionario.application.queries.GetCarreiraListQuery;
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
@Tag(name = "Carreira", description = "Gestao carreiras funcionario")
public class CarreiraController {


  private final QueryBus queryBus;
  private final CommandBus commandBus;

  public CarreiraController(QueryBus queryBus, CommandBus commandBus) {
          this.queryBus = queryBus;
          this.commandBus = commandBus;
  }
   @GetMapping(
   value = "carreiras"
  )
  @Operation(
    summary = "Get carreira list",
    description = "Get carreira list",
    responses = {
      @ApiResponse(
          responseCode = "200",
          description = "",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(
                  implementation = WrapperCarreiraListDTO.class,
                  type = "object")
          )
      )
    }
  )

  public ResponseEntity<WrapperCarreiraListDTO> getCarreiraList(
    @RequestParam(value = "idFuncionario") String idFuncionario,
    @RequestParam(value = "pageSize", defaultValue = "20") String pageSize,
    @RequestParam(value = "pageNumber", defaultValue = "0") String pageNumber,
    @RequestParam(value = "dataInicio", required = false) String dataInicio,
    @RequestParam(value = "dataFim", required = false) String dataFim,
    @RequestParam(value = "tipoCarreira", required = false) String tipoCarreira)
  {

      final var query = new GetCarreiraListQuery(idFuncionario, pageSize, pageNumber, dataInicio, dataFim, tipoCarreira);

      ResponseEntity<WrapperCarreiraListDTO> response = queryBus.handle(query);

      return response;
  }

   @PostMapping(
   value = "{funcionarioId}"
  )
  @Operation(
    summary = "Nova carreira",
    description = "Nova carreira",
    responses = {
      @ApiResponse(
          responseCode = "200",
          description = "",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(
                  implementation = String.class,
                  type = "String")
          )
      )
    }
  )

  public ResponseEntity<String> novaCarreira(@Valid @RequestBody DadosContratuaisReqDTO novaCarreiraRequest
    , @PathVariable(value = "funcionarioId") String funcionarioId)
  {

      final var command = new NovaCarreiraCommand(novaCarreiraRequest, funcionarioId);

       ResponseEntity<String> response = commandBus.send(command);

       return response;
  }

   @PostMapping(
   value = "{funcionarioId}/validar/{carreiraId}"
  )
  @Operation(
    summary = "Validar carreira",
    description = "Validar carreira",
    responses = {
      @ApiResponse(
          responseCode = "200",
          description = "",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(
                  implementation = String.class,
                  type = "String")
          )
      )
    }
  )

  public ResponseEntity<String> validarCarreira(@Valid @RequestBody ValidacaoCarreiraDTO validarCarreiraRequest
    , @PathVariable(value = "funcionarioId") String funcionarioId,@PathVariable(value = "carreiraId") String carreiraId)
  {

      final var command = new ValidarCarreiraCommand(validarCarreiraRequest, funcionarioId, carreiraId);

       ResponseEntity<String> response = commandBus.send(command);

       return response;
  }

}
