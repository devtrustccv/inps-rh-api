package cv.inps.rh.missaoservico.interfaces.rest;

import cv.igrp.framework.core.domain.CommandBus;
import cv.igrp.framework.core.domain.QueryBus;
import cv.igrp.framework.stereotype.IgrpController;
import cv.inps.rh.missaoservico.application.commands.SaveConfiguracaoAjudaCustoCommand;
import cv.inps.rh.missaoservico.application.dto.ParamAjudaCustoDTO;
import cv.inps.rh.missaoservico.application.queries.GetConfiguracaoAjudaCustoQuery;
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
@RequestMapping(path = "api/v1/missao-servico/configuracao-ajuda-custo")
@Tag(
    name = "ConfiguracaoAjudaCusto",
    description = "parametrizacao da ajuda de custo por carreira/cargo"
)
public class ConfiguracaoAjudaCustoController {

  private final QueryBus queryBus;
  private final CommandBus commandBus;

  public ConfiguracaoAjudaCustoController(QueryBus queryBus, CommandBus commandBus) {
    this.queryBus = queryBus;
    this.commandBus = commandBus;
  }

  @GetMapping
  @Operation(
      summary = "Get configuracao ajuda custo",
      description = "Get configuracao ajuda custo",
      responses = {
          @ApiResponse(
              responseCode = "200",
              content = @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = ParamAjudaCustoDTO.class, type = "object")
              )
          )
      }
  )
  public ResponseEntity<List<ParamAjudaCustoDTO>> getConfiguracaoAjudaCusto() {
    return queryBus.handle(new GetConfiguracaoAjudaCustoQuery());
  }

  @PostMapping
  @Operation(
      summary = "Save configuracao ajuda custo",
      description = "Save configuracao ajuda custo",
      responses = {
          @ApiResponse(
              responseCode = "200",
              content = @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = String.class, type = "String")
              )
          )
      }
  )
  public ResponseEntity<String> saveConfiguracaoAjudaCusto(@Valid @RequestBody List<ParamAjudaCustoDTO> saveConfiguracaoAjudaCustoRequest) {
    return commandBus.send(new SaveConfiguracaoAjudaCustoCommand(saveConfiguracaoAjudaCustoRequest));
  }

}
