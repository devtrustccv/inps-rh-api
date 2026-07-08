/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME. */

package cv.inps.rh.funcionario.interfaces.rest;

import cv.igrp.framework.core.domain.CommandBus;
import cv.igrp.framework.core.domain.QueryBus;
import cv.igrp.framework.stereotype.IgrpController;
import cv.inps.rh.funcionario.application.commands.NovoContratoCommand;
import cv.inps.rh.funcionario.application.commands.RenovarContratoCommand;
import cv.inps.rh.funcionario.application.commands.ValidarContratoCommand;
import cv.inps.rh.funcionario.application.commands.ValidarRenovacaoContratoCommand;
import cv.inps.rh.funcionario.application.dto.DadosContratuaisRespDTO;
import cv.inps.rh.funcionario.application.dto.NovoContratoDTO;
import cv.inps.rh.funcionario.application.dto.RenovacaoContratoDTO;
import cv.inps.rh.funcionario.application.dto.RenovacaoDetalheDTO;
import cv.inps.rh.funcionario.application.dto.WrapperListContratoDTO;
import cv.inps.rh.funcionario.application.queries.GetContratoByIdQuery;
import cv.inps.rh.funcionario.application.queries.GetListContratosQuery;
import cv.inps.rh.funcionario.application.queries.GetRenovacaoDetalheQuery;
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
    summary = "Get list contratos",
    description = "Get list contratos",
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
   value = "{idFuncionario}/contratos"
  )
  @Operation(
    summary = "Novo contrato",
    description = "Novo contrato",
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

  public ResponseEntity<DadosContratuaisRespDTO> novoContrato(@Valid @RequestBody NovoContratoDTO novoContratoRequest
    , @PathVariable(value = "idFuncionario") String idFuncionario)
  {

      final var command = new NovoContratoCommand(novoContratoRequest, idFuncionario);

       ResponseEntity<DadosContratuaisRespDTO> response = commandBus.send(command);

       return response;
  }

   @GetMapping(
   value = "{id}/contratos/{contratoId}"
  )
  @Operation(
    summary = "Get contrato by id",
    description = "Get contrato by id",
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
    @PathVariable(value = "contratoId") String contratoId,@PathVariable(value = "id") String id)
  {

      final var query = new GetContratoByIdQuery(contratoId, id);

      ResponseEntity<DadosContratuaisRespDTO> response = queryBus.handle(query);

      return response;
  }

   @PostMapping(
   value = "{idFuncionario}/renovacao-contrato/{contratoId}"
  )
  @Operation(
    summary = "Renovar contrato",
    description = "Renovar contrato",
    responses = {
      @ApiResponse(
          responseCode = "200",
          description = "",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(
                  implementation = RenovacaoContratoDTO.class,
                  type = "object")
          )
      )
    }
  )

  public ResponseEntity<RenovacaoContratoDTO> renovarContrato(@Valid @RequestBody RenovacaoContratoDTO renovarContratoRequest
    , @PathVariable(value = "idFuncionario") String idFuncionario,@PathVariable(value = "contratoId") String contratoId)
  {

      final var command = new RenovarContratoCommand(renovarContratoRequest, idFuncionario, contratoId);

       ResponseEntity<RenovacaoContratoDTO> response = commandBus.send(command);

       return response;
  }

   @PutMapping(
   value = "{idFuncionario}/contratos/{contratoId}"
  )
  @Operation(
    summary = "Validar contrato",
    description = "Validar contrato",
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

  public ResponseEntity<DadosContratuaisRespDTO> validarContrato(@Valid @RequestBody NovoContratoDTO validarContratoRequest
    , @PathVariable(value = "idFuncionario") String idFuncionario,@PathVariable(value = "contratoId") String contratoId)
  {

      final var command = new ValidarContratoCommand(validarContratoRequest, idFuncionario, contratoId);

       ResponseEntity<DadosContratuaisRespDTO> response = commandBus.send(command);

       return response;
  }

   @PostMapping(
   value = "{idFuncionario}/validar-renovacao-contrato/{contratoId}"
  )
  @Operation(
    summary = "Validar renovacao contrato",
    description = "Validar renovacao contrato",
    responses = {
      @ApiResponse(
          responseCode = "200",
          description = "",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(
                  implementation = RenovacaoContratoDTO.class,
                  type = "object")
          )
      )
    }
  )

  public ResponseEntity<RenovacaoContratoDTO> validarRenovacaoContrato(@Valid @RequestBody RenovacaoContratoDTO validarRenovacaoContratoRequest
    , @PathVariable(value = "idFuncionario") String idFuncionario,@PathVariable(value = "contratoId") String contratoId)
  {

      final var command = new ValidarRenovacaoContratoCommand(validarRenovacaoContratoRequest, idFuncionario, contratoId);

       ResponseEntity<RenovacaoContratoDTO> response = commandBus.send(command);

       return response;
  }

   @GetMapping(
   value = "{idFuncionario}/renovacao-contrato/{contratoId}"
  )
  @Operation(
    summary = "Get detalhe renovacao (atual + pendente)",
    description = "Get detalhe renovacao (atual + pendente)",
    responses = {
      @ApiResponse(
          responseCode = "200",
          description = "",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(
                  implementation = RenovacaoDetalheDTO.class,
                  type = "object")
          )
      )
    }
  )

  public ResponseEntity<RenovacaoDetalheDTO> getRenovacaoDetalhe(
    @PathVariable(value = "idFuncionario") String idFuncionario,@PathVariable(value = "contratoId") String contratoId)
  {

      final var query = new GetRenovacaoDetalheQuery(idFuncionario, contratoId);

      return queryBus.handle(query);

  }

}
