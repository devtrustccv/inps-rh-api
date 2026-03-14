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
import cv.inps.rh.configuracao.application.dto.ComponenteAvaliacaoRequestDTO;
import java.util.Map;
import cv.inps.rh.configuracao.application.dto.ComponenteAvaliacaoResponseDTO;
import cv.inps.rh.configuracao.application.dto.WrapperListComponenteAvaliacaoDTO;

@IgrpController
@RestController
@RequestMapping(path = "configuracao")
@Tag(
    name = "Configuracao",
    description = "gest componente avaliacao"
)
public class ComponenteAvaliacaoController {

  
  private final QueryBus queryBus;
  private final CommandBus commandBus;

  public ComponenteAvaliacaoController(QueryBus queryBus, CommandBus commandBus) {
          this.queryBus = queryBus;
          this.commandBus = commandBus;
  }
   @PostMapping(
   value = "avaliacao-desempenho/componentes"
  )
  @Operation(
    summary = "Create componentes avaliacao",
    description = "Create componentes avaliacao",
    responses = {
      @ApiResponse(
          responseCode = "201",
          
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(
                  implementation = String.class,
                  type = "String")
          )
      )
    }
  )
  
  public ResponseEntity<Map<String, ?>> createComponentesAvaliacao(@Valid @RequestBody ComponenteAvaliacaoRequestDTO createComponentesAvaliacaoRequest
    )
  {

      final var command = new CreateComponentesAvaliacaoCommand(createComponentesAvaliacaoRequest);

      return commandBus.send(command);

  }

   @GetMapping(
   value = "avaliacao-desempenho/componentes/{id}"
  )
  @Operation(
    summary = "Get componente avaliacao",
    description = "Get componente avaliacao",
    responses = {
      @ApiResponse(
          responseCode = "200",
          
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(
                  implementation = ComponenteAvaliacaoResponseDTO.class,
                  type = "object")
          )
      )
    }
  )
  
  public ResponseEntity<ComponenteAvaliacaoResponseDTO> getComponenteAvaliacao(
    @PathVariable(value = "id") String id)
  {

      final var query = new GetComponenteAvaliacaoQuery(id);

      return queryBus.handle(query);

  }

   @GetMapping(
   value = "avaliacao-desempenho/componentes"
  )
  @Operation(
    summary = "Get lista componentes avaliacao",
    description = "Get lista componentes avaliacao",
    responses = {
      @ApiResponse(
          responseCode = "200",
          
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(
                  implementation = WrapperListComponenteAvaliacaoDTO.class,
                  type = "object")
          )
      )
    }
  )
  
  public ResponseEntity<WrapperListComponenteAvaliacaoDTO> getListaComponentesAvaliacao(
    @RequestParam(value = "pageNumber", required = false, defaultValue = "0") String pageNumber,
    @RequestParam(value = "pageSize", required = false, defaultValue = "20") String pageSize)
  {

      final var query = new GetListaComponentesAvaliacaoQuery(pageNumber, pageSize);

      return queryBus.handle(query);

  }

   @PutMapping(
   value = "avaliacao-desempenho/componentes/{id}"
  )
  @Operation(
    summary = "Update componente avaliacao",
    description = "Update componente avaliacao",
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
  
  public ResponseEntity<Map<String, ?>> updateComponenteAvaliacao(@Valid @RequestBody ComponenteAvaliacaoRequestDTO updateComponenteAvaliacaoRequest
    , @PathVariable(value = "id") String id)
  {

      final var command = new UpdateComponenteAvaliacaoCommand(updateComponenteAvaliacaoRequest, id);

      return commandBus.send(command);

  }

}