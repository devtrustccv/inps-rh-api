/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME */

package cv.inps.rh.missaoservico.interfaces.rest;

import cv.inps.rh.shared.application.dto.SuccessResponseDTO;
import cv.igrp.framework.core.domain.CommandBus;
import cv.igrp.framework.core.domain.QueryBus;
import cv.igrp.framework.stereotype.IgrpController;
import cv.inps.rh.missaoservico.application.commands.*;
import cv.inps.rh.missaoservico.application.dto.*;
import cv.inps.rh.missaoservico.application.queries.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@IgrpController
@RestController
@RequestMapping(path = "api/v1/missao-servico/prestadores")
@Tag(
    name = "PrestadorServico",
    description = "gestao prestadores servico da missao"
)
public class PrestadorServicoController {


  private final QueryBus queryBus;
  private final CommandBus commandBus;

  public PrestadorServicoController(QueryBus queryBus, CommandBus commandBus) {
          this.queryBus = queryBus;
          this.commandBus = commandBus;
  }
   @PostMapping(
  )
  @Operation(
    summary = "Registar prestador servico",
    description = "Registar prestador servico",
    responses = {
      @ApiResponse(
          responseCode = "200",

          content = @Content(
              mediaType = "application/json",
              schema = @Schema(
                  implementation = SuccessResponseDTO.class,
                  type = "object")
          )
      )
    }
  )

   public ResponseEntity<SuccessResponseDTO> createPrestadorServico(@Valid @RequestBody PrestadorServicoRequestDTO createPrestadorServicoRequest
    )
  {

      final var command = new CreatePrestadorServicoCommand(createPrestadorServicoRequest);

      return commandBus.send(command);

  }

   @PutMapping(
   value = "{uuid}"
  )
  @Operation(
    summary = "Editar prestador servico",
    description = "Editar prestador servico",
    responses = {
      @ApiResponse(
          responseCode = "200",

          content = @Content(
              mediaType = "application/json",
              schema = @Schema(
                  implementation = SuccessResponseDTO.class,
                  type = "object")
          )
      )
    }
  )

   public ResponseEntity<SuccessResponseDTO> updatePrestadorServico(@Valid @RequestBody PrestadorServicoRequestDTO updatePrestadorServicoRequest
    , @PathVariable(value = "uuid") String uuid)
  {

      final var command = new UpdatePrestadorServicoCommand(updatePrestadorServicoRequest, uuid);

      return commandBus.send(command);

  }

   @GetMapping(
  )
  @Operation(
    summary = "Get lista prestador servico",
    description = "Get lista prestador servico",
    responses = {
      @ApiResponse(
          responseCode = "200",

          content = @Content(
              mediaType = "application/json",
              schema = @Schema(
                  implementation = WrapperListPrestadorServicoDTO.class,
                  type = "object")
          )
      )
    }
  )

   public ResponseEntity<WrapperListPrestadorServicoDTO> getListaPrestadorServico(
    @RequestParam(value = "nome", required = false) String nome,
    @RequestParam(value = "ilhaId", required = false) String ilhaId,
    @RequestParam(value = "estado", required = false) String estado,
    @RequestParam(value = "pageNumber", required = false, defaultValue = "0") String pageNumber,
    @RequestParam(value = "pageSize", required = false, defaultValue = "10") String pageSize)
  {

      final var query = new GetListaPrestadorServicoQuery(nome, ilhaId, estado, pageNumber, pageSize);

      return queryBus.handle(query);

  }

   @GetMapping(
   value = "{uuid}"
  )
  @Operation(
    summary = "Get prestador servico",
    description = "Get prestador servico",
    responses = {
      @ApiResponse(
          responseCode = "200",

          content = @Content(
              mediaType = "application/json",
              schema = @Schema(
                  implementation = PrestadorServicoResponseDTO.class,
                  type = "object")
          )
      )
    }
  )

   public ResponseEntity<PrestadorServicoResponseDTO> getPrestadorServico(
    @PathVariable(value = "uuid") String uuid)
  {

      final var query = new GetPrestadorServicoQuery(uuid);

      return queryBus.handle(query);

  }

   @GetMapping(
   value = "{uuid}/avaliacoes"
  )
  @Operation(
    summary = "Get avaliacoes prestador servico",
    description = "Get avaliacoes prestador servico",
    responses = {
      @ApiResponse(
          responseCode = "200",

          content = @Content(
              mediaType = "application/json",
              schema = @Schema(
                  implementation = PrestadorAvaliacaoResponseDTO.class,
                  type = "object")
          )
      )
    }
  )

   public ResponseEntity<List<PrestadorAvaliacaoResponseDTO>> getAvaliacoesPrestadorServico(
    @PathVariable(value = "uuid") String uuid)
  {

      final var query = new GetAvaliacoesPrestadorServicoQuery(uuid);

      return queryBus.handle(query);

  }

}
