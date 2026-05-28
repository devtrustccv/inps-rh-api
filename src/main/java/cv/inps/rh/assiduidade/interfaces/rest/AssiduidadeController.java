/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME */

package cv.inps.rh.assiduidade.interfaces.rest;

import cv.igrp.framework.core.domain.CommandBus;
import cv.igrp.framework.core.domain.QueryBus;
import cv.igrp.framework.stereotype.IgrpController;
import cv.inps.rh.assiduidade.application.commands.*;
import cv.inps.rh.assiduidade.application.commands.EnviarDireitoFeriasCommand;
import cv.inps.rh.assiduidade.application.dto.*;
import cv.inps.rh.assiduidade.application.queries.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@IgrpController
@RestController
@RequestMapping(path = "api/v1/assiduidade")
@Tag(
    name = "Assiduidade",
    description = "gestao de assiduidade"
)
public class AssiduidadeController {


  private final QueryBus queryBus;
  private final CommandBus commandBus;

  public AssiduidadeController(QueryBus queryBus, CommandBus commandBus) {
          this.queryBus = queryBus;
          this.commandBus = commandBus;
  }
   @GetMapping(
   value = "picagens"
  )
  @Operation(
    summary = "Get lista picagem",
    description = "Get lista picagem",
    responses = {
      @ApiResponse(
          responseCode = "200",

          content = @Content(
              mediaType = "application/json",
              schema = @Schema(
                  implementation = WrapperListaPicagemDTO.class,
                  type = "object")
          )
      )
    }
  )

   public ResponseEntity<WrapperListaPicagemDTO> getListaPicagem(
    @RequestParam(value = "pageSize", required = false, defaultValue = "20") String pageSize,
    @RequestParam(value = "pageNumber", required = false, defaultValue = "0") String pageNumber,
    @RequestParam(value = "colaborador", required = false) String colaborador,
    @RequestParam(value = "funcionarioUuid", required = false) String funcionarioUuid,
    @RequestParam(value = "direcao", required = false) Long direcao,
    @RequestParam(value = "seccao", required = false) Long seccao,
    @RequestParam(value = "ups", required = false) Long ups,
    @RequestParam(value = "dataInicio", required = false) String dataInicio,
    @RequestParam(value = "dataFim", required = false) String dataFim)
  {

      final var query = new GetListaPicagemQuery(pageSize, pageNumber, colaborador, funcionarioUuid, direcao, seccao, ups, dataInicio, dataFim);

      return queryBus.handle(query);

  }

   @GetMapping(
   value = "movimento-resumos"
  )
  @Operation(
    summary = "Get lista movimentos resumidos",
    description = "Get lista movimentos resumidos",
    responses = {
      @ApiResponse(
          responseCode = "200",

          content = @Content(
              mediaType = "application/json",
              schema = @Schema(
                  implementation = WrapperListaAssiduidadadeDTO.class,
                  type = "object")
          )
      )
    }
  )

   public ResponseEntity<WrapperListaAssiduidadadeDTO> getListaMovimentosResumidos(
    @RequestParam(value = "pageSize", required = false, defaultValue = "20") String pageSize,
    @RequestParam(value = "pageNumber", required = false, defaultValue = "0") String pageNumber,
    @RequestParam(value = "colaborador", required = false) String colaborador,
    @RequestParam(value = "funcionarioUuid", required = false) String funcionarioUuid,
    @RequestParam(value = "mes", required = false) Integer mes,
    @RequestParam(value = "ano", required = false) Integer ano,
    @RequestParam(value = "estado", required = false) String estado,
    @RequestParam(value = "ilha", required = false) Long ilha,
    @RequestParam(value = "direcao", required = false) Long direcao,
    @RequestParam(value = "seccao", required = false) Long seccao)
  {

      final var query = new GetListaMovimentosResumidosQuery(pageSize, pageNumber, colaborador, funcionarioUuid, mes, ano, estado, ilha, direcao, seccao);

      return queryBus.handle(query);

  }

   @PostMapping(
   value = "falta"
  )
  @Operation(
    summary = "Marcar falta",
    description = "Marcar falta",
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

   public ResponseEntity<Map<String, ?>> marcarFalta(@Valid @RequestBody FaltaReqDTO marcarFaltaRequest
    )
  {

      final var command = new MarcarFaltaCommand(marcarFaltaRequest);

      return commandBus.send(command);

  }

   @PostMapping(
   value = "falta/{pedidoId}"
  )
  @Operation(
    summary = "Validar falta",
    description = "Validar falta",
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

   public ResponseEntity<Map<String, ?>> validarFalta(@Valid @RequestBody FaltaReqDTO validarFaltaRequest
    , @PathVariable(value = "pedidoId") String pedidoId)
  {

      final var command = new ValidarFaltaCommand(validarFaltaRequest, pedidoId);

      return commandBus.send(command);

  }

   @GetMapping(
   value = "falta"
  )
  @Operation(
    summary = "Get lista falta",
    description = "Get lista falta",
    responses = {
      @ApiResponse(
          responseCode = "200",

          content = @Content(
              mediaType = "application/json",
              schema = @Schema(
                  implementation = WrapperListaFaltaDTO.class,
                  type = "object")
          )
      )
    }
  )

   public ResponseEntity<WrapperListaFaltaDTO> getListaFalta(
    @RequestParam(value = "pageNumber", required = false, defaultValue = "0") String pageNumber,
    @RequestParam(value = "pageSize", required = false, defaultValue = "20") String pageSize,
    @RequestParam(value = "colaborador", required = false) String colaborador,
    @RequestParam(value = "funcionarioUuid", required = false) String funcionarioUuid,
    @RequestParam(value = "ilha", required = false) Long ilha,
    @RequestParam(value = "direcao", required = false) Long direcao,
    @RequestParam(value = "seccao", required = false) Long seccao,
    @RequestParam(value = "dataInicio", required = false) String dataInicio,
    @RequestParam(value = "dataFim", required = false) String dataFim,
    @RequestParam(value = "estado", required = false) String estado)
  {

      final var query = new GetListaFaltaQuery(pageNumber, pageSize, colaborador, funcionarioUuid, ilha, direcao, seccao, dataInicio, dataFim, estado);

      return queryBus.handle(query);

  }

   @GetMapping(
   value = "dispensa"
  )
  @Operation(
    summary = "Get lista dispensa",
    description = "Get lista dispensa",
    responses = {
      @ApiResponse(
          responseCode = "200",

          content = @Content(
              mediaType = "application/json",
              schema = @Schema(
                  implementation = WrapperListaDispensaDTO.class,
                  type = "object")
          )
      )
    }
  )

   public ResponseEntity<WrapperListaDispensaDTO> getListaDispensa(
    @RequestParam(value = "pageNumber", required = false, defaultValue = "0") String pageNumber,
    @RequestParam(value = "pageSize", required = false, defaultValue = "20") String pageSize,
    @RequestParam(value = "colaborador", required = false) String colaborador,
    @RequestParam(value = "funcionarioUuid", required = false) String funcionarioUuid,
    @RequestParam(value = "ilha", required = false) Long ilha,
    @RequestParam(value = "direcao", required = false) Long direcao,
    @RequestParam(value = "seccao", required = false) Long seccao,
    @RequestParam(value = "dataInicio", required = false) String dataInicio,
    @RequestParam(value = "dataFim", required = false) String dataFim,
    @RequestParam(value = "estado", required = false) String estado)
  {

      final var query = new GetListaDispensaQuery(pageNumber, pageSize, colaborador, funcionarioUuid, ilha, direcao, seccao, dataInicio, dataFim, estado);

      return queryBus.handle(query);

  }

   @PostMapping(
   value = "dispensa"
  )
  @Operation(
    summary = "Marcar dispensa",
    description = "Marcar dispensa",
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

   public ResponseEntity<Map<String, ?>> marcarDispensa(@Valid @RequestBody DispensaReqDTO marcarDispensaRequest
    )
  {

      final var command = new MarcarDispensaCommand(marcarDispensaRequest);

      return commandBus.send(command);

  }

   @PutMapping(
   value = "dispensa/{pedidoId}/validar"
  )
  @Operation(
    summary = "Validar dispensa",
    description = "Validar dispensa",
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

   public ResponseEntity<Map<String, ?>> validarDispensa(@Valid @RequestBody DispensaReqDTO validarDispensaRequest
    , @PathVariable(value = "pedidoId") String pedidoId)
  {

      final var command = new ValidarDispensaCommand(validarDispensaRequest, pedidoId);

      return commandBus.send(command);

  }

   @GetMapping(
   value = "hora-extra"
  )
  @Operation(
    summary = "Get lista hora extra",
    description = "Get lista hora extra",
    responses = {
      @ApiResponse(
          responseCode = "200",

          content = @Content(
              mediaType = "application/json",
              schema = @Schema(
                  implementation = WrapperListaHoraExtraDTO.class,
                  type = "object")
          )
      )
    }
  )

   public ResponseEntity<WrapperListaHoraExtraDTO> getListaHoraExtra(
    @RequestParam(value = "pageNumber", required = false, defaultValue = "0") String pageNumber,
    @RequestParam(value = "pageSize", required = false, defaultValue = "20") String pageSize,
    @RequestParam(value = "ilha", required = false) Long ilha,
    @RequestParam(value = "direcao", required = false) Long direcao,
    @RequestParam(value = "seccao", required = false) Long seccao,
    @RequestParam(value = "dataInicio", required = false) String dataInicio,
    @RequestParam(value = "dataFim", required = false) String dataFim,
    @RequestParam(value = "funcionarioUuid", required = false) String funcionarioUuid)
  {

      final var query = new GetListaHoraExtraQuery(pageNumber, pageSize, ilha, direcao, seccao, dataInicio, dataFim, funcionarioUuid);

      return queryBus.handle(query);

  }

   @PostMapping(
   value = "hora-extra"
  )
  @Operation(
    summary = "Marcar hora extra",
    description = "Marcar hora extra",
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

   public ResponseEntity<Map<String, ?>> marcarHoraExtra(@Valid @RequestBody HoraExtraReqDTO marcarHoraExtraRequest
    )
  {

      final var command = new MarcarHoraExtraCommand(marcarHoraExtraRequest);

      return commandBus.send(command);

  }

   @PostMapping(
   value = "hora-extra/{pedidoId}"
  )
  @Operation(
    summary = "Validar hora extra",
    description = "Validar hora extra",
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

   public ResponseEntity<Map<String, ?>> validarHoraExtra(@Valid @RequestBody HoraExtraReqDTO validarHoraExtraRequest
    , @PathVariable(value = "pedidoId") String pedidoId)
  {

      final var command = new ValidarHoraExtraCommand(validarHoraExtraRequest, pedidoId);

      return commandBus.send(command);

  }

   @GetMapping(
   value = "hora-extra/calculo-valor"
  )
  @Operation(
    summary = "Calcular valor hora extra",
    description = "Calcula o valor diário de hora extra sem gravar. Usa o procedimento RH_PROCESSAMENTO_SALARIAL_DB.CALCULO_HORA_EXTRA.",
    responses = {
      @ApiResponse(
          responseCode = "200",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(
                  implementation = CalcValorHoraExtraDTO.class,
                  type = "object")
          )
      )
    }
  )

   public ResponseEntity<CalcValorHoraExtraDTO> getCalculoValorHoraExtra(
    @RequestParam(value = "funcionarioUuid") String funcionarioUuid,
    @RequestParam(value = "dataInicio") String dataInicio,
    @RequestParam(value = "dataFim") String dataFim,
    @RequestParam(value = "percentagemReferente") String percentagemReferente,
    @RequestParam(value = "horasDiaria") Long horasDiaria)
  {

      final var query = new GetCalcValorHoraExtraQuery(funcionarioUuid, dataInicio, dataFim, percentagemReferente, horasDiaria);

      return queryBus.handle(query);

  }

   @GetMapping(
   value = "feria"
  )
  @Operation(
    summary = "Get lista feria",
    description = "Get lista feria",
    responses = {
      @ApiResponse(
          responseCode = "200",

          content = @Content(
              mediaType = "application/json",
              schema = @Schema(
                  implementation = WrapperListaFeriaDTO.class,
                  type = "object")
          )
      )
    }
  )

   public ResponseEntity<WrapperListaFeriaDTO> getListaFeria(
    @RequestParam(value = "pageNumber", required = false, defaultValue = "0") String pageNumber,
    @RequestParam(value = "pageSize", required = false, defaultValue = "20") String pageSize,
    @RequestParam(value = "anoReferente", required = false) Integer anoReferente,
    @RequestParam(value = "ilha", required = false) Long ilha,
    @RequestParam(value = "direcao", required = false) Long direcao,
    @RequestParam(value = "seccao", required = false) Long seccao,
    @RequestParam(value = "colaborador", required = false) String colaborador,
    @RequestParam(value = "funcionarioUuid", required = false) String funcionarioUuid)
  {

      final var query = new GetListaFeriaQuery(pageNumber, pageSize, anoReferente, ilha, direcao, seccao, colaborador, funcionarioUuid);

      return queryBus.handle(query);

  }

   @PostMapping(
   value = "feria"
  )
  @Operation(
    summary = "Marcar feria",
    description = "Marcar feria",
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

   public ResponseEntity<Map<String, ?>> marcarFeria(@Valid @RequestBody PedidoFeriaReqDTO marcarFeriaRequest
    )
  {

      final var command = new MarcarFeriaCommand(marcarFeriaRequest);

      return commandBus.send(command);

  }

   @PutMapping(
   value = "feria/{pedidoId}"
  )
  @Operation(
    summary = "Alterar pedido feria",
    description = "Alterar pedido feria",
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

   public ResponseEntity<Map<String, ?>> alterarPedidoFeria(@Valid @RequestBody PedidoFeriaAlterarReqDTO alterarPedidoFeriaRequest
    , @PathVariable(value = "pedidoId") String pedidoId)
  {

      final var command = new AlterarPedidoFeriaCommand(alterarPedidoFeriaRequest, pedidoId);

      return commandBus.send(command);

  }

   @PostMapping(
   value = "feria/{pedidoId}"
  )
  @Operation(
    summary = "Validar pedido feria",
    description = "Validar pedido feria",
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

   public ResponseEntity<Map<String, ?>> validarPedidoFeria(@Valid @RequestBody PedidoFeriaReqDTO validarPedidoFeriaRequest
    , @PathVariable(value = "pedidoId") String pedidoId)
  {

      final var command = new ValidarPedidoFeriaCommand(validarPedidoFeriaRequest, pedidoId);

      return commandBus.send(command);

  }

   @GetMapping(
   value = "mapa-feria-view"
  )
  @Operation(
    summary = "Ver mapa",
    description = "Ver mapa",
    responses = {
      @ApiResponse(
          responseCode = "200",

          content = @Content(
              mediaType = "application/json",
              schema = @Schema(
                  implementation = VerMapaDTO.class,
                  type = "object")
          )
      )
    }
  )

   public ResponseEntity<VerMapaDTO> verMapa(
    @RequestParam(value = "ano", required = false) Integer ano,
    @RequestParam(value = "direcaoId", required = false) Long direcaoId,
    @RequestParam(value = "funcionarioUuid", required = false) String funcionarioUuid)
  {

      final var query = new VerMapaQuery(ano, direcaoId, funcionarioUuid);

      return queryBus.handle(query);

  }

   @GetMapping(
   value = "mapa-feria"
  )
  @Operation(
    summary = "Lista mapa feria",
    description = "Lista mapa feria",
    responses = {
      @ApiResponse(
          responseCode = "200",

          content = @Content(
              mediaType = "application/json",
              schema = @Schema(
                  implementation = WrapperListaMapaFeriaDTO.class,
                  type = "object")
          )
      )
    }
  )

   public ResponseEntity<WrapperListaMapaFeriaDTO> listaMapaFeria(
    @RequestParam(value = "pageNumber", required = false, defaultValue = "0") String pageNumber,
    @RequestParam(value = "pageSize", required = false, defaultValue = "20") String pageSize,
    @RequestParam(value = "anoReferente", required = false) Integer anoReferente,
    @RequestParam(value = "ilha", required = false) Long ilha,
    @RequestParam(value = "direcao", required = false) Long direcao,
    @RequestParam(value = "seccao", required = false) Long seccao,
    @RequestParam(value = "estado", required = false) String estado)
  {

      final var query = new ListaMapaFeriaQuery(pageNumber, pageSize, anoReferente, ilha, direcao, seccao, estado);

      return queryBus.handle(query);

  }

   @GetMapping(
   value = "mapa-feria/detalhe"
  )
  @Operation(
    summary = "Get detalhe mapa feria",
    description = "Get detalhe mapa feria",
    responses = {
      @ApiResponse(
          responseCode = "200",

          content = @Content(
              mediaType = "application/json",
              schema = @Schema(
                  implementation = DetalheMapaFeriaDTO.class,
                  type = "object")
          )
      )
    }
  )

   public ResponseEntity<DetalheMapaFeriaDTO> getDetalheMapaFeria(
    @RequestParam(value = "ano") Integer ano,
    @RequestParam(value = "direcao") Long direcao)
  {

      final var query = new GetDetalheMapaFeriaQuery(ano, direcao);

      return queryBus.handle(query);

  }

   @PostMapping(
   value = "falta/justificar/{funcionarioId}"
  )
  @Operation(
    summary = "Justificar falta",
    description = "Justificar falta",
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

   public ResponseEntity<Map<String, ?>> justificarFalta(@Valid @RequestBody JustificarFaltaDTO justificarFaltaRequest
    , @PathVariable(value = "funcionarioId") String funcionarioId)
  {

      final var command = new JustificarFaltaCommand(justificarFaltaRequest, funcionarioId);

      return commandBus.send(command);

  }

   @GetMapping(
   value = "falta/{pedidoId}"
  )
  @Operation(
    summary = "Get falta",
    description = "Get falta",
    responses = {
      @ApiResponse(
          responseCode = "200",

          content = @Content(
              mediaType = "application/json",
              schema = @Schema(
                  implementation = FaltaReqDTO.class,
                  type = "object")
          )
      )
    }
  )

   public ResponseEntity<FaltaReqDTO> getFalta(
    @PathVariable(value = "pedidoId") String pedidoId)
  {

      final var query = new GetFaltaQuery(pedidoId);

      return queryBus.handle(query);

  }

   @GetMapping(
   value = "dispensa/{dispensaId}"
  )
  @Operation(
    summary = "Get dispensa",
    description = "Get dispensa",
    responses = {
      @ApiResponse(
          responseCode = "200",

          content = @Content(
              mediaType = "application/json",
              schema = @Schema(
                  implementation = DispensaReqDTO.class,
                  type = "object")
          )
      )
    }
  )

   public ResponseEntity<DispensaReqDTO> getDispensa(
    @PathVariable(value = "dispensaId") String dispensaId)
  {

      final var query = new GetDispensaQuery(dispensaId);

      return queryBus.handle(query);

  }

   @GetMapping(
   value = "hora-extra/{pedidoId}"
  )
  @Operation(
    summary = "Get hora extra",
    description = "Get hora extra",
    responses = {
      @ApiResponse(
          responseCode = "200",

          content = @Content(
              mediaType = "application/json",
              schema = @Schema(
                  implementation = HoraExtraReqDTO.class,
                  type = "object")
          )
      )
    }
  )

   public ResponseEntity<HoraExtraReqDTO> getHoraExtra(
    @PathVariable(value = "pedidoId") String pedidoId)
  {

      final var query = new GetHoraExtraQuery(pedidoId);

      return queryBus.handle(query);

  }

   @GetMapping(
   value = "feria/{pedidoId}"
  )
  @Operation(
    summary = "Get pedido feria",
    description = "Get pedido feria",
    responses = {
      @ApiResponse(
          responseCode = "200",

          content = @Content(
              mediaType = "application/json",
              schema = @Schema(
                  implementation = PedidoFeriaAlterarReqDTO.class,
                  type = "object")
          )
      )
    }
  )

   public ResponseEntity<PedidoFeriaAlterarReqDTO> getPedidoFeria(
    @PathVariable(value = "pedidoId") String pedidoId)
  {

      final var query = new GetPedidoFeriaQuery(pedidoId);

      return queryBus.handle(query);

  }

   @PutMapping(
   value = "falta/justificar/validar/{pedidoId}"
  )
  @Operation(
    summary = "Validar falta justificada",
    description = "Validar falta justificada",
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

   public ResponseEntity<Map<String, ?>> validarFaltaJustificada(@Valid @RequestBody JustificarFaltaDTO validarFaltaJustificadaRequest
    , @PathVariable(value = "pedidoId") String pedidoId)
  {

      final var command = new ValidarFaltaJustificadaCommand(validarFaltaJustificadaRequest, pedidoId);

      return commandBus.send(command);

  }

   @GetMapping(
   value = "falta/justificar/{funcionarioId}"
  )
  @Operation(
    summary = "Get justificacao falta",
    description = "Get justificacao falta",
    responses = {
      @ApiResponse(
          responseCode = "200",

          content = @Content(
              mediaType = "application/json",
              schema = @Schema(
                  implementation = JustificarFaltaDTO.class,
                  type = "object")
          )
      )
    }
  )

   public ResponseEntity<JustificarFaltaDTO> getJustificacaoFalta(
    @RequestParam(value = "ano") Integer ano,
    @RequestParam(value = "mes") Integer mes, @PathVariable(value = "funcionarioId") String funcionarioId)
  {

      final var query = new GetJustificacaoFaltaQuery(ano, mes, funcionarioId);

      return queryBus.handle(query);

  }

   @GetMapping(
   value = "falta/justificar/pedido/{pedidoId}"
  )
  @Operation(
    summary = "Get justificacao falta by pedido",
    description = "Get justificacao falta by pedido",
    responses = {
      @ApiResponse(
          responseCode = "200",

          content = @Content(
              mediaType = "application/json",
              schema = @Schema(
                  implementation = JustificarFaltaDTO.class,
                  type = "object")
          )
      )
    }
  )

   public ResponseEntity<JustificarFaltaDTO> getJustificacaoFaltaByPedido(
    @PathVariable(value = "pedidoId") String pedidoId)
  {

      final var query = new GetJustificacaoFaltaByPedidoQuery(pedidoId);

      return queryBus.handle(query);

  }

   @GetMapping(
   value = "dispensa/{pedidoId}/validacao"
  )
  @Operation(
    summary = "Get dispensa by pedido id",
    description = "Get dispensa by pedido id",
    responses = {
      @ApiResponse(
          responseCode = "200",

          content = @Content(
              mediaType = "application/json",
              schema = @Schema(
                  implementation = DispensaReqDTO.class,
                  type = "object")
          )
      )
    }
  )

   public ResponseEntity<DispensaReqDTO> getDispensaByPedidoId(
    @PathVariable(value = "pedidoId") String pedidoId)
  {

      final var query = new GetDispensaByPedidoIdQuery(pedidoId);

      return queryBus.handle(query);

  }

   @PutMapping(
   value = "dispensa/{dispensaId}"
  )
  @Operation(
    summary = "Update dispensa",
    description = "Update dispensa",
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

   public ResponseEntity<Map<String, ?>> updateDispensa(@Valid @RequestBody DispensaReqDTO updateDispensaRequest
    , @PathVariable(value = "dispensaId") String dispensaId)
  {

      final var command = new UpdateDispensaCommand(updateDispensaRequest, dispensaId);

      return commandBus.send(command);

  }

   @GetMapping(
   value = "feria/saldo/{funcionarioId}"
  )
  @Operation(
    summary = "Get funcioario saldo ferias",
    description = "Get funcioario saldo ferias",
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

   public ResponseEntity<Map<String, ?>> getFuncioarioSaldoFerias(
    @RequestParam(value = "ano", required = false) Integer ano, @PathVariable(value = "funcionarioId") String funcionarioId)
  {

      final var query = new GetFuncioarioSaldoFeriasQuery(ano, funcionarioId);

      return queryBus.handle(query);

  }

   @GetMapping(
   value = "dispensa/saldo/{funcionarioId}"
  )
  @Operation(
    summary = "Get funcionario saldo dispensa",
    description = "Get funcionario saldo dispensa",
    responses = {
      @ApiResponse(
          responseCode = "200",

          content = @Content(
              mediaType = "application/json",
              schema = @Schema(
                  implementation = HorasDispensaStatusDTO.class,
                  type = "object")
          )
      )
    }
  )

   public ResponseEntity<HorasDispensaStatusDTO> getFuncionarioSaldoDispensa(
    @RequestParam(value = "data") String data, @PathVariable(value = "funcionarioId") String funcionarioId)
  {

      final var query = new GetFuncionarioSaldoDispensaQuery(data, funcionarioId);

      return queryBus.handle(query);

  }

  @PostMapping(
    value = "feria/{pedidoId}/direito-ferias"
  )
  @Operation(
    summary = "Enviar direito de ferias",
    description = "Envia email dos direitos de ferias ao colaborador e ao responsavel",
    responses = {
      @ApiResponse(
          responseCode = "200",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = String.class, type = "String")
          )
      )
    }
  )
  public ResponseEntity<Map<String, ?>> enviarDireitoFerias(
      @PathVariable(value = "pedidoId") String pedidoId) {
    final var command = new EnviarDireitoFeriasCommand(pedidoId);
    return commandBus.send(command);
  }

}
