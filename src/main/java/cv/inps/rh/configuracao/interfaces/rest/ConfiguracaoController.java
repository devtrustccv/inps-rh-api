/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME. */

package cv.inps.rh.configuracao.interfaces.rest;

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
import cv.inps.rh.configuracao.application.queries.*;
import cv.igrp.framework.core.domain.CommandBus;
import cv.inps.rh.configuracao.application.commands.*;
import cv.inps.rh.configuracao.application.dto.VinculoLaboralRequestDTO;
import cv.inps.rh.configuracao.application.dto.VinculoLaboralResponseDTO;
import java.util.List;

@IgrpController
@RestController
@RequestMapping(path = "configuracao")
@Tag(name = "Configuracao", description = "Parameterização Global")
public class ConfiguracaoController {

  
  private final QueryBus queryBus;
  private final CommandBus commandBus;

  public ConfiguracaoController(QueryBus queryBus, CommandBus commandBus) {
          this.queryBus = queryBus;
          this.commandBus = commandBus;
  }
   @PostMapping(
   value = "vinculoLaboral"
  )
  @Operation(
    summary = "POST method to handle operations for saveVinculoLaboral",
    description = "POST method to handle operations for saveVinculoLaboral",
    responses = {
      @ApiResponse(
          responseCode = "201",
          description = "",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(
                  implementation = VinculoLaboralResponseDTO.class,
                  type = "object")
          )
      )
    }
  )
  
  public ResponseEntity<VinculoLaboralResponseDTO> saveVinculoLaboral(@Valid @RequestBody VinculoLaboralRequestDTO saveVinculoLaboralRequest
    )
  {

      final var command = new SaveVinculoLaboralCommand(saveVinculoLaboralRequest);

       ResponseEntity<VinculoLaboralResponseDTO> response = commandBus.send(command);

       return response;
  }

   @PutMapping(
   value = "vinculoLaboral/{vinculoLaboralId}"
  )
  @Operation(
    summary = "PUT method to handle operations for updateVinculoLaboral",
    description = "PUT method to handle operations for updateVinculoLaboral",
    responses = {
      @ApiResponse(
          responseCode = "200",
          description = "",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(
                  implementation = VinculoLaboralResponseDTO.class,
                  type = "object")
          )
      )
    }
  )
  
  public ResponseEntity<VinculoLaboralResponseDTO> updateVinculoLaboral(@Valid @RequestBody VinculoLaboralRequestDTO updateVinculoLaboralRequest
    , @PathVariable(value = "vinculoLaboralId") String vinculoLaboralId)
  {

      final var command = new UpdateVinculoLaboralCommand(updateVinculoLaboralRequest, vinculoLaboralId);

       ResponseEntity<VinculoLaboralResponseDTO> response = commandBus.send(command);

       return response;
  }

   @GetMapping(
   value = "vinculoLaboral"
  )
  @Operation(
    summary = "GET method to handle operations for listVinculoLaboral",
    description = "GET method to handle operations for listVinculoLaboral",
    responses = {
      @ApiResponse(
          responseCode = "200",
          description = "",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(
                  implementation = VinculoLaboralResponseDTO.class,
                  type = "object")
          )
      )
    }
  )
  
  public ResponseEntity<List<VinculoLaboralResponseDTO>> listVinculoLaboral(
    @RequestParam(value = "pagina", defaultValue = "0") String pagina,
    @RequestParam(value = "tamanho", defaultValue = "20") String tamanho)
  {

      final var query = new ListVinculoLaboralQuery(pagina, tamanho);

      ResponseEntity<List<VinculoLaboralResponseDTO>> response = queryBus.handle(query);

      return response;
  }

   @DeleteMapping(
   value = "vinculoLaboral/{vinculoLaboralId}"
  )
  @Operation(
    summary = "DELETE method to handle operations for deleteVinculoLaboral",
    description = "DELETE method to handle operations for deleteVinculoLaboral",
    responses = {
      @ApiResponse(
          responseCode = "204",
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
  
  public ResponseEntity<String> deleteVinculoLaboral(
    @PathVariable(value = "vinculoLaboralId") String vinculoLaboralId)
  {

      final var command = new DeleteVinculoLaboralCommand(vinculoLaboralId);

       ResponseEntity<String> response = commandBus.send(command);

       return response;
  }

}