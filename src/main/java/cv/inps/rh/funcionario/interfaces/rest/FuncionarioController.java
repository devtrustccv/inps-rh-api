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
import cv.inps.rh.funcionario.application.dto.FuncionarioRequestDTO;
import cv.inps.rh.funcionario.application.dto.FuncionarioResponseDTO;

@IgrpController
@RestController
@RequestMapping(path = "api/v1/funcionarios")
@Tag(name = "Funcionario", description = "gestao de funcionarios")
public class FuncionarioController {

  
  private final CommandBus commandBus;

  public FuncionarioController(CommandBus commandBus) {
          
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

}