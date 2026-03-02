/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME */

package cv.inps.rh.progressaopromocao.interfaces.rest;

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
import cv.inps.rh.progressaopromocao.application.queries.*;
import cv.igrp.framework.core.domain.CommandBus;
import cv.inps.rh.progressaopromocao.application.commands.*;
import cv.inps.rh.progressaopromocao.application.dto.ListaProgressaoPromocaoDTO;
import cv.inps.rh.progressaopromocao.application.dto.AnexarOrdemServicoRequestDTO;

@IgrpController
@RestController
@RequestMapping(path = "progressao-promocao")
@Tag(
    name = "Progressaopromocao",
    description = "Data related t progression and promotion"
)
public class ProgressaoPromocaoController {


  private final QueryBus queryBus;
  private final CommandBus commandBus;

  public ProgressaoPromocaoController(QueryBus queryBus, CommandBus commandBus) {
          this.queryBus = queryBus;
          this.commandBus = commandBus;
  }
   @GetMapping(
  )
  @Operation(
    summary = "Get lista progressa promocao",
    description = "Get lista progressa promocao",
    responses = {
      @ApiResponse(
          responseCode = "200",

          content = @Content(
              mediaType = "application/json",
              schema = @Schema(
                  implementation = ListaProgressaoPromocaoDTO.class,
                  type = "object")
          )
      )
    }
  )

  public ResponseEntity<ListaProgressaoPromocaoDTO> getListaProgressaPromocao(
    @RequestParam(value = "progressaoPromocao", required = false) String progressaoPromocao,
    @RequestParam(value = "colaborador", required = false) String colaborador,
    @RequestParam(value = "carreiraId", required = false) String carreiraId,
    @RequestParam(value = "dataDe", required = false) String dataDe,
    @RequestParam(value = "dataAte", required = false) String dataAte,
    @RequestParam(value = "page", required = false, defaultValue = "0") String page,
    @RequestParam(value = "size", required = false, defaultValue = "20") String size)
  {

      final var query = new GetListaProgressaPromocaoQuery(progressaoPromocao, colaborador, carreiraId, dataDe, dataAte, page, size);

      return queryBus.handle(query);

  }

   @GetMapping(
   value = "validacao"
  )
  @Operation(
    summary = "Get lista validacao progressa promocao",
    description = "Get lista validacao progressa promocao",
    responses = {
      @ApiResponse(
          responseCode = "200",

          content = @Content(
              mediaType = "application/json",
              schema = @Schema(
                  implementation = ListaProgressaoPromocaoDTO.class,
                  type = "object")
          )
      )
    }
  )

  public ResponseEntity<ListaProgressaoPromocaoDTO> getListaValidacaoProgressaPromocao(
    @RequestParam(value = "progressaoPromocao", required = false) String progressaoPromocao,
    @RequestParam(value = "colaborador", required = false) String colaborador,
    @RequestParam(value = "carreiraId", required = false) String carreiraId,
    @RequestParam(value = "dataDe", required = false) String dataDe,
    @RequestParam(value = "dataAte", required = false) String dataAte,
    @RequestParam(value = "page", required = false, defaultValue = "0") String page,
    @RequestParam(value = "size", required = false, defaultValue = "20") String size)
  {

      final var query = new GetListaValidacaoProgressaPromocaoQuery(progressaoPromocao, colaborador, carreiraId, dataDe, dataAte, page, size);

      return queryBus.handle(query);

  }

   @PostMapping(
   value = "ordem-servico"
  )
  @Operation(
    summary = "Anexar ordem servico",
    description = "Anexar ordem servico",
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

  public ResponseEntity<String> anexarOrdemServico(@Valid @RequestBody AnexarOrdemServicoRequestDTO anexarOrdemServicoRequest
    )
  {

      final var command = new AnexarOrdemServicoCommand(anexarOrdemServicoRequest);

      return commandBus.send(command);

  }

}
