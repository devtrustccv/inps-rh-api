/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME */

package cv.inps.rh.processamento.interfaces.rest;

import cv.igrp.framework.core.domain.QueryBus;
import cv.igrp.framework.stereotype.IgrpController;
import cv.inps.rh.processamento.application.dto.WrapperListaColaboradorDTO;
import cv.inps.rh.processamento.application.queries.GetListaBaixamedicaQuery;
import cv.inps.rh.processamento.application.queries.GetListaLicensaSemVencimentoQuery;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@IgrpController
@RestController
@RequestMapping(path = "colaborador")
@Tag(name = "Colaborador", description = "Colaborador")
public class ColaboradorController {


  private final QueryBus queryBus;

  public ColaboradorController(QueryBus queryBus) {
    this.queryBus = queryBus;

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

}
