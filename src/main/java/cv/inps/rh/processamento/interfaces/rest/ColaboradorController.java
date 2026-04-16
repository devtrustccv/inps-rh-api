/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME */

package cv.inps.rh.processamento.interfaces.rest;

import cv.igrp.framework.core.domain.CommandBus;
import cv.igrp.framework.core.domain.QueryBus;
import cv.igrp.framework.stereotype.IgrpController;
import cv.inps.rh.processamento.application.commands.ImportarMovimentosCommand;
import cv.inps.rh.processamento.application.commands.ValidarMovimentoImportadoCommand;
import cv.inps.rh.processamento.application.dto.MovimentosImportadosDTO;
import cv.inps.rh.processamento.application.dto.ValidacaoMovimentoImportadoDTO;
import cv.inps.rh.processamento.application.dto.WrapperListaColaboradorDTO;
import cv.inps.rh.processamento.application.queries.GetListaBaixamedicaQuery;
import cv.inps.rh.processamento.application.queries.GetListaLicensaSemVencimentoQuery;
import cv.inps.rh.processamento.application.queries.GetMovimentosImportadosQuery;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@IgrpController
@RestController
@RequestMapping(path = "colaborador")
@Tag(
    name = "Processamento",
    description = "Colaborador"
)
public class ColaboradorController {


  private final QueryBus queryBus;
  private final CommandBus commandBus;

  public ColaboradorController(QueryBus queryBus, CommandBus commandBus) {
    this.queryBus = queryBus;
    this.commandBus = commandBus;
  }

  @GetMapping(
      value = "baixa-medica"
  )
  @Operation(
      summary = "Get lista baixamedica",
      description = "Get lista baixamedica",
      responses = {
          @ApiResponse(
              responseCode = "200",

              content = @Content(
                  mediaType = "application/json",
                  schema = @Schema(
                      implementation = WrapperListaColaboradorDTO.class,
                      type = "object")
              )
          )
      }
  )

  public ResponseEntity<WrapperListaColaboradorDTO> getListaBaixamedica(
      @RequestParam(value = "dataInicio", required = false) String dataInicio,
      @RequestParam(value = "dataFim", required = false) String dataFim,
      @RequestParam(value = "colaborador", required = false) String colaborador,
      @RequestParam(value = "direccao", required = false) String direccao,
      @RequestParam(value = "page", required = false, defaultValue = "0") String page,
      @RequestParam(value = "size", required = false, defaultValue = "20") String size) {

    final var query = new GetListaBaixamedicaQuery(dataInicio, dataFim, colaborador, direccao, page, size);

    return queryBus.handle(query);

  }

  @GetMapping(
      value = "licensa-sem-vencimento"
  )
  @Operation(
      summary = "Get lista licensa sem vencimento",
      description = "Get lista licensa sem vencimento",
      responses = {
          @ApiResponse(
              responseCode = "200",

              content = @Content(
                  mediaType = "application/json",
                  schema = @Schema(
                      implementation = WrapperListaColaboradorDTO.class,
                      type = "object")
              )
          )
      }
  )

  public ResponseEntity<WrapperListaColaboradorDTO> getListaLicensaSemVencimento(
      @RequestParam(value = "dataInicio", required = false) String dataInicio,
      @RequestParam(value = "dataFim", required = false) String dataFim,
      @RequestParam(value = "colaborador", required = false) String colaborador,
      @RequestParam(value = "direccao", required = false) String direccao,
      @RequestParam(value = "page", required = false, defaultValue = "0") String page,
      @RequestParam(value = "size", required = false, defaultValue = "20") String size) {

    final var query = new GetListaLicensaSemVencimentoQuery(dataInicio, dataFim, colaborador, direccao, page, size);

    return queryBus.handle(query);

  }

  @PostMapping(
      value = "importar-movimento",
      consumes = "multipart/form-data"
  )
  @Operation(
      summary = "Importar movimentos",
      description = "Importar movimentos",
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

  public ResponseEntity<String> importarMovimentos(
      @RequestParam(value = "ficheiro") MultipartFile ficheiro) {

    final var command = new ImportarMovimentosCommand(ficheiro);

    return commandBus.send(command);

  }

  @GetMapping(
      value = "movimentos"
  )
  @Operation(
      summary = "Get movimentos importados",
      description = "Get movimentos importados",
      responses = {
          @ApiResponse(
              responseCode = "200",

              content = @Content(
                  mediaType = "application/json",
                  schema = @Schema(
                      implementation = MovimentosImportadosDTO.class,
                      type = "object")
              )
          )
      }
  )

  public ResponseEntity<MovimentosImportadosDTO> getMovimentosImportados(
      @RequestParam(value = "page", required = false, defaultValue = "0") String page,
      @RequestParam(value = "size", required = false, defaultValue = "20") String size,
      @RequestParam(value = "dataImportacao", required = false) String dataImportacao) {

    final var query = new GetMovimentosImportadosQuery(page, size, dataImportacao);

    return queryBus.handle(query);

  }

  @PostMapping(
      value = "movimentos/{movimentoId}/validar"
  )
  @Operation(
      summary = "Validar movimento importado",
      description = "Validar movimento importado",
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

  public ResponseEntity<String> validarMovimentoImportado(@Valid @RequestBody ValidacaoMovimentoImportadoDTO validarMovimentoImportadoRequest
      , @PathVariable(value = "movimentoId") String movimentoId) {

    final var command = new ValidarMovimentoImportadoCommand(validarMovimentoImportadoRequest, movimentoId);

    return commandBus.send(command);

  }

}
