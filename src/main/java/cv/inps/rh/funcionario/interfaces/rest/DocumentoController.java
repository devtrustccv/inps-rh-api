/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME. */

package cv.inps.rh.funcionario.interfaces.rest;

import cv.igrp.framework.core.domain.CommandBus;
import cv.igrp.framework.stereotype.IgrpController;
import cv.inps.rh.funcionario.application.commands.SaveOrdemServicoCommand;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@IgrpController
@RestController
@RequestMapping(path = "documento")
@Tag(name = "Documento", description = "Guardar documentos")
public class DocumentoController {


  private final CommandBus commandBus;

  public DocumentoController(CommandBus commandBus) {

          this.commandBus = commandBus;
  }
   @PostMapping(
   value = "{funcionarioId}"
  )
  @Operation(
    summary = "Save ordem servico",
    description = "Save ordem servico",
    responses = {
      @ApiResponse(
          responseCode = "200",
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

  public ResponseEntity<String> saveOrdemServico(
    @RequestParam(value = "ordemServico") MultipartFile ordemServico, @PathVariable(value = "funcionarioId") String funcionarioId)
  {

      final var command = new SaveOrdemServicoCommand(ordemServico, funcionarioId);

      return commandBus.send(command);

  }

}
