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
import cv.igrp.framework.core.domain.CommandBus;
import cv.inps.rh.funcionario.application.commands.*;
import cv.inps.rh.funcionario.application.dto.SubstituicaoDTO;

@IgrpController
@RestController
@RequestMapping(path = "api/v1/funcionarios")
@Tag(name = "Substituicao", description = "gestao substituicoes")
public class SubstituicaoController {

  
  private final CommandBus commandBus;

  public SubstituicaoController(CommandBus commandBus) {
          
          this.commandBus = commandBus;
  }
   @PostMapping(
   value = "substituicoes/{idFuncionario}"
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
   value = "{id}/substituicoes/{substituicaoId}"
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
    , @PathVariable(value = "id") String id,@PathVariable(value = "substituicaoId") String substituicaoId)
  {

      final var command = new ValidarSubstituicaoCommand(validarSubstituicaoRequest, id, substituicaoId);

       ResponseEntity<SubstituicaoDTO> response = commandBus.send(command);

       return response;
  }

}