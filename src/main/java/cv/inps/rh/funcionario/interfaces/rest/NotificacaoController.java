/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME */

package cv.inps.rh.funcionario.interfaces.rest;

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
import cv.inps.rh.funcionario.application.queries.*;
import cv.igrp.framework.core.domain.CommandBus;
import cv.inps.rh.funcionario.application.commands.*;
import cv.inps.rh.shared.application.dto.WrapperListaNotificacoesDTO;
import cv.inps.rh.shared.application.dto.NotificacaoInfoDTO;
import cv.inps.rh.shared.application.dto.NotificacaoEnviarRequestDTO;
import java.util.Map;

@IgrpController
@RestController
@RequestMapping(path = "api/v1/funcionarios")
@Tag(
    name = "Funcionario",
    description = "desc"
)
public class NotificacaoController {

  
  private final QueryBus queryBus;
  private final CommandBus commandBus;

  public NotificacaoController(QueryBus queryBus, CommandBus commandBus) {
          this.queryBus = queryBus;
          this.commandBus = commandBus;
  }
   @GetMapping(
   value = "notificacoes"
  )
  @Operation(
    summary = "Lista notificacoes",
    description = "Lista notificacoes",
    responses = {
      @ApiResponse(
          responseCode = "200",
          
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(
                  implementation = WrapperListaNotificacoesDTO.class,
                  type = "object")
          )
      )
    }
  )
  
  public ResponseEntity<WrapperListaNotificacoesDTO> listaNotificacoes(
    @RequestParam(value = "tipoNotificacao", required = false) String tipoNotificacao,
    @RequestParam(value = "dataEnvioDe", required = false) String dataEnvioDe,
    @RequestParam(value = "dataEnvioAte", required = false) String dataEnvioAte,
    @RequestParam(value = "estado", required = false) String estado,
    @RequestParam(value = "pageNumber", required = false, defaultValue = "0") String pageNumber,
    @RequestParam(value = "pageSize", required = false, defaultValue = "20") String pageSize)
  {

      final var query = new ListaNotificacoesQuery(tipoNotificacao, dataEnvioDe, dataEnvioAte, estado, pageNumber, pageSize);

      return queryBus.handle(query);

  }

   @GetMapping(
   value = "notificacoes/{id}"
  )
  @Operation(
    summary = "Detalhe notificacao",
    description = "Detalhe notificacao",
    responses = {
      @ApiResponse(
          responseCode = "200",
          
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(
                  implementation = NotificacaoInfoDTO.class,
                  type = "object")
          )
      )
    }
  )
  
  public ResponseEntity<NotificacaoInfoDTO> detalheNotificacao(
    @PathVariable(value = "id") String id)
  {

      final var query = new DetalheNotificacaoQuery(id);

      return queryBus.handle(query);

  }

   @PostMapping(
   value = "notificacoes/{id}/enviar"
  )
  @Operation(
    summary = "Enviar notificacao",
    description = "Enviar notificacao",
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
  
  public ResponseEntity<Map<String, ?>> enviarNotificacao(@Valid @RequestBody NotificacaoEnviarRequestDTO enviarNotificacaoRequest
    , @PathVariable(value = "id") String id)
  {

      final var command = new EnviarNotificacaoCommand(enviarNotificacaoRequest, id);

      return commandBus.send(command);

  }

}