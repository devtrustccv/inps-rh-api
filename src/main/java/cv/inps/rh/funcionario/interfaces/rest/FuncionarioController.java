/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME */

package cv.inps.rh.funcionario.interfaces.rest;

import cv.igrp.framework.core.domain.CommandBus;
import cv.igrp.framework.core.domain.QueryBus;
import cv.igrp.framework.stereotype.IgrpController;
import cv.inps.rh.funcionario.application.commands.*;
import cv.inps.rh.funcionario.application.dto.*;
import cv.inps.rh.funcionario.application.queries.*;
import cv.inps.rh.shared.application.dto.SuccessResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@IgrpController
@RestController
@RequestMapping(path = "api/v1/funcionarios")
@Tag(
    name = "Funcionario",
    description = "gestao de funcionarios"
)
public class FuncionarioController {


  private final QueryBus queryBus;
  private final CommandBus commandBus;

  public FuncionarioController(QueryBus queryBus, CommandBus commandBus) {
          this.queryBus = queryBus;
          this.commandBus = commandBus;
  }
   @PostMapping(
  )
  @Operation(
    summary = "Create funcionario",
    description = "Create funcionario",
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

   public ResponseEntity<SuccessResponseDTO> createFuncionario(@Valid @RequestBody FuncionarioRequestDTO createFuncionarioRequest
    )
  {

      final var command = new CreateFuncionarioCommand(createFuncionarioRequest);

      return commandBus.send(command);

  }

   @GetMapping(
   value = "{id}"
  )
  @Operation(
    summary = "Get funcionario by id",
    description = "Get funcionario by id",
    responses = {
      @ApiResponse(
          responseCode = "200",

          content = @Content(
              mediaType = "application/json",
              schema = @Schema(
                  implementation = FuncionarioResponseDTO.class,
                  type = "object")
          )
      )
    }
  )

   public ResponseEntity<FuncionarioResponseDTO> getFuncionarioById(
    @PathVariable(value = "id") String id)
  {

      final var query = new GetFuncionarioByIdQuery(id);

      return queryBus.handle(query);

  }

   @GetMapping(
   value = "validacoes"
  )
  @Operation(
    summary = "Get valicoes utilizadores",
    description = "Get valicoes utilizadores",
    responses = {
      @ApiResponse(
          responseCode = "200",

          content = @Content(
              mediaType = "application/json",
              schema = @Schema(
                  implementation = WrapperListaValidacoesDTO.class,
                  type = "object")
          )
      )
    }
  )

   public ResponseEntity<WrapperListaValidacoesDTO> getValicoesUtilizadores(
    @RequestParam(value = "nomeColaborador", required = false) String nomeColaborador,
    @RequestParam(value = "tipoOperacao", required = false) String tipoOperacao,
    @RequestParam(value = "referenciaName", required = false) String referenciaName,
    @RequestParam(value = "dataInicio", required = false) String dataInicio,
    @RequestParam(value = "dataFim", required = false) String dataFim,
    @RequestParam(value = "pageNumber", defaultValue = "0") String pageNumber,
    @RequestParam(value = "pageSize", defaultValue = "20") String pageSize)
  {

      final var query = new GetValicoesUtilizadoresQuery(nomeColaborador, tipoOperacao, referenciaName, dataInicio, dataFim, pageNumber, pageSize);

      return queryBus.handle(query);

  }

   @GetMapping(
   value = "validacoes/{idValidacao}/detalhes"
  )
  @Operation(
    summary = "Get detalhe de alteracoes",
    description = "Campos alterados de uma validacao (valor anterior e novo), para o ecra 'Detalhe de alteracoes'",
    responses = {
      @ApiResponse(
          responseCode = "200",

          content = @Content(
              mediaType = "application/json",
              array = @ArraySchema(schema = @Schema(implementation = ValidacaoDetalheDTO.class))
          )
      )
    }
  )

   public ResponseEntity<List<ValidacaoDetalheDTO>> getDetalheAlteracoes(
    @PathVariable(value = "idValidacao") String idValidacao)
  {

      final var query = new GetDetalheAlteracoesQuery(idValidacao);

      return queryBus.handle(query);

  }

   @GetMapping(
  )
  @Operation(
    summary = "Get list funcionarios",
    description = "Get list funcionarios",
    responses = {
      @ApiResponse(
          responseCode = "200",

          content = @Content(
              mediaType = "application/json",
              schema = @Schema(
                  implementation = WrapperListaFuncionarioDTO.class,
                  type = "object")
          )
      )
    }
  )

   public ResponseEntity<WrapperListaFuncionarioDTO> getListFuncionarios(
    @RequestParam(value = "pageNumber", defaultValue = "0") String pageNumber,
    @RequestParam(value = "pageSize", defaultValue = "20") String pageSize,
    @RequestParam(value = "nome", required = false) String nome,
    @RequestParam(value = "direccao", required = false) Long direccao,
    @RequestParam(value = "seccao", required = false) Long seccao,
    @RequestParam(value = "tipoVinculoLaboral", required = false) Long tipoVinculoLaboral,
    @RequestParam(value = "dataInicio", required = false) String dataInicio,
    @RequestParam(value = "dataFim", required = false) String dataFim,
    @RequestParam(value = "estado", required = false) String estado,
    @RequestParam(value = "local", required = false) Long local,
    @RequestParam(value = "cargo", required = false) Long cargo,
    @RequestParam(value = "carreira", required = false) Long carreira)
  {

      final var query = new GetListFuncionariosQuery(pageNumber, pageSize, nome, direccao, seccao, tipoVinculoLaboral, dataInicio, dataFim, estado, local, cargo, carreira);

      return queryBus.handle(query);

  }

   @PutMapping(
   value = "{id}"
  )
  @Operation(
    summary = "Validar registo colaborador",
    description = "Validar registo colaborador",
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

   public ResponseEntity<SuccessResponseDTO> validarRegistoColaborador(@Valid @RequestBody FuncionarioRequestDTO validarRegistoColaboradorRequest
    , @PathVariable(value = "id") String id)
  {

      final var command = new ValidarRegistoColaboradorCommand(validarRegistoColaboradorRequest, id);

      return commandBus.send(command);

  }


   @PutMapping(
   value = "{idFuncionario}/dados-pessoais"
  )
  @Operation(
    summary = "Valida dados pessoais",
    description = "Valida dados pessoais",
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

   public ResponseEntity<SuccessResponseDTO> validaDadosPessoais(@Valid @RequestBody ValidacaoDadosPessoaisDTO validaDadosPessoaisRequest
    , @PathVariable(value = "idFuncionario") String idFuncionario)
  {

      final var command = new ValidaDadosPessoaisCommand(validaDadosPessoaisRequest, idFuncionario);

      return commandBus.send(command);

  }

   @PutMapping(
   value = "{idFuncionario}/dados-academicos"
  )
  @Operation(
    summary = "Validar dados academicos",
    description = "Validar dados academicos",
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

   public ResponseEntity<SuccessResponseDTO> validarDadosAcademicos(@Valid @RequestBody ValidarDadosAcademicosDTO validarDadosAcademicosRequest
    , @PathVariable(value = "idFuncionario") String idFuncionario)
  {

      final var command = new ValidarDadosAcademicosCommand(validarDadosAcademicosRequest, idFuncionario);

      return commandBus.send(command);

  }

   @PutMapping(
   value = "{idFuncionario}/familiares"
  )
  @Operation(
    summary = "Validar dados familiares",
    description = "Validar dados familiares",
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

   public ResponseEntity<SuccessResponseDTO> validarDadosFamiliares(@Valid @RequestBody ValidarAgregadosDependentesDTO validarDadosFamiliaresRequest
    , @PathVariable(value = "idFuncionario") String idFuncionario)
  {

      final var command = new ValidarDadosFamiliaresCommand(validarDadosFamiliaresRequest, idFuncionario);

      return commandBus.send(command);

  }

   @PutMapping(
   value = "{idFuncionario}/dados-bancarios"
  )
  @Operation(
    summary = "Validar dados bancarios",
    description = "Validar dados bancarios",
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

   public ResponseEntity<SuccessResponseDTO> validarDadosBancarios(@Valid @RequestBody ValidarDadosBancariosDTO validarDadosBancariosRequest
    , @PathVariable(value = "idFuncionario") String idFuncionario)
  {

      final var command = new ValidarDadosBancariosCommand(validarDadosBancariosRequest, idFuncionario);

      return commandBus.send(command);

  }

   @GetMapping(
   value = "{idFuncionario}/dados-pessoais"
  )
  @Operation(
    summary = "Get dados pessoais",
    description = "Get dados pessoais",
    responses = {
      @ApiResponse(
          responseCode = "200",

          content = @Content(
              mediaType = "application/json",
              schema = @Schema(
                  implementation = DadosPessoaisRespDTO.class,
                  type = "object")
          )
      )
    }
  )

   public ResponseEntity<DadosPessoaisRespDTO> getDadosPessoais(
    @PathVariable(value = "idFuncionario") String idFuncionario)
  {

      final var query = new GetDadosPessoaisQuery(idFuncionario);

      return queryBus.handle(query);

  }

   @GetMapping(
   value = "{idFuncionario}/dados-academicos"
  )
  @Operation(
    summary = "Get dados academicos",
    description = "Get dados academicos",
    responses = {
      @ApiResponse(
          responseCode = "200",

          content = @Content(
              mediaType = "application/json",
              schema = @Schema(
                  implementation = DadosAcademicosProfResponseDTO.class,
                  type = "object")
          )
      )
    }
  )

   public ResponseEntity<DadosAcademicosProfResponseDTO> getDadosAcademicos(
    @PathVariable(value = "idFuncionario") String idFuncionario,
    @RequestParam(value = "validacao", defaultValue = "false") boolean validacao)
  {

      final var query = new GetDadosAcademicosQuery(idFuncionario, validacao);

      return queryBus.handle(query);

  }

   @GetMapping(
   value = "{idFuncionario}/familiares"
  )
  @Operation(
    summary = "Get dados familiares",
    description = "Get dados familiares",
    responses = {
      @ApiResponse(
          responseCode = "200",

          content = @Content(
              mediaType = "application/json",
              schema = @Schema(
                  implementation = AgregadoDependenteRespDTO.class,
                  type = "object")
          )
      )
    }
  )

   public ResponseEntity<List<AgregadoDependenteRespDTO>> getDadosFamiliares(
    @PathVariable(value = "idFuncionario") String idFuncionario,
    @RequestParam(value = "validacao", defaultValue = "false") boolean validacao)
  {

      final var query = new GetDadosFamiliaresQuery(idFuncionario, validacao);

      return queryBus.handle(query);

  }

   @GetMapping(
   value = "{idFuncionario}/dados-bancarios"
  )
  @Operation(
    summary = "Get dados bancarios",
    description = "Get dados bancarios",
    responses = {
      @ApiResponse(
          responseCode = "200",

          content = @Content(
              mediaType = "application/json",
              schema = @Schema(
                  implementation = DadosBancariosRespDTO.class,
                  type = "object")
          )
      )
    }
  )

   public ResponseEntity<List<DadosBancariosRespDTO>> getDadosBancarios(
    @PathVariable(value = "idFuncionario") String idFuncionario,
    @RequestParam(value = "validacao", defaultValue = "false") boolean validacao)
  {

      final var query = new GetDadosBancariosQuery(idFuncionario, validacao);

      return queryBus.handle(query);

  }


   @GetMapping(
   value = "{idFuncionario}/situacao-laboral"
  )
  @Operation(
    summary = "Get situacao laboral do funcionario",
    description = "Get situacao laboral do funcionario",
    responses = {
      @ApiResponse(
          responseCode = "200",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(
                  implementation = AlterarSituacaoLaboralRequest.class,
                  type = "object")
          )
      )
    }
  )
   public ResponseEntity<AlterarSituacaoLaboralRequest> getAlterarSituacaoLaboral(
    @PathVariable(value = "idFuncionario") String idFuncionario)
  {
      final var query = new GetAlterarSituacaoLaboralQuery(idFuncionario);
      return queryBus.handle(query);
  }

   @PatchMapping(
   value = "{idFuncionario}/situacao-laboral"
  )
  @Operation(
    summary = "Alterar situacao laboral do funcionario",
    description = "Alterar situacao laboral do funcionario (inclui Licença S/Vencimento e outras situacoes)",
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
   public ResponseEntity<SuccessResponseDTO> alterarSituacaoLaboral(
    @Valid @RequestBody AlterarSituacaoLaboralRequest alterarSituacaoLaboralRequest,
    @PathVariable(value = "idFuncionario") String idFuncionario)
  {
      final var command = new AlterarSituacaoLaboralCommand(alterarSituacaoLaboralRequest, idFuncionario);
      return commandBus.send(command);
  }

}
