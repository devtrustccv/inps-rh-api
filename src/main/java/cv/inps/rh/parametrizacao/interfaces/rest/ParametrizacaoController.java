/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME. */

package cv.inps.rh.parametrizacao.interfaces.rest;

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
import cv.inps.rh.parametrizacao.application.queries.*;

import java.util.List;
import cv.inps.rh.parametrizacao.application.dto.DominioDTO;
import cv.inps.rh.parametrizacao.application.dto.ParametrizacaoDTO;

@IgrpController
@RestController
@RequestMapping(path = "api/v1/parametrizacao")
@Tag(name = "Parametrizacao", description = "Modulo parametrizacao")
public class ParametrizacaoController {

  
  private final QueryBus queryBus;

  public ParametrizacaoController(QueryBus queryBus) {
          this.queryBus = queryBus;
          
  }
   @GetMapping(
   value = "dominios"
  )
  @Operation(
    summary = "GET method to handle operations for getDominios",
    description = "GET method to handle operations for getDominios",
    responses = {
      @ApiResponse(
          responseCode = "200",
          description = "",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(
                  implementation = DominioDTO.class,
                  type = "object")
          )
      )
    }
  )
  
  public ResponseEntity<List<DominioDTO>> getDominios(
    @RequestParam(value = "dominio") String dominio)
  {

      final var query = new GetDominiosQuery(dominio);

      ResponseEntity<List<DominioDTO>> response = queryBus.handle(query);

      return response;
  }

   @GetMapping(
   value = "cargos/ativos"
  )
  @Operation(
    summary = "GET method to handle operations for getCargosAtivos",
    description = "GET method to handle operations for getCargosAtivos",
    responses = {
      @ApiResponse(
          responseCode = "200",
          description = "",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(
                  implementation = ParametrizacaoDTO.class,
                  type = "object")
          )
      )
    }
  )
  
  public ResponseEntity<List<ParametrizacaoDTO>> getCargosAtivos(
    )
  {

      final var query = new GetCargosAtivosQuery();

      ResponseEntity<List<ParametrizacaoDTO>> response = queryBus.handle(query);

      return response;
  }

}