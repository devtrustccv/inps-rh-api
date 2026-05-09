/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME */

package cv.inps.rh.funcionario.interfaces.rest;

import cv.igrp.framework.core.domain.CommandBus;
import cv.igrp.framework.core.domain.QueryBus;
import cv.igrp.framework.stereotype.IgrpController;
import cv.inps.rh.funcionario.application.commands.AtualizarCarreiraCommand;
import cv.inps.rh.funcionario.application.commands.EliminarCarreiraCommand;
import cv.inps.rh.funcionario.application.commands.NovaCarreiraCommand;
import cv.inps.rh.funcionario.application.commands.ValidarCarreiraCommand;
import cv.inps.rh.funcionario.application.dto.CarreiraResponseDTO;
import cv.inps.rh.funcionario.application.dto.DadosContratuaisReqDTO;
import cv.inps.rh.funcionario.application.dto.ValidacaoCarreiraDTO;
import cv.inps.rh.funcionario.application.dto.WrapperCarreiraListDTO;
import cv.inps.rh.funcionario.application.queries.GetCarreiraAtualQuery;
import cv.inps.rh.funcionario.application.queries.GetCarreiraByIdQuery;
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
@Tag(
    name = "Funcionario",
    description = "Gestao carreiras funcionario"
)
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

      return queryBus.handle(query);

  }

   @PostMapping(
   value = "{funcionarioId}/carreiras"
  )
  @Operation(
    summary = "Nova carreira",
    description = "Nova carreira",
    responses = {
      @ApiResponse(
          responseCode = "200",

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

      return commandBus.send(command);

  }

   @PostMapping(
   value = "{funcionarioId}/carreiras/{carreiraId}/validar"
  )
  @Operation(
    summary = "Validar carreira",
    description = "Validar carreira",
    responses = {
      @ApiResponse(
          responseCode = "200",

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

      return commandBus.send(command);

  }

   @GetMapping(
   value = "carreiras/{carreiraId}"
  )
  @Operation(
    summary = "Get carreira by id",
    description = "Get carreira by id",
    responses = {
      @ApiResponse(
          responseCode = "200",

          content = @Content(
              mediaType = "application/json",
              schema = @Schema(
                  implementation = CarreiraResponseDTO.class,
                  type = "object")
          )
      )
    }
  )

   public ResponseEntity<CarreiraResponseDTO> getCarreiraById(
    @PathVariable(value = "carreiraId") String carreiraId)
  {

      final var query = new GetCarreiraByIdQuery(carreiraId);

      return queryBus.handle(query);

  }

   @GetMapping(
   value = "carreiras/{uuidFuncionario}/atual"
  )
  @Operation(
    summary = "Get carreira atual",
    description = "Get carreira atual",
    responses = {
      @ApiResponse(
          responseCode = "200",

          content = @Content(
              mediaType = "application/json",
              schema = @Schema(
                  implementation = CarreiraResponseDTO.class,
                  type = "object")
          )
      )
    }
  )

   public ResponseEntity<CarreiraResponseDTO> getCarreiraAtual(
    @PathVariable(value = "uuidFuncionario") String uuidFuncionario)
  {

      final var query = new GetCarreiraAtualQuery(uuidFuncionario);

      return queryBus.handle(query);

  }

   @DeleteMapping(
   value = "carreiras/{carreiraId}"
  )
  @Operation(
    summary = "Eliminar carreira",
    description = "Eliminar carreira",
    responses = {
      @ApiResponse(
          responseCode = "204",

          content = @Content(
              mediaType = "application/json",
              schema = @Schema(
                  implementation = String.class,
                  type = "String")
          )
      )
    }
  )

   public ResponseEntity<String> eliminarCarreira(
    @PathVariable(value = "carreiraId") String carreiraId)
  {

      final var command = new EliminarCarreiraCommand(carreiraId);

      return commandBus.send(command);

  }

   @PutMapping(
   value = "{funcionarioId}/carreiras/{carreiraId}"
  )
  @Operation(
    summary = "Atualizar carreira",
    description = "Atualizar carreira",
    responses = {
      @ApiResponse(
          responseCode = "200",

          content = @Content(
              mediaType = "application/json",
              schema = @Schema(
                  implementation = String.class,
                  type = "String")
          )
      )
    }
  )

   public ResponseEntity<String> atualizarCarreira(@Valid @RequestBody DadosContratuaisReqDTO atualizarCarreiraRequest
    , @PathVariable(value = "funcionarioId") String funcionarioId,@PathVariable(value = "carreiraId") String carreiraId)
  {

      final var command = new AtualizarCarreiraCommand(atualizarCarreiraRequest, funcionarioId, carreiraId);

      return commandBus.send(command);

  }

}
