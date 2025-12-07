/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME */

package cv.inps.rh.processamento.interfaces.rest;

import cv.igrp.framework.core.domain.CommandBus;
import cv.igrp.framework.stereotype.IgrpController;
import cv.inps.rh.processamento.application.commands.RemoverFuncionariosProcessamentoSalarialCommand;
import cv.inps.rh.processamento.application.dto.MarcarNaoProcessadoRequestDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@IgrpController
@RestController
@RequestMapping(path = "processamento-salarial")
@Tag(name = "ProcessoSalarial", description = "Processamento Salarial")
public class ProcessoSalarialController {


  private final CommandBus commandBus;

  public ProcessoSalarialController(CommandBus commandBus) {

          this.commandBus = commandBus;
  }
   @PostMapping(
   value = "/folha/funcionarios/excluir"
  )
  @Operation(
    summary = "Remover funcionarios processamento salarial",
    description = "Remover funcionarios processamento salarial",
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

  public ResponseEntity<String> removerFuncionariosProcessamentoSalarial(@Valid @RequestBody MarcarNaoProcessadoRequestDTO removerFuncionariosProcessamentoSalarialRequest
    )
  {

      final var command = new RemoverFuncionariosProcessamentoSalarialCommand(removerFuncionariosProcessamentoSalarialRequest);

      return commandBus.send(command);

  }

}
