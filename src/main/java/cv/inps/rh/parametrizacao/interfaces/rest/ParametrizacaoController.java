/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME */

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
import cv.inps.rh.parametrizacao.application.dto.EscalaoDTO;
import cv.inps.rh.parametrizacao.application.dto.LocalTrabalhoDTO;
import cv.inps.rh.parametrizacao.application.dto.VinculoDTO;
import cv.inps.rh.parametrizacao.application.dto.TipoDocumentoDTO;

@IgrpController
@RestController
@RequestMapping(path = "api/v1/parametrizacao")
@Tag(
    name = "Parametrizacao",
    description = "Modulo parametrizacao"
)
public class ParametrizacaoController {


  private final QueryBus queryBus;

  public ParametrizacaoController(QueryBus queryBus) {
          this.queryBus = queryBus;

  }
   @GetMapping(
   value = "dominios"
  )
  @Operation(
    summary = "Get dominios",
    description = "Get dominios",
    responses = {
      @ApiResponse(
          responseCode = "200",

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
    @RequestParam(value = "dominio") String dominio,
    @RequestParam(value = "referencia", required = false) String referencia)
  {

      final var query = new GetDominiosQuery(dominio, referencia);

      return queryBus.handle(query);

  }

   @GetMapping(
   value = "cargos/ativos"
  )
  @Operation(
    summary = "Get cargos ativos",
    description = "Get cargos ativos",
    responses = {
      @ApiResponse(
          responseCode = "200",

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

      return queryBus.handle(query);

  }

   @GetMapping(
   value = "carreiras/ativos"
  )
  @Operation(
    summary = "Get carreiras ativos",
    description = "Get carreiras ativos",
    responses = {
      @ApiResponse(
          responseCode = "200",

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

      return queryBus.handle(query);

  }

   @GetMapping(
   value = "categorias/ativos"
  )
  @Operation(
    summary = "Get categorias ativos",
    description = "Get categorias ativos",
    responses = {
      @ApiResponse(
          responseCode = "200",

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

      return queryBus.handle(query);

  }

   @GetMapping(
   value = "contratos/ativos"
  )
  @Operation(
    summary = "Get param contratos ativos",
    description = "Get param contratos ativos",
    responses = {
      @ApiResponse(
          responseCode = "200",

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

      return queryBus.handle(query);

  }

   @GetMapping(
   value = "escalao/ativos"
  )
  @Operation(
    summary = "Get escaloes ativos",
    description = "Get escaloes ativos",
    responses = {
      @ApiResponse(
          responseCode = "200",

          content = @Content(
              mediaType = "application/json",
              schema = @Schema(
                  implementation = EscalaoDTO.class,
                  type = "object")
          )
      )
    }
  )

  public ResponseEntity<List<EscalaoDTO>> getEscaloesAtivos(
    )
  {

      final var query = new GetEscaloesAtivosQuery();

      return queryBus.handle(query);

  }

   @GetMapping(
   value = "local-trabalho/ativos"
  )
  @Operation(
    summary = "Get local trabalho ativos",
    description = "Get local trabalho ativos",
    responses = {
      @ApiResponse(
          responseCode = "200",

          content = @Content(
              mediaType = "application/json",
              schema = @Schema(
                  implementation = LocalTrabalhoDTO.class,
                  type = "object")
          )
      )
    }
  )

  public ResponseEntity<List<LocalTrabalhoDTO>> getLocalTrabalhoAtivos(
    )
  {

      final var query = new GetLocalTrabalhoAtivosQuery();

      return queryBus.handle(query);

  }

   @GetMapping(
   value = "vinculos/ativos"
  )
  @Operation(
    summary = "Get vinculos ativos",
    description = "Get vinculos ativos",
    responses = {
      @ApiResponse(
          responseCode = "200",

          content = @Content(
              mediaType = "application/json",
              schema = @Schema(
                  implementation = VinculoDTO.class,
                  type = "object")
          )
      )
    }
  )

  public ResponseEntity<List<VinculoDTO>> getVinculosAtivos(
    @RequestParam(value = "paramContratoId", required = false) Long paramContratoId)
  {

      final var query = new GetVinculosAtivosQuery(paramContratoId);

      return queryBus.handle(query);

  }

   @GetMapping(
   value = "seccoes/ativos"
  )
  @Operation(
    summary = "Get seccoes ativos",
    description = "Get seccoes ativos",
    responses = {
      @ApiResponse(
          responseCode = "200",

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
    @RequestParam(value = "institId") Long institId)
  {

      final var query = new GetSeccoesAtivosQuery(institId);

      return queryBus.handle(query);

  }

   @GetMapping(
   value = "tipo-documento/ativos"
  )
  @Operation(
    summary = "Get tipos documento ativos",
    description = "Get tipos documento ativos",
    responses = {
      @ApiResponse(
          responseCode = "200",

          content = @Content(
              mediaType = "application/json",
              schema = @Schema(
                  implementation = TipoDocumentoDTO.class,
                  type = "object")
          )
      )
    }
  )

  public ResponseEntity<List<TipoDocumentoDTO>> getTiposDocumentoAtivos(
    @RequestParam(value = "referencia", required = false) String referencia)
  {

      final var query = new GetTiposDocumentoAtivosQuery(referencia);

      return queryBus.handle(query);

  }

   @GetMapping(
   value = "param-situacoes/motivos/ativos"
  )
  @Operation(
    summary = "Get param situacao detalhe ativo",
    description = "Get param situacao detalhe ativo",
    responses = {
      @ApiResponse(
          responseCode = "200",

          content = @Content(
              mediaType = "application/json",
              schema = @Schema(
                  implementation = ParametrizacaoDTO.class,
                  type = "object")
          )
      )
    }
  )

  public ResponseEntity<List<ParametrizacaoDTO>> getParamSituacaoDetalheAtivo(
    @RequestParam(value = "situacaoLaboralId") Long situacaoLaboralId)
  {

      final var query = new GetParamSituacaoDetalheAtivoQuery(situacaoLaboralId);

      return queryBus.handle(query);

  }

   @GetMapping(
   value = "param-situacoes/ativos"
  )
  @Operation(
    summary = "Get param situacoes ativo",
    description = "Get param situacoes ativo",
    responses = {
      @ApiResponse(
          responseCode = "200",

          content = @Content(
              mediaType = "application/json",
              schema = @Schema(
                  implementation = ParametrizacaoDTO.class,
                  type = "object")
          )
      )
    }
  )

  public ResponseEntity<List<ParametrizacaoDTO>> getParamSituacoesAtivo(
    @RequestParam(value = "flgSituacaoLaboral", required = false) Integer flgSituacaoLaboral,
    @RequestParam(value = "flgAusencia", required = false) String flgAusencia,
    @RequestParam(value = "tipoAusencia", required = false) String tipoAusencia,
    @RequestParam(value = "tipoFalta", required = false) String tipoFalta)
  {

      final var query = new GetParamSituacoesAtivoQuery(flgSituacaoLaboral, flgAusencia, tipoAusencia, tipoFalta);

      return queryBus.handle(query);

  }

   @GetMapping(
   value = "vinculos/situacoes-laborais/ativos"
  )
  @Operation(
    summary = "Get param situacoes laborais by vinculo",
    description = "Get param situacoes laborais by vinculo",
    responses = {
      @ApiResponse(
          responseCode = "200",

          content = @Content(
              mediaType = "application/json",
              schema = @Schema(
                  implementation = ParametrizacaoDTO.class,
                  type = "object")
          )
      )
    }
  )

  public ResponseEntity<List<ParametrizacaoDTO>> getParamSituacoesLaboraisByVinculo(
    @RequestParam(value = "vinculoId") Long vinculoId,
    @RequestParam(value = "flgEstadoContrato", required = false) String flgEstadoContrato)
  {

      final var query = new GetParamSituacoesLaboraisByVinculoQuery(vinculoId, flgEstadoContrato);

      return queryBus.handle(query);

  }

   @GetMapping(
   value = "/ups"
  )
  @Operation(
    summary = "Get ups",
    description = "Get ups",
    responses = {
      @ApiResponse(
          responseCode = "200",

          content = @Content(
              mediaType = "application/json",
              schema = @Schema(
                  implementation = ParametrizacaoDTO.class,
                  type = "object")
          )
      )
    }
  )

  public ResponseEntity<List<ParametrizacaoDTO>> getUps(
    )
  {

      final var query = new GetUpsQuery();

      return queryBus.handle(query);

  }

}
