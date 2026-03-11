/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME */

package cv.inps.rh.avaliacao.interfaces.rest;

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
import cv.inps.rh.avaliacao.application.queries.*;
import cv.igrp.framework.core.domain.CommandBus;
import cv.inps.rh.avaliacao.application.commands.*;
import cv.inps.rh.avaliacao.application.dto.AvaliacaoInicializarRequestDTO;
import java.util.Map;
import cv.inps.rh.avaliacao.application.dto.WrapperListaAvaliacaoDTO;

@IgrpController
@RestController
@RequestMapping(path = "avaliacao-desempenho/avaliacoes")
@Tag(
    name = "Avaliacao",
    description = "gest avaliacao"
)
public class AvaliacaoController {

  
  private final QueryBus queryBus;
  private final CommandBus commandBus;

  public AvaliacaoController(QueryBus queryBus, CommandBus commandBus) {
          this.queryBus = queryBus;
          this.commandBus = commandBus;
  }
   @PostMapping(
  )
  @Operation(
    summary = "Init avaliacao",
    description = "Init avaliacao",
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
  
  public ResponseEntity<Map<String, ?>> initAvaliacao(@Valid @RequestBody AvaliacaoInicializarRequestDTO initAvaliacaoRequest
    )
  {

      final var command = new InitAvaliacaoCommand(initAvaliacaoRequest);

      return commandBus.send(command);

  }

   @GetMapping(
  )
  @Operation(
    summary = "Get lista definicao objectivos",
    description = "Get lista definicao objectivos",
    responses = {
      @ApiResponse(
          responseCode = "200",
          
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(
                  implementation = WrapperListaAvaliacaoDTO.class,
                  type = "object")
          )
      )
    }
  )
  
  public ResponseEntity<WrapperListaAvaliacaoDTO> getListaDefinicaoObjectivos(
    @RequestParam(value = "ano", required = false) String ano,
    @RequestParam(value = "semestre", required = false) String semestre,
    @RequestParam(value = "estado", required = false) String estado,
    @RequestParam(value = "institId", required = false) Long institId,
    @RequestParam(value = "cargoId", required = false) Long cargoId,
    @RequestParam(value = "funId", required = false) String funId,
    @RequestParam(value = "pageNumber", required = false, defaultValue = "0") String pageNumber,
    @RequestParam(value = "pageSize", required = false, defaultValue = "20") String pageSize)
  {

      final var query = new GetListaDefinicaoObjectivosQuery(ano, semestre, estado, institId, cargoId, funId, pageNumber, pageSize);

      return queryBus.handle(query);

  }

}