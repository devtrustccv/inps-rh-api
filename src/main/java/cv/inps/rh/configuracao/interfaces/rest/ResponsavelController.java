/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME */

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
import cv.inps.rh.configuracao.application.dto.ResponsaveisDirecaoResponseDTO;
import cv.inps.rh.configuracao.application.dto.AssociarResponsaveisRequestDTO;
import cv.inps.rh.configuracao.application.dto.WrapperListResponsaveisDTO;

@IgrpController
@RestController
@RequestMapping(path = "configuracao")
@Tag(
    name = "Configuracao",
    description = "Gestão de Responsáveis Direção"
)
public class ResponsavelController {

  
  private final QueryBus queryBus;
  private final CommandBus commandBus;

  public ResponsavelController(QueryBus queryBus, CommandBus commandBus) {
          this.queryBus = queryBus;
          this.commandBus = commandBus;
  }
   @GetMapping(
   value = "direcao/responsavel/{institutoId}"
  )
  @Operation(
    summary = "Get responsaveis direcao",
    description = "Get responsaveis direcao",
    responses = {
      @ApiResponse(
          responseCode = "200",
          
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(
                  implementation = ResponsaveisDirecaoResponseDTO.class,
                  type = "object")
          )
      )
    }
  )
  
  public ResponseEntity<ResponsaveisDirecaoResponseDTO> getResponsaveisDirecao(
    @RequestParam(value = "seccaoId") String seccaoId, @PathVariable(value = "institutoId") String institutoId)
  {

      final var query = new GetResponsaveisDirecaoQuery(seccaoId, institutoId);

      return queryBus.handle(query);

  }

   @PostMapping(
   value = "direcao/responsavel"
  )
  @Operation(
    summary = "Save responsaveis direcao",
    description = "Save responsaveis direcao",
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
  
  public ResponseEntity<String> saveResponsaveisDirecao(@Valid @RequestBody AssociarResponsaveisRequestDTO saveResponsaveisDirecaoRequest
    )
  {

      final var command = new SaveResponsaveisDirecaoCommand(saveResponsaveisDirecaoRequest);

      return commandBus.send(command);

  }

   @GetMapping(
   value = "responsaveis/search"
  )
  @Operation(
    summary = "Get responsaveis",
    description = "Get responsaveis",
    responses = {
      @ApiResponse(
          responseCode = "200",
          
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(
                  implementation = WrapperListResponsaveisDTO.class,
                  type = "object")
          )
      )
    }
  )
  
  public ResponseEntity<WrapperListResponsaveisDTO> getResponsaveis(
    @RequestParam(value = "pageNumber", defaultValue = "0") String pageNumber,
    @RequestParam(value = "pageSize", defaultValue = "20") String pageSize,
    @RequestParam(value = "nomeFuncionario", required = false) String nomeFuncionario,
    @RequestParam(value = "nomeInstituicao", required = false) String nomeInstituicao,
    @RequestParam(value = "idInstituicao", required = false) Long idInstituicao,
    @RequestParam(value = "nomeSecccao", required = false) String nomeSecccao,
    @RequestParam(value = "idSeccao", required = false) Long idSeccao)
  {

      final var query = new GetResponsaveisQuery(pageNumber, pageSize, nomeFuncionario, nomeInstituicao, idInstituicao, nomeSecccao, idSeccao);

      return queryBus.handle(query);

  }

}