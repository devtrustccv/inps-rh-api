/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME */

package cv.inps.rh.funcionario.interfaces.rest;

import cv.igrp.framework.core.domain.CommandBus;
import cv.igrp.framework.core.domain.QueryBus;
import cv.igrp.framework.stereotype.IgrpController;
import cv.inps.rh.funcionario.application.commands.AlterarEscalaoCargoCommand;
import cv.inps.rh.funcionario.application.commands.AtualizarRelacaoLaboralCommand;
import cv.inps.rh.funcionario.application.commands.NovaRelacaoLaboralCommand;
import cv.inps.rh.funcionario.application.commands.ValidarEscalaoCargoCommand;
import cv.inps.rh.funcionario.application.dto.AlterarEscalaoCargoDTO;
import cv.inps.rh.funcionario.application.dto.RelacaoLaboralDTO;
import cv.inps.rh.funcionario.application.dto.RelacaoLaboralReqDTO;
import cv.inps.rh.funcionario.application.dto.WrapperHistLaboralResponseDTO;
import cv.inps.rh.funcionario.application.dto.WrapperRelacaoLaboralSumaryDTO;
import cv.inps.rh.funcionario.application.queries.GetHistoricoLaboralQuery;
import cv.inps.rh.funcionario.application.queries.GetRelacaoLaboralByTiprelUuidQuery;
import cv.inps.rh.funcionario.application.queries.GetRelacaoLaboralByFunIdQuery;
import cv.inps.rh.funcionario.application.queries.GetRelacaoLaboralComboQuery;
import cv.inps.rh.funcionario.application.queries.GetRelacaoLaboralQuery;
import cv.inps.rh.shared.application.dto.ComboItemDTO;
import cv.inps.rh.shared.application.dto.SuccessResponseDTO;
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
@RequestMapping(path = "api/v1/funcionarios")
@Tag(
    name = "Funcionario",
    description = "Gestão de Histórico Laboral"
)
public class HistoricoLaboralController {


  private final QueryBus queryBus;
  private final CommandBus commandBus;

  public HistoricoLaboralController(QueryBus queryBus, CommandBus commandBus) {
          this.queryBus = queryBus;
          this.commandBus = commandBus;
  }
   @GetMapping(
   value = "{funcionarioId}/historico-laboral"
  )
  @Operation(
    summary = "Get historico laboral",
    description = "Get historico laboral",
    responses = {
      @ApiResponse(
          responseCode = "200",

          content = @Content(
              mediaType = "application/json",
              schema = @Schema(
                  implementation = WrapperHistLaboralResponseDTO.class,
                  type = "object")
          )
      )
    }
  )

   public ResponseEntity<WrapperHistLaboralResponseDTO> getHistoricoLaboral(
    @RequestParam(value = "referencia", required = false) String referencia,
    @RequestParam(value = "tipoSituacao", required = false) String tipoSituacao,
    @RequestParam(value = "situacaoLaboral", required = false) String situacaoLaboral,
    @RequestParam(value = "dataInicio", required = false) String dataInicio,
    @RequestParam(value = "dataFim", required = false) String dataFim,
    @RequestParam(value = "tamanho", required = false, defaultValue = "20") String tamanho,
    @RequestParam(value = "pagina", required = false, defaultValue = "0") String pagina, @PathVariable(value = "funcionarioId") String funcionarioId)
  {

      final var query = new GetHistoricoLaboralQuery(referencia, tipoSituacao, situacaoLaboral, dataInicio, dataFim, tamanho, pagina, funcionarioId);

      return queryBus.handle(query);

  }

   @PostMapping(
   value = "{idFuncionario}/relacao-laboral"
  )
  @Operation(
    summary = "Nova relacao laboral",
    description = "Nova relacao laboral",
    responses = {
      @ApiResponse(
          responseCode = "200",

          content = @Content(
              mediaType = "application/json",
              schema = @Schema(
                  implementation = SuccessResponseDTO.class,
                  type = "object")
          )
      )
    }
  )

   public ResponseEntity<SuccessResponseDTO> novaRelacaoLaboral(@Valid @RequestBody RelacaoLaboralReqDTO novaRelacaoLaboralRequest
    , @PathVariable(value = "idFuncionario") String idFuncionario)
  {

      final var command = new NovaRelacaoLaboralCommand(novaRelacaoLaboralRequest, idFuncionario);

      return commandBus.send(command);

  }

   @PutMapping(
   value = "{idFuncionario}/relacao-laboral/{tiprelUuid}"
  )
  @Operation(
    summary = "Atualizar relacao laboral",
    description = "Atualizar relacao laboral",
    responses = {
      @ApiResponse(
          responseCode = "200",

          content = @Content(
              mediaType = "application/json",
              schema = @Schema(
                  implementation = SuccessResponseDTO.class,
                  type = "object")
          )
      )
    }
  )

   public ResponseEntity<SuccessResponseDTO> atualizarRelacaoLaboral(@Valid @RequestBody RelacaoLaboralReqDTO atualizarRelacaoLaboralRequest
    , @PathVariable(value = "idFuncionario") String idFuncionario,@PathVariable(value = "tiprelUuid") String tiprelUuid)
  {

      final var command = new AtualizarRelacaoLaboralCommand(atualizarRelacaoLaboralRequest, idFuncionario, tiprelUuid);

      return commandBus.send(command);

  }

   @GetMapping(
   value = "relacao-laboral/{tiprelUuid}"
  )
  @Operation(
    summary = "Get relacao laboral by tiprel uuid",
    description = "Get relacao laboral by tiprel uuid",
    responses = {
      @ApiResponse(
          responseCode = "200",

          content = @Content(
              mediaType = "application/json",
              schema = @Schema(
                  implementation = RelacaoLaboralDTO.class,
                  type = "object")
          )
      )
    }
  )

   public ResponseEntity<RelacaoLaboralDTO> getRelacaoLaboralByTiprelUuid(
    @PathVariable(value = "tiprelUuid") String tiprelUuid)
  {

      final var query = new GetRelacaoLaboralByTiprelUuidQuery(tiprelUuid);

      return queryBus.handle(query);

  }

   @GetMapping(
   value = "{funcionarioId}/relacao-laboral"
  )
  @Operation(
    summary = "Get relacao laboral",
    description = "Get relacao laboral",
    responses = {
      @ApiResponse(
          responseCode = "200",

          content = @Content(
              mediaType = "application/json",
              schema = @Schema(
                  implementation = WrapperRelacaoLaboralSumaryDTO.class,
                  type = "object")
          )
      )
    }
  )

   public ResponseEntity<WrapperRelacaoLaboralSumaryDTO> getRelacaoLaboral(
    @PathVariable(value = "funcionarioId") String funcionarioId)
  {

      final var query = new GetRelacaoLaboralQuery(funcionarioId);

      return queryBus.handle(query);

  }

   @GetMapping(
   value = "relacao-laboral/{idFuncionario}/atual"
  )
  @Operation(
    summary = "Get relacao laboral by fun id",
    description = "Get relacao laboral by fun id",
    responses = {
      @ApiResponse(
          responseCode = "200",

          content = @Content(
              mediaType = "application/json",
              schema = @Schema(
                  implementation = RelacaoLaboralDTO.class,
                  type = "object")
          )
      )
    }
  )

   public ResponseEntity<RelacaoLaboralDTO> getRelacaoLaboralByFunId(
    @PathVariable(value = "idFuncionario") String idFuncionario)
  {

      final var query = new GetRelacaoLaboralByFunIdQuery(idFuncionario);

      return queryBus.handle(query);

  }

   @GetMapping(
   value = "{funcionarioId}/relacao-laboral/combo"
  )
  @Operation(
    summary = "Get relacao laboral combo",
    description = "Get relacao laboral combo",
    responses = {
      @ApiResponse(
          responseCode = "200",

          content = @Content(
              mediaType = "application/json",
              schema = @Schema(
                  implementation = ComboItemDTO.class,
                  type = "array")
          )
      )
    }
  )

   public ResponseEntity<List<ComboItemDTO>> getRelacaoLaboralCombo(
    @PathVariable(value = "funcionarioId") String funcionarioId)
  {

      final var query = new GetRelacaoLaboralComboQuery(funcionarioId);

      return queryBus.handle(query);

  }

   @PostMapping(
   value = "{funcionarioId}/relacao-laboral/alterar-escalao-cargo"
  )
  @Operation(
    summary = "Alterar escalao / cargo (Gestao Laboral)",
    description = "Alterar escalao / cargo para colaboradores PCCS sem carreira. Escalao vai a validacao; cargo e imediato.",
    responses = {
      @ApiResponse(
          responseCode = "200",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(
                  implementation = SuccessResponseDTO.class,
                  type = "object")
          )
      )
    }
  )

   public ResponseEntity<SuccessResponseDTO> alterarEscalaoCargo(@Valid @RequestBody AlterarEscalaoCargoDTO alterarEscalaoCargoRequest
    , @PathVariable(value = "funcionarioId") String funcionarioId)
  {

      final var command = new AlterarEscalaoCargoCommand(alterarEscalaoCargoRequest, funcionarioId);

      return commandBus.send(command);

  }

   @PutMapping(
   value = "{funcionarioId}/relacao-laboral/alterar-escalao-cargo/{tiprelUuid}"
  )
  @Operation(
    summary = "Validar alteracao de escalao / cargo",
    description = "Validar (SIM/NAO/CORRIGIR) a alteracao de escalao pendente. Na validacao positiva fecha o vencimento anterior e abre um novo.",
    responses = {
      @ApiResponse(
          responseCode = "200",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(
                  implementation = SuccessResponseDTO.class,
                  type = "object")
          )
      )
    }
  )

   public ResponseEntity<SuccessResponseDTO> validarEscalaoCargo(@Valid @RequestBody AlterarEscalaoCargoDTO validarEscalaoCargoRequest
    , @PathVariable(value = "funcionarioId") String funcionarioId,@PathVariable(value = "tiprelUuid") String tiprelUuid)
  {

      final var command = new ValidarEscalaoCargoCommand(validarEscalaoCargoRequest, funcionarioId, tiprelUuid);

      return commandBus.send(command);

  }

}
