/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME */

package cv.inps.rh.processamento.interfaces.rest;

import cv.igrp.framework.core.domain.CommandBus;
import cv.igrp.framework.core.domain.QueryBus;
import cv.igrp.framework.stereotype.IgrpController;
import cv.inps.rh.processamento.application.commands.*;
import cv.inps.rh.processamento.application.dto.DetalheXmlRequestDTO;
import cv.inps.rh.processamento.application.dto.DetalhesFosXmlDTO;
import cv.inps.rh.processamento.application.dto.ListaFosDTO;
import cv.inps.rh.processamento.application.queries.GetDetalheFosXmlQuery;
import cv.inps.rh.processamento.application.queries.GetListaFosQuery;
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
@RequestMapping(path = "fos")
@Tag(
    name = "Processamento",
    description = "Operações Fos"
)
public class FosController {


  private final QueryBus queryBus;
  private final CommandBus commandBus;

  public FosController(QueryBus queryBus, CommandBus commandBus) {
    this.queryBus = queryBus;
    this.commandBus = commandBus;
  }

  @GetMapping(
  )
  @Operation(
      summary = "Get lista fos",
      description = "Get lista fos",
      responses = {
          @ApiResponse(
              responseCode = "200",

              content = @Content(
                  mediaType = "application/json",
                  schema = @Schema(
                      implementation = ListaFosDTO.class,
                      type = "object")
              )
          )
      }
  )

  public ResponseEntity<ListaFosDTO> getListaFos(
      @RequestParam(value = "dataInicio", required = false) String dataInicio,
      @RequestParam(value = "dataFim", required = false) String dataFim,
      @RequestParam(value = "page", required = false, defaultValue = "0") String page,
      @RequestParam(value = "size", required = false, defaultValue = "20") String size) {

    final var query = new GetListaFosQuery(dataInicio, dataFim, page, size);

    return queryBus.handle(query);

  }

  @PostMapping(
      value = "novo-segurado"
  )
  @Operation(
      summary = "Novo segurado",
      description = "Novo segurado",
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

  public ResponseEntity<String> novoSegurado(
      @RequestParam(value = "ano") Integer ano,
      @RequestParam(value = "mes") Integer mes) {

    final var command = new NovoSeguradoCommand(ano, mes);

    return commandBus.send(command);

  }

  @PostMapping(
      value = "remover-fos"
  )
  @Operation(
      summary = "Remover detalhe fos",
      description = "Remover detalhe fos",
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

  public ResponseEntity<String> removerDetalheFos(
      @RequestParam(value = "detalheFosId") Long detalheFosId) {

    final var command = new RemoverDetalheFosCommand(detalheFosId);

    return commandBus.send(command);

  }

  @PostMapping(
      value = "restaurar"
  )
  @Operation(
      summary = "Restaurar fos",
      description = "Restaurar fos",
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

  public ResponseEntity<String> restaurarFos(
      @RequestParam(value = "referenceMonth") String referenceMonth,
      @RequestParam(value = "fosId") Long fosId) {

    final var command = new RestaurarFosCommand(referenceMonth, fosId);

    return commandBus.send(command);

  }

  @GetMapping(
      value = "detalhe-fos"
  )
  @Operation(
      summary = "Get detalhe fos xml",
      description = "Get detalhe fos xml",
      responses = {
          @ApiResponse(
              responseCode = "200",

              content = @Content(
                  mediaType = "application/json",
                  schema = @Schema(
                      implementation = DetalhesFosXmlDTO.class,
                      type = "object")
              )
          )
      }
  )

  public ResponseEntity<DetalhesFosXmlDTO> getDetalheFosXml(
      @RequestParam(value = "fosId") Long fosId,
      @RequestParam(value = "direcaoId", required = false) Integer direcaoId) {

    final var query = new GetDetalheFosXmlQuery(fosId, direcaoId);

    return queryBus.handle(query);

  }

  @PostMapping(
      value = "registar-atualizar-registo"
  )
  @Operation(
      summary = "Registar atualizar registo",
      description = "Registar atualizar registo",
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

  public ResponseEntity<String> registarAtualizarRegisto(@Valid @RequestBody DetalheXmlRequestDTO registarAtualizarRegistoRequest
  ) {

    final var command = new RegistarAtualizarRegistoCommand(registarAtualizarRegistoRequest);

    return commandBus.send(command);

  }

  @PostMapping(
      value = "novo-funcionario"
  )
  @Operation(
      summary = "Adicionar funcionario",
      description = "Adicionar funcionario",
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

  public ResponseEntity<String> adicionarFuncionario(
      @RequestParam(value = "ano") Integer ano,
      @RequestParam(value = "mes") Integer mes,
      @RequestParam(value = "fosId") Long fosId,
      @RequestParam(value = "numeroSegurado") Long numeroSegurado) {

    final var command = new AdicionarFuncionarioCommand(ano, mes, fosId, numeroSegurado);

    return commandBus.send(command);

  }

  @PostMapping(
      value = "enviar-folha"
  )
  @Operation(
      summary = "Enviar folha",
      description = "Enviar folha",
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

  public ResponseEntity<String> enviarFolha(
      @RequestParam(value = "fosId") Long fosId) {

    final var command = new EnviarFolhaCommand(fosId);

    return commandBus.send(command);

  }

  @PostMapping(
      value = "substituir-xml"
  )
  @Operation(
      summary = "Substituir xml",
      description = "Substituir xml",
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

  public ResponseEntity<String> substituirXml(
      @RequestParam(value = "mesReferencia") String mesReferencia,
      @RequestParam(value = "fosId") Long fosId) {

    final var command = new SubstituirXmlCommand(mesReferencia, fosId);

    return commandBus.send(command);

  }

}
