/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME */

package cv.inps.rh.configuracao.interfaces.rest;

import cv.igrp.framework.core.domain.CommandBus;
import cv.igrp.framework.core.domain.QueryBus;
import cv.igrp.framework.stereotype.IgrpController;
import cv.inps.rh.configuracao.application.commands.SaveResponsaveisDirecaoCommand;
import cv.inps.rh.configuracao.application.dto.AssociarResponsaveisRequestDTO;
import cv.inps.rh.configuracao.application.dto.ResponsaveisDirecaoResponseDTO;
import cv.inps.rh.configuracao.application.dto.ResponsavelEmailDTO;
import cv.inps.rh.configuracao.application.dto.WrapperListResponsaveisDTO;
import cv.inps.rh.configuracao.application.queries.GetResponsaveisDirecaoQuery;
import cv.inps.rh.configuracao.application.queries.GetResponsaveisEmailsQuery;
import cv.inps.rh.configuracao.application.queries.GetResponsaveisQuery;
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
@RequestMapping(path = "configuracao")
@Tag(
    name = "Configuracao",
    description = "Gestão de Responsáveis Direção"
)
public class ResponsavelController {


  private final QueryBus queryBus;
  private final CommandBus commandBus;

  public ResponsavelController(QueryBus queryBus, CommandBus commandBus) {
          this.queryBus = queryBus;
          this.commandBus = commandBus;
  }
   @GetMapping(
   value = "direcao/responsavel/{institutoId}"
  )
  @Operation(
    summary = "Get responsaveis direcao",
    description = "Get responsaveis direcao",
    responses = {
      @ApiResponse(
          responseCode = "200",

          content = @Content(
              mediaType = "application/json",
              schema = @Schema(
                  implementation = ResponsaveisDirecaoResponseDTO.class,
                  type = "object")
          )
      )
    }
  )

   public ResponseEntity<ResponsaveisDirecaoResponseDTO> getResponsaveisDirecao(
       @PathVariable String institutoId)
  {

    final var query = new GetResponsaveisDirecaoQuery(institutoId);

      return queryBus.handle(query);

  }

   @PostMapping(
   value = "direcao/responsavel"
  )
  @Operation(
    summary = "Save responsaveis direcao",
    description = "Save responsaveis direcao",
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

   public ResponseEntity<String> saveResponsaveisDirecao(@Valid @RequestBody AssociarResponsaveisRequestDTO saveResponsaveisDirecaoRequest
    )
  {

      final var command = new SaveResponsaveisDirecaoCommand(saveResponsaveisDirecaoRequest);

      return commandBus.send(command);

  }

   @GetMapping(
   value = "responsaveis/search"
  )
  @Operation(
    summary = "Get responsaveis",
    description = "Get responsaveis",
    responses = {
      @ApiResponse(
          responseCode = "200",

          content = @Content(
              mediaType = "application/json",
              schema = @Schema(
                  implementation = WrapperListResponsaveisDTO.class,
                  type = "object")
          )
      )
    }
  )

   public ResponseEntity<WrapperListResponsaveisDTO> getResponsaveis(
    @RequestParam(value = "pageNumber", defaultValue = "0") String pageNumber,
    @RequestParam(value = "pageSize", defaultValue = "20") String pageSize,
    @RequestParam(value = "nomeFuncionario", required = false) String nomeFuncionario,
    @RequestParam(value = "nomeInstituicao", required = false) String nomeInstituicao,
    @RequestParam(value = "idInstituicao", required = false) Long idInstituicao,
    @RequestParam(value = "nomeSecccao", required = false) String nomeSecccao,
    @RequestParam(value = "idSeccao", required = false) Long idSeccao)
  {

      final var query = new GetResponsaveisQuery(pageNumber, pageSize, nomeFuncionario, nomeInstituicao, idInstituicao, nomeSecccao, idSeccao);

      return queryBus.handle(query);

  }

   @GetMapping(
   value = "responsaveis/emails"
  )
  @Operation(
    summary = "Get emails dos responsaveis",
    description = "Lista de emails de RH_T_RESPONSAVEL para o multiselect do ecra de notificacao. "
        + "Passando funcionarioId, a direcao/seccao e deduzida da mobilidade activa do colaborador.",
    responses = {
      @ApiResponse(
          responseCode = "200",

          content = @Content(
              mediaType = "application/json",
              schema = @Schema(
                  implementation = ResponsavelEmailDTO.class,
                  type = "array")
          )
      )
    }
  )

   public ResponseEntity<List<ResponsavelEmailDTO>> getResponsaveisEmails(
    @RequestParam(value = "funcionarioId", required = false) String funcionarioId,
    @RequestParam(value = "idInstituicao", required = false) Long idInstituicao,
    @RequestParam(value = "idSeccao", required = false) Long idSeccao)
  {

      final var query = new GetResponsaveisEmailsQuery(funcionarioId, idInstituicao, idSeccao);

      return queryBus.handle(query);

  }

}
