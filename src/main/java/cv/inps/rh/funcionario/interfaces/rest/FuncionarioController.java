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
import cv.inps.rh.funcionario.application.dto.FuncionarioRequestDTO;
import cv.inps.rh.funcionario.application.dto.FuncionarioResponseDTO;
import cv.inps.rh.funcionario.application.dto.FuncionarioResponseDetailsDTO;
import cv.inps.rh.funcionario.application.dto.WrapperListaValidacoesDTO;
import cv.inps.rh.funcionario.application.dto.WrapperListaFuncionarioDTO;
import java.util.Map;

@IgrpController
@RestController
@RequestMapping(path = "api/v1/funcionarios")
@Tag(name = "Funcionario", description = "gestao de funcionarios")
public class FuncionarioController {

  
  private final QueryBus queryBus;
  private final CommandBus commandBus;

  public FuncionarioController(QueryBus queryBus, CommandBus commandBus) {
          this.queryBus = queryBus;
          this.commandBus = commandBus;
  }
   @PostMapping(
  )
  @Operation(
    summary = "POST method to handle operations for createFuncionario",
    description = "POST method to handle operations for createFuncionario",
    responses = {
      @ApiResponse(
          responseCode = "200",
          description = "",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(
                  implementation = FuncionarioResponseDTO.class,
                  type = "object")
          )
      )
    }
  )
  
  public ResponseEntity<FuncionarioResponseDTO> createFuncionario(@Valid @RequestBody FuncionarioRequestDTO createFuncionarioRequest
    )
  {

      final var command = new CreateFuncionarioCommand(createFuncionarioRequest);

       ResponseEntity<FuncionarioResponseDTO> response = commandBus.send(command);

       return response;
  }

   @GetMapping(
   value = "{id}"
  )
  @Operation(
    summary = "GET method to handle operations for getFuncionarioById",
    description = "GET method to handle operations for getFuncionarioById",
    responses = {
      @ApiResponse(
          responseCode = "200",
          description = "",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(
                  implementation = FuncionarioResponseDetailsDTO.class,
                  type = "object")
          )
      )
    }
  )
  
  public ResponseEntity<FuncionarioResponseDetailsDTO> getFuncionarioById(
    @PathVariable(value = "id") String id)
  {

      final var query = new GetFuncionarioByIdQuery(id);

      ResponseEntity<FuncionarioResponseDetailsDTO> response = queryBus.handle(query);

      return response;
  }

   @GetMapping(
   value = "validacoes"
  )
  @Operation(
    summary = "GET method to handle operations for getValicoesUtilizadores",
    description = "GET method to handle operations for getValicoesUtilizadores",
    responses = {
      @ApiResponse(
          responseCode = "200",
          description = "",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(
                  implementation = WrapperListaValidacoesDTO.class,
                  type = "object")
          )
      )
    }
  )
  
  public ResponseEntity<WrapperListaValidacoesDTO> getValicoesUtilizadores(
    @RequestParam(value = "nomeColaborador", required = false) String nomeColaborador,
    @RequestParam(value = "tipoOperacao", required = false) String tipoOperacao,
    @RequestParam(value = "referenciaName", required = false) String referenciaName,
    @RequestParam(value = "dataInicio", required = false) String dataInicio,
    @RequestParam(value = "dataFim", required = false) String dataFim,
    @RequestParam(value = "pageNumber", defaultValue = "0") String pageNumber,
    @RequestParam(value = "pageSize", defaultValue = "20") String pageSize)
  {

      final var query = new GetValicoesUtilizadoresQuery(nomeColaborador, tipoOperacao, referenciaName, dataInicio, dataFim, pageNumber, pageSize);

      ResponseEntity<WrapperListaValidacoesDTO> response = queryBus.handle(query);

      return response;
  }

   @GetMapping(
  )
  @Operation(
    summary = "GET method to handle operations for getListFuncionarios",
    description = "GET method to handle operations for getListFuncionarios",
    responses = {
      @ApiResponse(
          responseCode = "200",
          description = "",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(
                  implementation = WrapperListaFuncionarioDTO.class,
                  type = "object")
          )
      )
    }
  )
  
  public ResponseEntity<WrapperListaFuncionarioDTO> getListFuncionarios(
    @RequestParam(value = "pageNumber", defaultValue = "0") String pageNumber,
    @RequestParam(value = "pageSize", defaultValue = "20") String pageSize,
    @RequestParam(value = "nome", required = false) String nome,
    @RequestParam(value = "direccao", required = false) Long direccao,
    @RequestParam(value = "seccao", required = false) Long seccao,
    @RequestParam(value = "tipoVinculoLaboral", required = false) Long tipoVinculoLaboral,
    @RequestParam(value = "dataInicio", required = false) String dataInicio,
    @RequestParam(value = "dataFim", required = false) String dataFim,
    @RequestParam(value = "estado", required = false) String estado)
  {

      final var query = new GetListFuncionariosQuery(pageNumber, pageSize, nome, direccao, seccao, tipoVinculoLaboral, dataInicio, dataFim, estado);

      ResponseEntity<WrapperListaFuncionarioDTO> response = queryBus.handle(query);

      return response;
  }

   @PutMapping(
   value = "{id}"
  )
  @Operation(
    summary = "PUT method to handle operations for validarRegistoColaborador",
    description = "PUT method to handle operations for validarRegistoColaborador",
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
  
  public ResponseEntity<Map<String, ?>> validarRegistoColaborador(@Valid @RequestBody FuncionarioRequestDTO validarRegistoColaboradorRequest
    , @PathVariable(value = "id") String id)
  {

      final var command = new ValidarRegistoColaboradorCommand(validarRegistoColaboradorRequest, id);

       ResponseEntity<Map<String, ?>> response = commandBus.send(command);

       return response;
  }

}