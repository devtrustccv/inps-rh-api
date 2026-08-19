/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME. */

package cv.inps.rh.funcionario.interfaces.rest;

import cv.igrp.framework.core.domain.CommandBus;
import cv.igrp.framework.core.domain.QueryBus;
import cv.igrp.framework.stereotype.IgrpController;
import cv.inps.rh.funcionario.application.commands.RegistarSubstituicaoCommand;
import cv.inps.rh.funcionario.application.commands.ValidarSubstituicaoCommand;
import cv.inps.rh.funcionario.application.dto.CalcularSubstituicaoResponseDTO;
import cv.inps.rh.funcionario.application.dto.SubstituicaoDTO;
import cv.inps.rh.funcionario.application.dto.SubstituicaoDetalheDTO;
import cv.inps.rh.funcionario.application.dto.WrapperSubstituicaoSumaryDTO;
import cv.inps.rh.funcionario.application.queries.CalcularSubstituicaoQuery;
import cv.inps.rh.funcionario.application.queries.GetSubstituicaoByIdQuery;
import cv.inps.rh.funcionario.application.queries.ListaSubstituicaoQuery;
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
@Tag(name = "Substituicao", description = "gestao substituicoes")
public class SubstituicaoController {


  private final QueryBus queryBus;
  private final CommandBus commandBus;

  public SubstituicaoController(QueryBus queryBus, CommandBus commandBus) {
          this.queryBus = queryBus;
          this.commandBus = commandBus;
  }
   @PostMapping(
   value = "{idFuncionario}/substituicoes"
  )
  @Operation(
    summary = "Registar substituicao",
    description = "Registar substituicao",
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

  public ResponseEntity<SuccessResponseDTO> registarSubstituicao(@Valid @RequestBody SubstituicaoDTO registarSubstituicaoRequest
    , @PathVariable(value = "idFuncionario") String idFuncionario)
  {

      final var command = new RegistarSubstituicaoCommand(registarSubstituicaoRequest, idFuncionario);

       ResponseEntity<SuccessResponseDTO> response = commandBus.send(command);

       return response;
  }

   @PutMapping(
   value = "{idFuncionario}/substituicoes/{substituicaoId}"
  )
  @Operation(
    summary = "Validar substituicao",
    description = "Validar substituicao",
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

  public ResponseEntity<SuccessResponseDTO> validarSubstituicao(@Valid @RequestBody SubstituicaoDTO validarSubstituicaoRequest
    , @PathVariable(value = "idFuncionario") String idFuncionario,@PathVariable(value = "substituicaoId") String substituicaoId)
  {

      final var command = new ValidarSubstituicaoCommand(validarSubstituicaoRequest, idFuncionario, substituicaoId);

       ResponseEntity<SuccessResponseDTO> response = commandBus.send(command);

       return response;
  }

   @GetMapping(
   value = "{idFuncionario}/substituicoes/{substituicaoId}"
  )
  @Operation(
    summary = "Get substituicao by id",
    description = "Get substituicao by id",
    responses = {
      @ApiResponse(
          responseCode = "200",
          description = "",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(
                  implementation = SubstituicaoDetalheDTO.class,
                  type = "object")
          )
      )
    }
  )

  public ResponseEntity<SubstituicaoDetalheDTO> getSubstituicaoById(
    @PathVariable(value = "idFuncionario") String idFuncionario,@PathVariable(value = "substituicaoId") String substituicaoId)
  {

      final var query = new GetSubstituicaoByIdQuery(idFuncionario, substituicaoId);

      ResponseEntity<SubstituicaoDetalheDTO> response = queryBus.handle(query);

      return response;
  }

   @GetMapping(
   value = "substituicoes"
  )
  @Operation(
    summary = "Lista substituicao",
    description = "Lista substituicao",
    responses = {
      @ApiResponse(
          responseCode = "200",
          description = "",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(
                  implementation = WrapperSubstituicaoSumaryDTO.class,
                  type = "object")
          )
      )
    }
  )

  public ResponseEntity<WrapperSubstituicaoSumaryDTO> listaSubstituicao(
    @RequestParam(value = "pageNumber", defaultValue = "0") String pageNumber,
    @RequestParam(value = "pageSize", defaultValue = "20") String pageSize,
    @RequestParam(value = "idFuncionario") String idFuncionario)
  {

      final var query = new ListaSubstituicaoQuery(pageNumber, pageSize, idFuncionario);

      ResponseEntity<WrapperSubstituicaoSumaryDTO> response = queryBus.handle(query);

      return response;
  }

   @GetMapping(
   value = "substituicoes/calcular"
  )
  @Operation(
    summary = "Calcular substituicao",
    description = "Calcula detalhe mensal da substituicao entre dois colaboradores num intervalo de datas",
    responses = {
      @ApiResponse(
          responseCode = "200",

          content = @Content(
              mediaType = "application/json",
              schema = @Schema(
                  implementation = CalcularSubstituicaoResponseDTO.class,
                  type = "object")
          )
      )
    }
  )

  public ResponseEntity<CalcularSubstituicaoResponseDTO> calcularSubstituicao(
    @RequestParam(value = "dataInicio") String dataInicio,
    @RequestParam(value = "dataFim") String dataFim,
    @RequestParam(value = "funcionarioDeId") String funcionarioDeId,
    @RequestParam(value = "funcionarioParaId") String funcionarioParaId)
  {

      final var query = new CalcularSubstituicaoQuery(dataInicio, dataFim, funcionarioDeId, funcionarioParaId);

      return queryBus.handle(query);

  }

}
