/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME */

package cv.inps.rh.emprestimo.interfaces.rest;

import cv.igrp.framework.core.domain.CommandBus;
import cv.igrp.framework.core.domain.QueryBus;
import cv.igrp.framework.stereotype.IgrpController;
import cv.inps.rh.emprestimo.application.commands.*;
import cv.inps.rh.emprestimo.application.dto.*;
import cv.inps.rh.emprestimo.application.queries.GetConfiguracaoEmprestimoQuery;
import cv.inps.rh.emprestimo.application.queries.GetEmprestimoByIdQuery;
import cv.inps.rh.emprestimo.application.queries.ListarEmprestimosQuery;
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

   @PostMapping(
  )
  @Operation(
    summary = "Save emprestimo",
    description = "Save emprestimo",
    responses = {
      @ApiResponse(
          responseCode = "201",

          content = @Content(
              mediaType = "application/json",
              schema = @Schema(
                  implementation = IdDTO.class,
                  type = "object")
          )
      )
    }
  )

  public ResponseEntity<IdDTO> saveEmprestimo(@Valid @RequestBody PedidoEmprestimoDTO saveEmprestimoRequest
    )
  {

      final var command = new SaveEmprestimoCommand(saveEmprestimoRequest);

      return commandBus.send(command);

  }

   @PutMapping(
   value = "{emprestimoId}"
  )
  @Operation(
    summary = "Update emprestimo",
    description = "Update emprestimo",
    responses = {
      @ApiResponse(
          responseCode = "200",

          content = @Content(
              mediaType = "application/json",
              schema = @Schema(
                  implementation = IdDTO.class,
                  type = "object")
          )
      )
    }
  )

  public ResponseEntity<IdDTO> updateEmprestimo(@Valid @RequestBody PedidoEmprestimoDTO updateEmprestimoRequest
    , @PathVariable(value = "emprestimoId") String emprestimoId)
  {

      final var command = new UpdateEmprestimoCommand(updateEmprestimoRequest, emprestimoId);

      return commandBus.send(command);

  }

   @GetMapping(
   value = "{emprestimoId}"
  )
  @Operation(
    summary = "Get emprestimo by id",
    description = "Get emprestimo by id",
    responses = {
      @ApiResponse(
          responseCode = "200",

          content = @Content(
              mediaType = "application/json",
              schema = @Schema(
                  implementation = DetalhesEmprestimoDTO.class,
                  type = "object")
          )
      )
    }
  )

  public ResponseEntity<DetalhesEmprestimoDTO> getEmprestimoById(
    @PathVariable(value = "emprestimoId") String emprestimoId)
  {

      final var query = new GetEmprestimoByIdQuery(emprestimoId);

      return queryBus.handle(query);

  }

   @GetMapping(
  )
  @Operation(
    summary = "Listar emprestimos",
    description = "Listar emprestimos",
    responses = {
      @ApiResponse(
          responseCode = "200",

          content = @Content(
              mediaType = "application/json",
              schema = @Schema(
                  implementation = EmprestimoListDTO.class,
                  type = "object")
          )
      )
    }
  )

  public ResponseEntity<EmprestimoListDTO> listarEmprestimos(
    @RequestParam(value = "tipoEmprestimo", required = false) String tipoEmprestimo,
    @RequestParam(value = "direccaoId", required = false) String direccaoId,
    @RequestParam(value = "dataInicio", required = false) String dataInicio,
    @RequestParam(value = "dataFim", required = false) String dataFim,
    @RequestParam(value = "estadoEmprestimo", required = false) String estadoEmprestimo,
    @RequestParam(value = "page", required = false, defaultValue = "0") String page,
    @RequestParam(value = "size", required = false, defaultValue = "20") String size)
  {

      final var query = new ListarEmprestimosQuery(tipoEmprestimo, direccaoId, dataInicio, dataFim, estadoEmprestimo, page, size);

      return queryBus.handle(query);

  }

   @PostMapping(
   value = "/{emprestimoId}/analise-rh"
  )
  @Operation(
    summary = "Save decisao analise",
    description = "Save decisao analise",
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

  public ResponseEntity<String> saveDecisaoAnalise(@Valid @RequestBody AnaliseRhRequestDTO saveDecisaoAnaliseRequest
    , @PathVariable(value = "emprestimoId") String emprestimoId)
  {

      final var command = new SaveDecisaoAnaliseCommand(saveDecisaoAnaliseRequest, emprestimoId);

      return commandBus.send(command);

  }

   @PostMapping(
   value = "/{emprestimoId}/analise-financeiro"
  )
  @Operation(
    summary = "Save decisao analise financeira",
    description = "Save decisao analise financeira",
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

  public ResponseEntity<String> saveDecisaoAnaliseFinanceira(@Valid @RequestBody AnaliseFinanceiroRequestDTO saveDecisaoAnaliseFinanceiraRequest
    , @PathVariable(value = "emprestimoId") String emprestimoId)
  {

      final var command = new SaveDecisaoAnaliseFinanceiraCommand(saveDecisaoAnaliseFinanceiraRequest, emprestimoId);

      return commandBus.send(command);

  }

}
