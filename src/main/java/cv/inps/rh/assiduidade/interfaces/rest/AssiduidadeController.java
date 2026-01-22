/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME */

package cv.inps.rh.assiduidade.interfaces.rest;

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
import cv.inps.rh.assiduidade.application.queries.*;
import cv.igrp.framework.core.domain.CommandBus;
import cv.inps.rh.assiduidade.application.commands.*;
import cv.inps.rh.assiduidade.application.dto.WrapperListaPicagemDTO;
import cv.inps.rh.assiduidade.application.dto.WrapperListaAssiduidadadeDTO;
import cv.inps.rh.assiduidade.application.dto.FaltaReqDTO;
import java.util.Map;
import cv.inps.rh.assiduidade.application.dto.WrapperListaFaltaDTO;
import cv.inps.rh.assiduidade.application.dto.WrapperListaDispensaDTO;
import cv.inps.rh.assiduidade.application.dto.DispensaReqDTO;
import cv.inps.rh.assiduidade.application.dto.WrapperListaHoraExtraDTO;
import cv.inps.rh.assiduidade.application.dto.HoraExtraReqDTO;
import cv.inps.rh.assiduidade.application.dto.WrapperListaFeriaDTO;
import cv.inps.rh.assiduidade.application.dto.PedidoFeriaReqDTO;
import cv.inps.rh.assiduidade.application.dto.PedidoFeriaAlterarReqDTO;
import cv.inps.rh.assiduidade.application.dto.VerMapaDTO;
import cv.inps.rh.assiduidade.application.dto.WrapperListaMapaFeriaDTO;
import cv.inps.rh.assiduidade.application.dto.DetalheMapaFeriaDTO;
import cv.inps.rh.assiduidade.application.dto.JustificarFaltaDTO;

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
    @RequestParam(value = "nomeColaborador", required = false) String nomeColaborador,
    @RequestParam(value = "direcao", required = false) Long direcao,
    @RequestParam(value = "seccao", required = false) Long seccao,
    @RequestParam(value = "ups", required = false) Long ups,
    @RequestParam(value = "dataInicio", required = false) String dataInicio,
    @RequestParam(value = "dataFim", required = false) String dataFim)
  {

      final var query = new GetListaPicagemQuery(pageSize, pageNumber, nomeColaborador, direcao, seccao, ups, dataInicio, dataFim);

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
    @RequestParam(value = "dataInicio", required = false) String dataInicio,
    @RequestParam(value = "dataFim", required = false) String dataFim,
    @RequestParam(value = "estado", required = false) String estado,
    @RequestParam(value = "ilha", required = false) Long ilha,
    @RequestParam(value = "direcao", required = false) Long direcao,
    @RequestParam(value = "seccao", required = false) Long seccao)
  {

      final var query = new GetListaMovimentosResumidosQuery(pageSize, pageNumber, colaborador, dataInicio, dataFim, estado, ilha, direcao, seccao);

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
   value = "falta/{faltaId}"
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
    , @PathVariable(value = "faltaId") String faltaId)
  {

      final var command = new ValidarFaltaCommand(validarFaltaRequest, faltaId);

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
    @RequestParam(value = "ilha", required = false) Long ilha,
    @RequestParam(value = "direcao", required = false) Long direcao,
    @RequestParam(value = "seccao", required = false) Long seccao,
    @RequestParam(value = "dataInicio", required = false) String dataInicio,
    @RequestParam(value = "dataFim", required = false) String dataFim,
    @RequestParam(value = "estado", required = false) String estado)
  {

      final var query = new GetListaFaltaQuery(pageNumber, pageSize, colaborador, ilha, direcao, seccao, dataInicio, dataFim, estado);

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
    @RequestParam(value = "ilha", required = false) Long ilha,
    @RequestParam(value = "direcao", required = false) Long direcao,
    @RequestParam(value = "seccao", required = false) Long seccao,
    @RequestParam(value = "dataInicio", required = false) String dataInicio,
    @RequestParam(value = "dataFim", required = false) String dataFim,
    @RequestParam(value = "estado", required = false) String estado)
  {

      final var query = new GetListaDispensaQuery(pageNumber, pageSize, colaborador, ilha, direcao, seccao, dataInicio, dataFim, estado);

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

   @PostMapping(
   value = "dispensa/{dispensaId}"
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
    , @PathVariable(value = "dispensaId") String dispensaId)
  {

      final var command = new ValidarDispensaCommand(validarDispensaRequest, dispensaId);

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
    @RequestParam(value = "dataFim", required = false) String dataFim)
  {

      final var query = new GetListaHoraExtraQuery(pageNumber, pageSize, ilha, direcao, seccao, dataInicio, dataFim);

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
   value = "hora-extra/{horaExtraId}"
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
    , @PathVariable(value = "horaExtraId") String horaExtraId)
  {

      final var command = new ValidarHoraExtraCommand(validarHoraExtraRequest, horaExtraId);

      return commandBus.send(command);

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
    @RequestParam(value = "colaborador", required = false) String colaborador)
  {

      final var query = new GetListaFeriaQuery(pageNumber, pageSize, anoReferente, ilha, direcao, seccao, colaborador);

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
   value = "feria/{feriaId}"
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
    , @PathVariable(value = "feriaId") String feriaId)
  {

      final var command = new AlterarPedidoFeriaCommand(alterarPedidoFeriaRequest, feriaId);

      return commandBus.send(command);

  }

   @PostMapping(
   value = "feria/{feriaId}"
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
    , @PathVariable(value = "feriaId") String feriaId)
  {

      final var command = new ValidarPedidoFeriaCommand(validarPedidoFeriaRequest, feriaId);

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
    )
  {

      final var query = new VerMapaQuery();

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
   value = "mapa-feria/{mapaFeriaId}"
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
    @PathVariable(value = "mapaFeriaId") String mapaFeriaId)
  {

      final var query = new GetDetalheMapaFeriaQuery(mapaFeriaId);

      return queryBus.handle(query);

  }

   @PostMapping(
   value = "falta/justificar/{faltaId}"
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
    , @PathVariable(value = "faltaId") String faltaId)
  {

      final var command = new JustificarFaltaCommand(justificarFaltaRequest, faltaId);

      return commandBus.send(command);

  }

   @GetMapping(
   value = "falta/{faltaId}"
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
    @PathVariable(value = "faltaId") String faltaId)
  {

      final var query = new GetFaltaQuery(faltaId);

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
   value = "hora-extra/{horaExtraId}"
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
    @PathVariable(value = "horaExtraId") String horaExtraId)
  {

      final var query = new GetHoraExtraQuery(horaExtraId);

      return queryBus.handle(query);

  }

   @GetMapping(
   value = "feria/{feriaId}"
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
    @PathVariable(value = "feriaId") String feriaId)
  {

      final var query = new GetPedidoFeriaQuery(feriaId);

      return queryBus.handle(query);

  }

   @PutMapping(
   value = "falta/justificar/validar/{faltaId}"
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
    , @PathVariable(value = "faltaId") String faltaId)
  {

      final var command = new ValidarFaltaJustificadaCommand(validarFaltaJustificadaRequest, faltaId);

      return commandBus.send(command);

  }

}