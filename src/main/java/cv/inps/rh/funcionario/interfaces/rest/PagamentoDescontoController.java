/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME. */

package cv.inps.rh.funcionario.interfaces.rest;

import cv.igrp.framework.core.domain.QueryBus;
import cv.igrp.framework.stereotype.IgrpController;
import cv.inps.rh.funcionario.application.dto.WrapperListPagamentosDescontoDTO;
import cv.inps.rh.funcionario.application.queries.GetListPagamentosDescontoQuery;
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
@RequestMapping(path = "api/v1/funcionarios")
@Tag(name = "PagamentoDesconto", description = "gest pagamantos descontos")
public class PagamentoDescontoController {


  private final QueryBus queryBus;

  public PagamentoDescontoController(QueryBus queryBus) {
          this.queryBus = queryBus;

  }
   @GetMapping(
   value = "pagamentos-descontos"
  )
  @Operation(
    summary = "GET method to handle operations for Get list pagamentos desconto",
    description = "GET method to handle operations for Get list pagamentos desconto",
    responses = {
      @ApiResponse(
          responseCode = "200",
          description = "",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(
                  implementation = WrapperListPagamentosDescontoDTO.class,
                  type = "object")
          )
      )
    }
  )

  public ResponseEntity<WrapperListPagamentosDescontoDTO> getListPagamentosDesconto(
    @RequestParam(value = "idFuncionario") String idFuncionario,
    @RequestParam(value = "pageNumber", defaultValue = "0") String pageNumber,
    @RequestParam(value = "pageSize", defaultValue = "20") String pageSize,
    @RequestParam(value = "dataInicio", required = false) String dataInicio,
    @RequestParam(value = "dataFim", required = false) String dataFim,
    @RequestParam(value = "estado", required = false) String estado)
  {

      final var query = new GetListPagamentosDescontoQuery(idFuncionario, pageNumber, pageSize, dataInicio, dataFim, estado);

      ResponseEntity<WrapperListPagamentosDescontoDTO> response = queryBus.handle(query);

      return response;
  }

}
