/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME */

package cv.inps.rh.processamento.interfaces.rest;

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
import cv.inps.rh.processamento.application.queries.*;

import cv.inps.rh.processamento.application.dto.WrapperPesquisaColaboradorDTO;
import cv.inps.rh.processamento.application.dto.WrapperPesquisaCentroCustoDTO;

@IgrpController
@RestController
@RequestMapping(path = "pesquisa")
@Tag(
    name = "Processamento",
    description = "Pesquisas Globais"
)
public class PesquisaController {

  
  private final QueryBus queryBus;

  public PesquisaController(QueryBus queryBus) {
          this.queryBus = queryBus;
          
  }
   @GetMapping(
   value = "colaborador"
  )
  @Operation(
    summary = "Pesquisa colaborador",
    description = "Pesquisa colaborador",
    responses = {
      @ApiResponse(
          responseCode = "200",
          
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(
                  implementation = WrapperPesquisaColaboradorDTO.class,
                  type = "object")
          )
      )
    }
  )
  
  public ResponseEntity<WrapperPesquisaColaboradorDTO> pesquisaColaborador(
    @RequestParam(value = "uuidFuncionario", required = false) String uuidFuncionario,
    @RequestParam(value = "nome", required = false) String nome,
    @RequestParam(value = "direccao", required = false) String direccao,
    @RequestParam(value = "centroCusto", required = false) String centroCusto,
    @RequestParam(value = "page", required = false, defaultValue = "0") String page,
    @RequestParam(value = "size", required = false, defaultValue = "20") String size)
  {

      final var query = new PesquisaColaboradorQuery(uuidFuncionario, nome, direccao, centroCusto, page, size);

      return queryBus.handle(query);

  }

   @GetMapping(
   value = "centro-custo"
  )
  @Operation(
    summary = "Pesquisa centro custo",
    description = "Pesquisa centro custo",
    responses = {
      @ApiResponse(
          responseCode = "200",
          
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(
                  implementation = WrapperPesquisaCentroCustoDTO.class,
                  type = "object")
          )
      )
    }
  )
  
  public ResponseEntity<WrapperPesquisaCentroCustoDTO> pesquisaCentroCusto(
    @RequestParam(value = "nome", required = false) String nome,
    @RequestParam(value = "page", required = false, defaultValue = "0") String page,
    @RequestParam(value = "size", required = false, defaultValue = "20") String size)
  {

      final var query = new PesquisaCentroCustoQuery(nome, page, size);

      return queryBus.handle(query);

  }

}