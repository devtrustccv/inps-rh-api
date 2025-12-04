/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME */

package cv.inps.rh.funcionario.interfaces.rest;

import cv.igrp.framework.core.domain.CommandBus;
import cv.igrp.framework.stereotype.IgrpController;
import cv.inps.rh.funcionario.application.commands.NovoPedidoDeclaracaoCommand;
import cv.inps.rh.funcionario.application.dto.PedidoDeclaracaoDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@IgrpController
@RestController
@RequestMapping(path = "api/v1/funcionarios")
@Tag(name = "Declaracao", description = "Emitir pedidos declaração")
public class DeclaracaoController {


  private final CommandBus commandBus;

  public DeclaracaoController(CommandBus commandBus) {

          this.commandBus = commandBus;
  }
   @PostMapping(
   value = "{funcionarioId}/declaracao"
  )
  @Operation(
    summary = "Novo pedido declaracao",
    description = "Novo pedido declaracao",
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

  public ResponseEntity<String> novoPedidoDeclaracao(@Valid @RequestBody PedidoDeclaracaoDTO novoPedidoDeclaracaoRequest
    , @PathVariable(value = "funcionarioId") String funcionarioId)
  {

      final var command = new NovoPedidoDeclaracaoCommand(novoPedidoDeclaracaoRequest, funcionarioId);

      return commandBus.send(command);

  }

}
