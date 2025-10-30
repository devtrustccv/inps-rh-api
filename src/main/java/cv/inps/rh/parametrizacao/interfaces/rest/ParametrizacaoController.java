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

   @GetMapping(
   value = "carreiras/ativos"
  )
  @Operation(
    summary = "GET method to handle operations for getCarreirasAtivos",
    description = "GET method to handle operations for getCarreirasAtivos",
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
  
  public ResponseEntity<List<ParametrizacaoDTO>> getCarreirasAtivos(
    )
  {

      final var query = new GetCarreirasAtivosQuery();

      ResponseEntity<List<ParametrizacaoDTO>> response = queryBus.handle(query);

      return response;
  }

   @GetMapping(
   value = "categorias/ativos"
  )
  @Operation(
    summary = "GET method to handle operations for getCategoriasAtivos",
    description = "GET method to handle operations for getCategoriasAtivos",
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
  
  public ResponseEntity<List<ParametrizacaoDTO>> getCategoriasAtivos(
    )
  {

      final var query = new GetCategoriasAtivosQuery();

      ResponseEntity<List<ParametrizacaoDTO>> response = queryBus.handle(query);

      return response;
  }

   @GetMapping(
   value = "contratos/ativos"
  )
  @Operation(
    summary = "GET method to handle operations for getParamContratosAtivos",
    description = "GET method to handle operations for getParamContratosAtivos",
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
  
  public ResponseEntity<List<ParametrizacaoDTO>> getParamContratosAtivos(
    )
  {

      final var query = new GetParamContratosAtivosQuery();

      ResponseEntity<List<ParametrizacaoDTO>> response = queryBus.handle(query);

      return response;
  }

   @GetMapping(
   value = "escalao/ativos"
  )
  @Operation(
    summary = "GET method to handle operations for getEscaloesAtivos",
    description = "GET method to handle operations for getEscaloesAtivos",
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
  
  public ResponseEntity<List<ParametrizacaoDTO>> getEscaloesAtivos(
    )
  {

      final var query = new GetEscaloesAtivosQuery();

      ResponseEntity<List<ParametrizacaoDTO>> response = queryBus.handle(query);

      return response;
  }

   @GetMapping(
   value = "local-trabalho/ativos"
  )
  @Operation(
    summary = "GET method to handle operations for getLocalTrabalhoAtivos",
    description = "GET method to handle operations for getLocalTrabalhoAtivos",
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
  
  public ResponseEntity<List<ParametrizacaoDTO>> getLocalTrabalhoAtivos(
    )
  {

      final var query = new GetLocalTrabalhoAtivosQuery();

      ResponseEntity<List<ParametrizacaoDTO>> response = queryBus.handle(query);

      return response;
  }

   @GetMapping(
   value = "situacao-laboral/ativos"
  )
  @Operation(
    summary = "GET method to handle operations for getParamSituacaoLaboralAtivo",
    description = "GET method to handle operations for getParamSituacaoLaboralAtivo",
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
  
  public ResponseEntity<List<ParametrizacaoDTO>> getParamSituacaoLaboralAtivo(
    )
  {

      final var query = new GetParamSituacaoLaboralAtivoQuery();

      ResponseEntity<List<ParametrizacaoDTO>> response = queryBus.handle(query);

      return response;
  }

   @GetMapping(
   value = "vinculos/ativos"
  )
  @Operation(
    summary = "GET method to handle operations for getVinculosAtivos",
    description = "GET method to handle operations for getVinculosAtivos",
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
  
  public ResponseEntity<List<ParametrizacaoDTO>> getVinculosAtivos(
    )
  {

      final var query = new GetVinculosAtivosQuery();

      ResponseEntity<List<ParametrizacaoDTO>> response = queryBus.handle(query);

      return response;
  }

   @GetMapping(
   value = "seccoes/ativos"
  )
  @Operation(
    summary = "GET method to handle operations for getSeccoesAtivos",
    description = "GET method to handle operations for getSeccoesAtivos",
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
  
  public ResponseEntity<List<ParametrizacaoDTO>> getSeccoesAtivos(
    )
  {

      final var query = new GetSeccoesAtivosQuery();

      ResponseEntity<List<ParametrizacaoDTO>> response = queryBus.handle(query);

      return response;
  }

   @GetMapping(
   value = "tipo-documento/ativos"
  )
  @Operation(
    summary = "GET method to handle operations for getTiposDocumentoAtivos",
    description = "GET method to handle operations for getTiposDocumentoAtivos",
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
  
  public ResponseEntity<List<ParametrizacaoDTO>> getTiposDocumentoAtivos(
    )
  {

      final var query = new GetTiposDocumentoAtivosQuery();

      ResponseEntity<List<ParametrizacaoDTO>> response = queryBus.handle(query);

      return response;
  }

}