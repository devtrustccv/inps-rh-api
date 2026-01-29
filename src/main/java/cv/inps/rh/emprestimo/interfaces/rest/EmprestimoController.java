/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME */

package cv.inps.rh.emprestimo.interfaces.rest;

import cv.igrp.framework.core.domain.CommandBus;
import cv.igrp.framework.core.domain.QueryBus;
import cv.igrp.framework.stereotype.IgrpController;
import cv.inps.rh.emprestimo.application.commands.SaveConfiguracaoInfoEmprestimoCommand;
import cv.inps.rh.emprestimo.application.dto.InformacaoEmprestimoRequestDTO;
import cv.inps.rh.emprestimo.application.queries.GetConfiguracaoEmprestimoQuery;
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
@RequestMapping(path = "emprestimo")
@Tag(
    name = "Emprestimo",
    description = "Módulo Empréstimo"
)
public class EmprestimoController {


  private final QueryBus queryBus;
  private final CommandBus commandBus;

  public EmprestimoController(QueryBus queryBus, CommandBus commandBus) {
          this.queryBus = queryBus;
          this.commandBus = commandBus;
  }
   @PostMapping(
   value = "info-emprestimo"
  )
  @Operation(
    summary = "Save configuracao info emprestimo",
    description = "Save configuracao info emprestimo",
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

  public ResponseEntity<String> saveConfiguracaoInfoEmprestimo(@Valid @RequestBody List<InformacaoEmprestimoRequestDTO> saveConfiguracaoInfoEmprestimoRequest
    )
  {

      final var command = new SaveConfiguracaoInfoEmprestimoCommand(saveConfiguracaoInfoEmprestimoRequest);

      return commandBus.send(command);

  }

   @GetMapping(
   value = "info-emprestimo"
  )
  @Operation(
    summary = "Get configuracao emprestimo",
    description = "Get configuracao emprestimo",
    responses = {
      @ApiResponse(
          responseCode = "200",

          content = @Content(
              mediaType = "application/json",
              schema = @Schema(
                  implementation = InformacaoEmprestimoRequestDTO.class,
                  type = "object")
          )
      )
    }
  )

  public ResponseEntity<List<InformacaoEmprestimoRequestDTO>> getConfiguracaoEmprestimo(
    )
  {

      final var query = new GetConfiguracaoEmprestimoQuery();

      return queryBus.handle(query);

  }

}
