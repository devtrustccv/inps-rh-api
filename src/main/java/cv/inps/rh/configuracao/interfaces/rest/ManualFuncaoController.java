/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME */

package cv.inps.rh.configuracao.interfaces.rest;

import cv.igrp.framework.core.domain.CommandBus;
import cv.igrp.framework.core.domain.QueryBus;
import cv.igrp.framework.stereotype.IgrpController;
import cv.inps.rh.configuracao.application.commands.CreateManualFuncaoCommand;
import cv.inps.rh.configuracao.application.commands.UpdateManualFuncaoCommand;
import cv.inps.rh.configuracao.application.dto.ManualFuncaoRequestDTO;
import cv.inps.rh.configuracao.application.dto.ManualFuncaoResponseDTO;
import cv.inps.rh.configuracao.application.dto.WrapperListaManualFuncaoDTO;
import cv.inps.rh.configuracao.application.queries.GetListaManualFuncaoQuery;
import cv.inps.rh.configuracao.application.queries.GetManualFuncaoQuery;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@IgrpController
@RestController
@RequestMapping(path = "configuracao")
@Tag(
    name = "Configuracao",
    description = "gestao manual de funcao"
)
public class ManualFuncaoController {


  private final QueryBus queryBus;
  private final CommandBus commandBus;

  public ManualFuncaoController(QueryBus queryBus, CommandBus commandBus) {
          this.queryBus = queryBus;
          this.commandBus = commandBus;
  }
   @PostMapping(
   value = "avaliacao-desempenho/manual-funcao"
  )
  @Operation(
    summary = "Create manual funcao",
    description = "Create manual funcao",
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

   public ResponseEntity<Map<String, ?>> createManualFuncao(@Valid @RequestBody ManualFuncaoRequestDTO createManualFuncaoRequest
    )
  {

      final var command = new CreateManualFuncaoCommand(createManualFuncaoRequest);

      return commandBus.send(command);

  }

   @GetMapping(
   value = "avaliacao-desempenho/manual-funcao"
  )
  @Operation(
    summary = "Get lista manual funcao",
    description = "Get lista manual funcao",
    responses = {
      @ApiResponse(
          responseCode = "200",

          content = @Content(
              mediaType = "application/json",
              schema = @Schema(
                  implementation = WrapperListaManualFuncaoDTO.class,
                  type = "object")
          )
      )
    }
  )

   public ResponseEntity<WrapperListaManualFuncaoDTO> getListaManualFuncao(
    @RequestParam(value = "pageNumber", required = false, defaultValue = "0") String pageNumber,
    @RequestParam(value = "pageSize", required = false, defaultValue = "20") String pageSize,
    @RequestParam(value = "cargoId", required = false) Long cargoId,
    @RequestParam(value = "carrPccsId", required = false) Long carrPccsId,
    @RequestParam(value = "institId", required = false) Long institId,
    @RequestParam(value = "seccaoId", required = false) Long seccaoId,
    @RequestParam(value = "conteudo", required = false) String conteudo)
  {

      final var query = new GetListaManualFuncaoQuery(pageNumber, pageSize, cargoId, carrPccsId, institId, seccaoId, conteudo);

      return queryBus.handle(query);

  }

   @GetMapping(
   value = "avaliacao-desempenho/manual-funcao/{id}"
  )
  @Operation(
    summary = "Get manual funcao",
    description = "Get manual funcao",
    responses = {
      @ApiResponse(
          responseCode = "200",

          content = @Content(
              mediaType = "application/json",
              schema = @Schema(
                  implementation = ManualFuncaoResponseDTO.class,
                  type = "object")
          )
      )
    }
  )

   public ResponseEntity<ManualFuncaoResponseDTO> getManualFuncao(
       @PathVariable String id)
  {

      final var query = new GetManualFuncaoQuery(id);

      return queryBus.handle(query);

  }

   @PutMapping(
   value = "avaliacao-desempenho/manual-funcao/{id}"
  )
  @Operation(
    summary = "Update manual funcao",
    description = "Update manual funcao",
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

   public ResponseEntity<Map<String, ?>> updateManualFuncao(@Valid @RequestBody ManualFuncaoRequestDTO updateManualFuncaoRequest
       , @PathVariable String id)
  {

      final var command = new UpdateManualFuncaoCommand(updateManualFuncaoRequest, id);

      return commandBus.send(command);

  }

}
