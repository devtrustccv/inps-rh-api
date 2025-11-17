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
import cv.inps.rh.funcionario.application.dto.WrapperListContratoDTO;
import cv.inps.rh.funcionario.application.dto.DadosContratuaisReqDTO;
import cv.inps.rh.funcionario.application.dto.DadosContratuaisRespDTO;
import cv.inps.rh.funcionario.application.dto.ValidarContratoDTO;
import cv.inps.rh.funcionario.application.dto.RenovarContratoReqDTO;
import cv.inps.rh.funcionario.application.dto.ValidarRenovacaoContratoDTO;

@IgrpController
@RestController
@RequestMapping(path = "api/v1/funcionarios")
@Tag(name = "Contrato", description = "Gestao Contratos")
public class ContratoController {

  
  private final QueryBus queryBus;
  private final CommandBus commandBus;

  public ContratoController(QueryBus queryBus, CommandBus commandBus) {
          this.queryBus = queryBus;
          this.commandBus = commandBus;
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

   @PostMapping(
   value = "contratos/{idFuncionario}"
  )
  @Operation(
    summary = "POST method to handle operations for novoContrato",
    description = "POST method to handle operations for novoContrato",
    responses = {
      @ApiResponse(
          responseCode = "200",
          description = "",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(
                  implementation = DadosContratuaisRespDTO.class,
                  type = "object")
          )
      )
    }
  )
  
  public ResponseEntity<DadosContratuaisRespDTO> novoContrato(@Valid @RequestBody DadosContratuaisReqDTO novoContratoRequest
    , @PathVariable(value = "idFuncionario") String idFuncionario)
  {

      final var command = new NovoContratoCommand(novoContratoRequest, idFuncionario);

       ResponseEntity<DadosContratuaisRespDTO> response = commandBus.send(command);

       return response;
  }

   @PostMapping(
   value = "validar-contrato/{contratoId}"
  )
  @Operation(
    summary = "POST method to handle operations for validarContrato",
    description = "POST method to handle operations for validarContrato",
    responses = {
      @ApiResponse(
          responseCode = "200",
          description = "",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(
                  implementation = ValidarContratoDTO.class,
                  type = "object")
          )
      )
    }
  )
  
  public ResponseEntity<ValidarContratoDTO> validarContrato(@Valid @RequestBody ValidarContratoDTO validarContratoRequest
    , @PathVariable(value = "contratoId") String contratoId)
  {

      final var command = new ValidarContratoCommand(validarContratoRequest, contratoId);

       ResponseEntity<ValidarContratoDTO> response = commandBus.send(command);

       return response;
  }

   @GetMapping(
   value = "contratos/{contratoId}"
  )
  @Operation(
    summary = "GET method to handle operations for getContratoById",
    description = "GET method to handle operations for getContratoById",
    responses = {
      @ApiResponse(
          responseCode = "200",
          description = "",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(
                  implementation = DadosContratuaisRespDTO.class,
                  type = "object")
          )
      )
    }
  )
  
  public ResponseEntity<DadosContratuaisRespDTO> getContratoById(
    @PathVariable(value = "contratoId") String contratoId)
  {

      final var query = new GetContratoByIdQuery(contratoId);

      ResponseEntity<DadosContratuaisRespDTO> response = queryBus.handle(query);

      return response;
  }

   @PostMapping(
   value = "renovacao-contrato"
  )
  @Operation(
    summary = "POST method to handle operations for renovarContrato",
    description = "POST method to handle operations for renovarContrato",
    responses = {
      @ApiResponse(
          responseCode = "200",
          description = "",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(
                  implementation = RenovarContratoReqDTO.class,
                  type = "object")
          )
      )
    }
  )
  
  public ResponseEntity<RenovarContratoReqDTO> renovarContrato(@Valid @RequestBody RenovarContratoReqDTO renovarContratoRequest
    )
  {

      final var command = new RenovarContratoCommand(renovarContratoRequest);

       ResponseEntity<RenovarContratoReqDTO> response = commandBus.send(command);

       return response;
  }

   @PostMapping(
   value = "validar-renovacao-contrato/{contratoId}"
  )
  @Operation(
    summary = "POST method to handle operations for validarRenovacaoContrato",
    description = "POST method to handle operations for validarRenovacaoContrato",
    responses = {
      @ApiResponse(
          responseCode = "200",
          description = "",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(
                  implementation = ValidarRenovacaoContratoDTO.class,
                  type = "object")
          )
      )
    }
  )
  
  public ResponseEntity<ValidarRenovacaoContratoDTO> validarRenovacaoContrato(@Valid @RequestBody ValidarRenovacaoContratoDTO validarRenovacaoContratoRequest
    , @PathVariable(value = "contratoId") String contratoId)
  {

      final var command = new ValidarRenovacaoContratoCommand(validarRenovacaoContratoRequest, contratoId);

       ResponseEntity<ValidarRenovacaoContratoDTO> response = commandBus.send(command);

       return response;
  }

}