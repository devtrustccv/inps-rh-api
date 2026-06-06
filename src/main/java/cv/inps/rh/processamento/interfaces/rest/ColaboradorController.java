/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME */

package cv.inps.rh.processamento.interfaces.rest;

import cv.igrp.framework.core.domain.CommandBus;
import cv.igrp.framework.core.domain.QueryBus;
import cv.igrp.framework.stereotype.IgrpController;
import cv.inps.rh.processamento.application.commands.CriarBaixaMedicaCommand;
import cv.inps.rh.processamento.application.commands.ImportarMovimentosCommand;
import cv.inps.rh.processamento.application.commands.ValidarBaixaMedicaCommand;
import cv.inps.rh.processamento.application.commands.ValidarMovimentoImportadoCommand;
import cv.inps.rh.processamento.application.dto.*;
import cv.inps.rh.processamento.application.queries.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@IgrpController
@RestController
@RequestMapping(path = "colaborador")
@Tag(name = "Processamento", description = "Colaborador")
public class ColaboradorController {

    private final QueryBus queryBus;
    private final CommandBus commandBus;

    public ColaboradorController(QueryBus queryBus, CommandBus commandBus) {
        this.queryBus = queryBus;
        this.commandBus = commandBus;
    }

    @GetMapping(value = "baixa-medica")
    @Operation(
        summary = "Get lista baixamedica",
        description = "Get lista baixamedica",
        responses = {
            @ApiResponse(
                responseCode = "200",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = BaixaMedicaListDTO.class)
                )
            ),
        }
    )
    public ResponseEntity<BaixaMedicaListDTO> getListaBaixamedica(
        @RequestParam(value = "dataInicio", required = false) String dataInicio,
        @RequestParam(value = "dataFim", required = false) String dataFim,
        @RequestParam(value = "nomeFuncionario", required = false) String nomeFuncionario,
        @RequestParam(value = "direccaoId", required = false) Long direccaoId,
        @RequestParam(value = "page", required = false, defaultValue = "0") Integer page,
        @RequestParam(value = "size", required = false, defaultValue = "20") Integer size,
        @RequestParam(value = "tipoAbonoBeneficioId", required = false) Long tipoAbonoBeneficioId
    ) {
        final var query = new GetListaBaixamedicaQuery(
            dataInicio,
            dataFim,
            nomeFuncionario,
            direccaoId,
            page,
            size,
            tipoAbonoBeneficioId
        );

        return queryBus.handle(query);
    }

    @GetMapping(value = "licensa-sem-vencimento")
    @Operation(
        summary = "Get lista licensa sem vencimento",
        description = "Get lista licensa sem vencimento",
        responses = {
            @ApiResponse(
                responseCode = "200",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = WrapperListaColaboradorDTO.class)
                )
            ),
        }
    )
    public ResponseEntity<WrapperListaColaboradorDTO> getListaLicensaSemVencimento(
        @RequestParam(value = "dataInicio", required = false) String dataInicio,
        @RequestParam(value = "dataFim", required = false) String dataFim,
        @RequestParam(value = "colaborador", required = false) String colaborador,
        @RequestParam(value = "direccao", required = false) String direccao,
        @RequestParam(value = "page", required = false, defaultValue = "0") String page,
        @RequestParam(value = "size", required = false, defaultValue = "20") String size
    ) {
        final var query = new GetListaLicensaSemVencimentoQuery(dataInicio, dataFim, colaborador, direccao, page, size);

        return queryBus.handle(query);
    }

    @PostMapping(value = "importar-movimento", consumes = "multipart/form-data")
    @Operation(
        summary = "Importar movimentos",
        description = "Importar movimentos",
        responses = {
            @ApiResponse(
                responseCode = "200",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))
            ),
        }
    )
    public ResponseEntity<String> importarMovimentos(@RequestParam(value = "ficheiro") MultipartFile ficheiro) {
        final var command = new ImportarMovimentosCommand(ficheiro);

        return commandBus.send(command);
    }

    @GetMapping(value = "movimentos")
    @Operation(
        summary = "Get movimentos importados",
        description = "Get movimentos importados",
        responses = {
            @ApiResponse(
                responseCode = "200",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = MovimentosImportadosDTO.class)
                )
            ),
        }
    )
    public ResponseEntity<MovimentosImportadosDTO> getMovimentosImportados(
        @RequestParam(value = "page", required = false, defaultValue = "0") String page,
        @RequestParam(value = "size", required = false, defaultValue = "20") String size,
        @RequestParam(value = "dataImportacao", required = false) String dataImportacao
    ) {
        final var query = new GetMovimentosImportadosQuery(page, size, dataImportacao);

        return queryBus.handle(query);
    }

    @PostMapping(value = "movimentos/{movimentoId}/validar")
    @Operation(
        summary = "Validar movimento importado",
        description = "Validar movimento importado",
        responses = {
            @ApiResponse(
                responseCode = "200",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))
            ),
        }
    )
    public ResponseEntity<String> validarMovimentoImportado(
        @Valid @RequestBody ValidacaoMovimentoImportadoDTO validarMovimentoImportadoRequest,
        @PathVariable(value = "movimentoId") String movimentoId
    ) {
        final var command = new ValidarMovimentoImportadoCommand(validarMovimentoImportadoRequest, movimentoId);

        return commandBus.send(command);
    }

    @GetMapping(value = "baixa-medica/calculo")
    @Operation(
        summary = "Calcular baixa medica",
        description = "Calcular baixa medica",
        responses = {
            @ApiResponse(
                responseCode = "200",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = BaixaMedicaCalculoDTO.class)
                )
            ),
        }
    )
    public ResponseEntity<BaixaMedicaCalculoDTO> calcularBaixaMedica(
        @RequestParam(value = "colaborador") String colaborador,
        @RequestParam(value = "dataInicio") String dataInicio,
        @RequestParam(value = "dataFim") String dataFim,
        @RequestParam(value = "tipoLicenca") String tipoLicenca,
        @RequestParam(value = "dataInicioFalta", required = false) String dataInicioFalta
    ) {
        final var query = new CalcularBaixaMedicaQuery(colaborador, dataInicio, dataFim, tipoLicenca, dataInicioFalta);

        return queryBus.handle(query);
    }

  @GetMapping(value = "baixa-medica/{baixaMedicaId}")
    @Operation(
        summary = "Get baixa medica",
        description = "Get baixa medica",
        responses = {
            @ApiResponse(
                responseCode = "200",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = BaixaMedicaRowDTO.class)
                )
            ),
        }
    )
  public ResponseEntity<BaixaMedicaRowDTO> getBaixaMedica(
      @PathVariable(value = "baixaMedicaId") String baixaMedicaId
  ) {
    final var query = new GetBaixaMedicaQuery(baixaMedicaId);

        return queryBus.handle(query);
    }

    @PostMapping(value = "baixa-medica")
    @Operation(
        summary = "Criar baixa medica",
        description = "Criar baixa medica",
        responses = {
            @ApiResponse(
                responseCode = "200",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))
            ),
        }
    )
    public ResponseEntity<String> criarBaixaMedica(@Valid @RequestBody BaixaMedicaReqDTO criarBaixaMedicaRequest) {
        final var command = new CriarBaixaMedicaCommand(criarBaixaMedicaRequest);

        return commandBus.send(command);
    }

    @PostMapping(value = "baixa-medica/{pedidoId}")
    @Operation(
        summary = "Validar baixa medica",
        description = "Validar baixa medica",
        responses = {
            @ApiResponse(
                responseCode = "200",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))
            ),
        }
    )
    public ResponseEntity<String> validarBaixaMedica(
        @Valid @RequestBody BaixaMedicaReqDTO validarBaixaMedicaRequest,
        @RequestParam(value = "validar") String validar,
        @PathVariable(value = "pedidoId") String pedidoId
    ) {
        final var command = new ValidarBaixaMedicaCommand(validarBaixaMedicaRequest, validar, pedidoId);

        return commandBus.send(command);
    }
}
