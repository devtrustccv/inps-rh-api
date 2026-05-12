/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME */

package cv.inps.rh.funcionario.interfaces.rest;

import cv.igrp.framework.core.domain.CommandBus;
import cv.igrp.framework.core.domain.QueryBus;
import cv.igrp.framework.stereotype.IgrpController;
import cv.inps.rh.funcionario.application.commands.AnexarDocOrdemServicoCommand;
import cv.inps.rh.funcionario.application.dto.WrapperListOrdemServicoDTO;
import cv.inps.rh.funcionario.application.dto.WrapperListReciboDTO;
import cv.inps.rh.funcionario.application.queries.GerarOrdemServicoPdfQuery;
import cv.inps.rh.funcionario.application.queries.GetListOrdemServicoQuery;
import cv.inps.rh.funcionario.application.queries.GetListRecibosQuery;
import cv.inps.rh.funcionario.application.queries.GetOrdemServicoQuery;
import cv.inps.rh.funcionario.application.queries.GetUrlTemporarioQuery;
import cv.inps.rh.shared.application.dto.AnexoReqDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;



@IgrpController
@RestController
@RequestMapping(path = "documento")
@Tag(
    name = "Funcionario",
    description = "Gerir documentos"
)
public class DocumentoController {


  private final QueryBus queryBus;
  private final CommandBus commandBus;

  public DocumentoController(QueryBus queryBus, CommandBus commandBus) {
    this.queryBus = queryBus;
    this.commandBus = commandBus;
  }

  @GetMapping(
   value = "{funcionarioId}"
  )
  @Operation(
      summary = "Get ordem servico",
      description = "Get ordem servico",
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

  public ResponseEntity<String> getOrdemServico(
      @PathVariable(value = "funcionarioId") String funcionarioId)
  {

    final var query = new GetOrdemServicoQuery(funcionarioId);

    return queryBus.handle(query);

  }

  @GetMapping(
      value = "url-temporario"
  )
  @Operation(
      summary = "Get url temporario",
      description = "Get url temporario",
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

  public ResponseEntity<String> getUrlTemporario(
  ) {

    final var query = new GetUrlTemporarioQuery();

    return queryBus.handle(query);

  }

  @GetMapping(
   value = "recibos"
  )
  @Operation(
    summary = "Get list recibos",
    description = "Get list recibos",
    responses = {
      @ApiResponse(
          responseCode = "200",

          content = @Content(
              mediaType = "application/json",
              schema = @Schema(
                  implementation = WrapperListReciboDTO.class,
                  type = "object")
          )
      )
    }
  )

  public ResponseEntity<WrapperListReciboDTO> getListRecibos(
    @RequestParam(value = "idFuncionario") String idFuncionario,
    @RequestParam(value = "pageSize", defaultValue = "20") String pageSize,
    @RequestParam(value = "pageNumber", defaultValue = "0") String pageNumber,
    @RequestParam(value = "dataInicio", required = false) String dataInicio,
    @RequestParam(value = "dataFim", required = false) String dataFim)
  {

    final var query = new GetListRecibosQuery(idFuncionario, pageSize, pageNumber, dataInicio, dataFim);

    return queryBus.handle(query);

  }

  @GetMapping(
    value = "lista-os/{funcionarioUuid}"
  )
  @Operation(
    summary = "Get list ordem servico",
    description = "Get list ordem servico",
    responses = {
      @ApiResponse(
          responseCode = "200",

          content = @Content(
              mediaType = "application/json",
              schema = @Schema(
                  implementation = WrapperListOrdemServicoDTO.class,
                  type = "object")
          )
      )
    }
  )

  public ResponseEntity<WrapperListOrdemServicoDTO> getListOrdemServico(
    @PathVariable(value = "funcionarioUuid") String funcionarioUuid,
    @RequestParam(value = "pageSize", defaultValue = "20") String pageSize,
    @RequestParam(value = "pageNumber", defaultValue = "0") String pageNumber)
  {

    final var query = new GetListOrdemServicoQuery(funcionarioUuid, pageSize, pageNumber);

    return queryBus.handle(query);

  }

  @GetMapping(
    value = "gerar-os/{osUuid}"
  )
  @Operation(
    summary = "Gerar ordem servico PDF",
    description = "Gerar ordem servico PDF",
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

  public ResponseEntity<String> gerarOrdemServicoPdf(
      @PathVariable(value = "osUuid") String osUuid)
  {

    final var query = new GerarOrdemServicoPdfQuery(osUuid);

    return queryBus.handle(query);

  }

  @PostMapping(
    value = "ordem-servico/{osUuid}/anexar"
  )
  @Operation(
    summary = "Anexar documento a ordem servico",
    description = "Anexar documento a ordem servico",
    responses = {
      @ApiResponse(
          responseCode = "200",

          content = @Content(
              mediaType = "application/json",
              schema = @Schema(
                  type = "object")
          )
      )
    }
  )

  public ResponseEntity<Map<String, ?>> anexarDocOrdemServico(
      @PathVariable(value = "osUuid") String osUuid,
      @RequestBody AnexoReqDTO anexo)
  {

    final var command = new AnexarDocOrdemServicoCommand(osUuid, anexo);

    return commandBus.send(command);

  }

}
