/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME. */

package cv.inps.rh.funcionario.interfaces.rest;

import cv.igrp.framework.core.domain.CommandBus;
import cv.igrp.framework.core.domain.QueryBus;
import cv.igrp.framework.stereotype.IgrpController;
import cv.inps.rh.funcionario.application.commands.RegistarSubstituicaoCommand;
import cv.inps.rh.funcionario.application.commands.ValidarSubstituicaoCommand;
import cv.inps.rh.funcionario.application.dto.SubstituicaoDTO;
import cv.inps.rh.funcionario.application.dto.SubstituicaoSumaryDTO;
import cv.inps.rh.funcionario.application.queries.ListaSubstituicaoQuery;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
                  implementation = SubstituicaoDTO.class,
                  type = "object")
          )
      )
    }
  )

  public ResponseEntity<SubstituicaoDTO> registarSubstituicao(@Valid @RequestBody SubstituicaoDTO registarSubstituicaoRequest
    , @PathVariable(value = "idFuncionario") String idFuncionario)
  {

      final var command = new RegistarSubstituicaoCommand(registarSubstituicaoRequest, idFuncionario);

       ResponseEntity<SubstituicaoDTO> response = commandBus.send(command);

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
                  implementation = SubstituicaoDTO.class,
                  type = "object")
          )
      )
    }
  )

  public ResponseEntity<SubstituicaoDTO> validarSubstituicao(@Valid @RequestBody SubstituicaoDTO validarSubstituicaoRequest
    , @PathVariable(value = "idFuncionario") String idFuncionario,@PathVariable(value = "substituicaoId") String substituicaoId)
  {

      final var command = new ValidarSubstituicaoCommand(validarSubstituicaoRequest, idFuncionario, substituicaoId);

       ResponseEntity<SubstituicaoDTO> response = commandBus.send(command);

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
                  implementation = SubstituicaoSumaryDTO.class,
                  type = "object")
          )
      )
    }
  )

  public ResponseEntity<List<SubstituicaoSumaryDTO>> listaSubstituicao(
    @RequestParam(value = "pageNumber", defaultValue = "0") String pageNumber,
    @RequestParam(value = "pageSize", defaultValue = "20") String pageSize,
    @RequestParam(value = "idFuncionario") String idFuncionario)
  {

      final var query = new ListaSubstituicaoQuery(pageNumber, pageSize, idFuncionario);

      ResponseEntity<List<SubstituicaoSumaryDTO>> response = queryBus.handle(query);

      return response;
  }

}
