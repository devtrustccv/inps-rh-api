/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME. */

package cv.inps.rh.funcionario.interfaces.rest;

import cv.igrp.framework.core.domain.CommandBus;
import cv.igrp.framework.core.domain.QueryBus;
import cv.igrp.framework.stereotype.IgrpController;
import cv.inps.rh.funcionario.application.commands.SaveMobilidadeCommand;
import cv.inps.rh.funcionario.application.commands.ValidarMobilidadeCommand;
import cv.inps.rh.funcionario.application.dto.MobilidadeDTO;
import cv.inps.rh.funcionario.application.dto.WrapperListMobilidadeDTO;
import cv.inps.rh.funcionario.application.queries.GetListMobilidadesQuery;
import cv.inps.rh.funcionario.application.queries.GetMobilidadeByIdQuery;
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
@RequestMapping(path = "api/v1/funcionarios")
@Tag(name = "Mobilidade", description = "GEstao mobilidade")
public class MobilidadeController {


  private final QueryBus queryBus;
  private final CommandBus commandBus;

  public MobilidadeController(QueryBus queryBus, CommandBus commandBus) {
          this.queryBus = queryBus;
          this.commandBus = commandBus;
  }
   @GetMapping(
   value = "mobilidades"
  )
  @Operation(
    summary = "Get list mobilidades",
    description = "Get list mobilidades",
    responses = {
      @ApiResponse(
          responseCode = "200",
          description = "",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(
                  implementation = WrapperListMobilidadeDTO.class,
                  type = "object")
          )
      )
    }
  )

  public ResponseEntity<WrapperListMobilidadeDTO> getListMobilidades(
    @RequestParam(value = "idFuncionario") String idFuncionario,
    @RequestParam(value = "dataInicio", required = false) String dataInicio,
    @RequestParam(value = "dataFim", required = false) String dataFim,
    @RequestParam(value = "tipoMobilidade", required = false) String tipoMobilidade,
    @RequestParam(value = "pageNumber", defaultValue = "0") String pageNumber,
    @RequestParam(value = "pageSize", defaultValue = "20") String pageSize)
  {

      final var query = new GetListMobilidadesQuery(idFuncionario, dataInicio, dataFim, tipoMobilidade, pageNumber, pageSize);

      ResponseEntity<WrapperListMobilidadeDTO> response = queryBus.handle(query);

      return response;
  }

   @GetMapping(
   value = "mobilidades/{id}"
  )
  @Operation(
    summary = "Get mobilidade by id",
    description = "Get mobilidade by id",
    responses = {
      @ApiResponse(
          responseCode = "200",
          description = "",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(
                  implementation = MobilidadeDTO.class,
                  type = "object")
          )
      )
    }
  )

  public ResponseEntity<MobilidadeDTO> getMobilidadeById(
    @PathVariable(value = "id") String id)
  {

      final var query = new GetMobilidadeByIdQuery(id);

      ResponseEntity<MobilidadeDTO> response = queryBus.handle(query);

      return response;
  }

   @PostMapping(
   value = "{idFuncionario}/mobilidades"
  )
  @Operation(
    summary = "Save mobilidade",
    description = "Save mobilidade",
    responses = {
      @ApiResponse(
          responseCode = "200",
          description = "",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(
                  implementation = MobilidadeDTO.class,
                  type = "object")
          )
      )
    }
  )

  public ResponseEntity<MobilidadeDTO> saveMobilidade(@Valid @RequestBody MobilidadeDTO saveMobilidadeRequest
    , @PathVariable(value = "idFuncionario") String idFuncionario)
  {

      final var command = new SaveMobilidadeCommand(saveMobilidadeRequest, idFuncionario);

       ResponseEntity<MobilidadeDTO> response = commandBus.send(command);

       return response;
  }

   @PutMapping(
   value = "{idFuncionario}/mobilidades/{mobilidadeId}"
  )
  @Operation(
    summary = "Validar mobilidade",
    description = "Validar mobilidade",
    responses = {
      @ApiResponse(
          responseCode = "200",
          description = "",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(
                  implementation = MobilidadeDTO.class,
                  type = "object")
          )
      )
    }
  )

  public ResponseEntity<MobilidadeDTO> validarMobilidade(@Valid @RequestBody MobilidadeDTO validarMobilidadeRequest
    , @PathVariable(value = "idFuncionario") String idFuncionario,@PathVariable(value = "mobilidadeId") String mobilidadeId)
  {

      final var command = new ValidarMobilidadeCommand(validarMobilidadeRequest, idFuncionario, mobilidadeId);

       ResponseEntity<MobilidadeDTO> response = commandBus.send(command);

       return response;
  }

}
