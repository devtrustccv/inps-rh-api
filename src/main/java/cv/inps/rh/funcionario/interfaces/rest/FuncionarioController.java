/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME. */

package cv.inps.rh.funcionario.interfaces.rest;

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
import cv.inps.rh.funcionario.application.queries.*;
import cv.igrp.framework.core.domain.CommandBus;
import cv.inps.rh.funcionario.application.commands.*;
import cv.inps.rh.funcionario.application.dto.FuncionarioRequestDTO;
import cv.inps.rh.funcionario.application.dto.FuncionarioResponseDTO;
import cv.inps.rh.funcionario.application.dto.WrapperListaValidacoesDTO;
import cv.inps.rh.funcionario.application.dto.WrapperListaFuncionarioDTO;
import java.util.Map;
import cv.inps.rh.funcionario.application.dto.AtivarInativarColaboradorDTO;
import cv.inps.rh.funcionario.application.dto.ValidacaoDadosPessoaisDTO;
import cv.inps.rh.funcionario.application.dto.ValidarDadosAcademicosDTO;
import cv.inps.rh.funcionario.application.dto.ValidarAgregadosDependentesDTO;
import cv.inps.rh.funcionario.application.dto.ValidarDadosBancariosDTO;
import cv.inps.rh.funcionario.application.dto.DadosPessoaisRespDTO;
import cv.inps.rh.funcionario.application.dto.DadosAcademicosProfResponseDTO;
import java.util.List;
import cv.inps.rh.funcionario.application.dto.AgregadoDependenteRespDTO;
import cv.inps.rh.funcionario.application.dto.DadosBancariosRespDTO;

@IgrpController
@RestController
@RequestMapping(path = "api/v1/funcionarios")
@Tag(name = "Funcionario", description = "gestao de funcionarios")
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
          description = "",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(
                  implementation = FuncionarioResponseDTO.class,
                  type = "object")
          )
      )
    }
  )
  
  public ResponseEntity<FuncionarioResponseDTO> createFuncionario(@Valid @RequestBody FuncionarioRequestDTO createFuncionarioRequest
    )
  {

      final var command = new CreateFuncionarioCommand(createFuncionarioRequest);

       ResponseEntity<FuncionarioResponseDTO> response = commandBus.send(command);

       return response;
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
          description = "",
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

      ResponseEntity<FuncionarioResponseDTO> response = queryBus.handle(query);

      return response;
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
          description = "",
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

      ResponseEntity<WrapperListaValidacoesDTO> response = queryBus.handle(query);

      return response;
  }

   @GetMapping(
  )
  @Operation(
    summary = "Get list funcionarios",
    description = "Get list funcionarios",
    responses = {
      @ApiResponse(
          responseCode = "200",
          description = "",
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
    @RequestParam(value = "estado", required = false) String estado)
  {

      final var query = new GetListFuncionariosQuery(pageNumber, pageSize, nome, direccao, seccao, tipoVinculoLaboral, dataInicio, dataFim, estado);

      ResponseEntity<WrapperListaFuncionarioDTO> response = queryBus.handle(query);

      return response;
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
          description = "",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(
                  implementation = String.class,
                  type = "String")
          )
      )
    }
  )
  
  public ResponseEntity<Map<String, ?>> validarRegistoColaborador(@Valid @RequestBody FuncionarioRequestDTO validarRegistoColaboradorRequest
    , @PathVariable(value = "id") String id)
  {

      final var command = new ValidarRegistoColaboradorCommand(validarRegistoColaboradorRequest, id);

       ResponseEntity<Map<String, ?>> response = commandBus.send(command);

       return response;
  }

   @PatchMapping(
   value = "status/{id}"
  )
  @Operation(
    summary = "Inativar ativar colaborador",
    description = "Inativar ativar colaborador",
    responses = {
      @ApiResponse(
          responseCode = "200",
          description = "",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(
                  implementation = AtivarInativarColaboradorDTO.class,
                  type = "object")
          )
      )
    }
  )
  
  public ResponseEntity<AtivarInativarColaboradorDTO> inativarAtivarColaborador(@Valid @RequestBody AtivarInativarColaboradorDTO inativarAtivarColaboradorRequest
    , @PathVariable(value = "id") String id)
  {

      final var command = new InativarAtivarColaboradorCommand(inativarAtivarColaboradorRequest, id);

       ResponseEntity<AtivarInativarColaboradorDTO> response = commandBus.send(command);

       return response;
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
          description = "",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(
                  implementation = ValidacaoDadosPessoaisDTO.class,
                  type = "object")
          )
      )
    }
  )
  
  public ResponseEntity<ValidacaoDadosPessoaisDTO> validaDadosPessoais(@Valid @RequestBody ValidacaoDadosPessoaisDTO validaDadosPessoaisRequest
    , @PathVariable(value = "idFuncionario") String idFuncionario)
  {

      final var command = new ValidaDadosPessoaisCommand(validaDadosPessoaisRequest, idFuncionario);

       ResponseEntity<ValidacaoDadosPessoaisDTO> response = commandBus.send(command);

       return response;
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
          description = "",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(
                  implementation = ValidarDadosAcademicosDTO.class,
                  type = "object")
          )
      )
    }
  )
  
  public ResponseEntity<ValidarDadosAcademicosDTO> validarDadosAcademicos(@Valid @RequestBody ValidarDadosAcademicosDTO validarDadosAcademicosRequest
    , @PathVariable(value = "idFuncionario") String idFuncionario)
  {

      final var command = new ValidarDadosAcademicosCommand(validarDadosAcademicosRequest, idFuncionario);

       ResponseEntity<ValidarDadosAcademicosDTO> response = commandBus.send(command);

       return response;
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
          description = "",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(
                  implementation = ValidarAgregadosDependentesDTO.class,
                  type = "object")
          )
      )
    }
  )
  
  public ResponseEntity<ValidarAgregadosDependentesDTO> validarDadosFamiliares(@Valid @RequestBody ValidarAgregadosDependentesDTO validarDadosFamiliaresRequest
    , @PathVariable(value = "idFuncionario") String idFuncionario)
  {

      final var command = new ValidarDadosFamiliaresCommand(validarDadosFamiliaresRequest, idFuncionario);

       ResponseEntity<ValidarAgregadosDependentesDTO> response = commandBus.send(command);

       return response;
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
          description = "",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(
                  implementation = ValidarDadosBancariosDTO.class,
                  type = "object")
          )
      )
    }
  )
  
  public ResponseEntity<ValidarDadosBancariosDTO> validarDadosBancarios(@Valid @RequestBody ValidarDadosBancariosDTO validarDadosBancariosRequest
    , @PathVariable(value = "idFuncionario") String idFuncionario)
  {

      final var command = new ValidarDadosBancariosCommand(validarDadosBancariosRequest, idFuncionario);

       ResponseEntity<ValidarDadosBancariosDTO> response = commandBus.send(command);

       return response;
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
          description = "",
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

      ResponseEntity<DadosPessoaisRespDTO> response = queryBus.handle(query);

      return response;
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
          description = "",
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
    @PathVariable(value = "idFuncionario") String idFuncionario)
  {

      final var query = new GetDadosAcademicosQuery(idFuncionario);

      ResponseEntity<DadosAcademicosProfResponseDTO> response = queryBus.handle(query);

      return response;
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
          description = "",
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
    @PathVariable(value = "idFuncionario") String idFuncionario)
  {

      final var query = new GetDadosFamiliaresQuery(idFuncionario);

      ResponseEntity<List<AgregadoDependenteRespDTO>> response = queryBus.handle(query);

      return response;
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
          description = "",
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
    @PathVariable(value = "idFuncionario") String idFuncionario)
  {

      final var query = new GetDadosBancariosQuery(idFuncionario);

      ResponseEntity<List<DadosBancariosRespDTO>> response = queryBus.handle(query);

      return response;
  }

   @GetMapping(
   value = "status/{id}"
  )
  @Operation(
    summary = "Get situacao laboral colaborador",
    description = "Get situacao laboral colaborador",
    responses = {
      @ApiResponse(
          responseCode = "200",
          description = "",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(
                  implementation = AtivarInativarColaboradorDTO.class,
                  type = "object")
          )
      )
    }
  )
  
  public ResponseEntity<AtivarInativarColaboradorDTO> getSituacaoLaboralColaborador(
    @PathVariable(value = "id") String id)
  {

      final var query = new GetSituacaoLaboralColaboradorQuery(id);

      ResponseEntity<AtivarInativarColaboradorDTO> response = queryBus.handle(query);

      return response;
  }

}