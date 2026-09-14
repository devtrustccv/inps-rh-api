/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME */

package cv.inps.rh.missaoservico.interfaces.rest;

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

import java.util.Map;

@IgrpController
@RestController
@RequestMapping(path = "api/v1/missao-servico")
@Tag(
    name = "MissaoProcesso",
    description = "etapas dos processos da missao servico"
)
public class MissaoProcessoController {


  private final QueryBus queryBus;
  private final CommandBus commandBus;

  public MissaoProcessoController(QueryBus queryBus, CommandBus commandBus) {
          this.queryBus = queryBus;
          this.commandBus = commandBus;
  }
   @GetMapping(
   value = "{uuid}/processos/{tipoProcesso}/prestadores"
  )
  @Operation(
    summary = "Get prestadores do processo",
    description = "Get prestadores do processo",
    responses = {
      @ApiResponse(
          responseCode = "200",

          content = @Content(
              mediaType = "application/json",
              schema = @Schema(
                  implementation = ProcessoPrestadoresResponseDTO.class,
                  type = "object")
          )
      )
    }
  )

   public ResponseEntity<ProcessoPrestadoresResponseDTO> getProcessoPrestadores(
    @PathVariable(value = "uuid") String uuid,
    @PathVariable(value = "tipoProcesso") String tipoProcesso)
  {

      final var query = new GetProcessoPrestadoresQuery(uuid, tipoProcesso);

      return queryBus.handle(query);

  }

   @PutMapping(
   value = "{uuid}/processos/{tipoProcesso}/prestadores"
  )
  @Operation(
    summary = "Save prestadores do processo",
    description = "Save prestadores do processo",
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

   public ResponseEntity<Map<String, ?>> saveProcessoPrestadores(@Valid @RequestBody ProcessoPrestadoresRequestDTO saveProcessoPrestadoresRequest
    , @PathVariable(value = "uuid") String uuid, @PathVariable(value = "tipoProcesso") String tipoProcesso)
  {

      final var command = new SaveProcessoPrestadoresCommand(saveProcessoPrestadoresRequest, uuid, tipoProcesso);

      return commandBus.send(command);

  }

}
