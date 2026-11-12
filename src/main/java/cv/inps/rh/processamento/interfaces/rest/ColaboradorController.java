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
import cv.inps.rh.processamento.application.queries.GetBaixaMedicaQuery;
import cv.inps.rh.processamento.application.queries.GetListaBaixamedicaQuery;
import cv.inps.rh.processamento.application.queries.GetListaLicensaSemVencimentoQuery;
import cv.inps.rh.processamento.application.queries.GetMovimentosImportadosQuery;
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
        description = """
            Importa movimentos a partir de um ficheiro Excel (.xlsx ou .xls), enviado no campo `ficheiro`.
            Modelo de exemplo: `docs/modelos/importacao_movimentos_modelo.xlsx`.

            Só é lida a primeira folha (sheet). A linha 1 é o cabeçalho e é ignorada. Linhas com a coluna A vazia
            são ignoradas.

            Colunas:

            - Coluna A (índice 0): UUID do Funcionário — obrigatório
              - UUID de um funcionário existente (RH_T_FUNCIONARIOS.UUID).

            - Coluna B (índice 1): Nome do Funcionário — informativo
              - Não é lido pelo sistema; serve só de referência para quem preenche, mas a coluna tem de existir nesta posição.

            - Coluna C (índice 2): ID do Tipo de Movimento de Retenção
              - ID numérico de RH_T_TIPO_MOVIMENTO (ex: 205). Ao validar o movimento cria uma definição de pagamento.

            - Coluna D (índice 3): ID do Tipo de Movimento de Remuneração
              - ID numérico de RH_T_TIPO_MOVIMENTO (ex: 101). Ao validar o movimento cria uma definição de remuneração.

            - Coluna E (índice 4): Percentagem — opcional
              - Número decimal (ex: 10.5). Aceita célula numérica ou texto, com ponto ou vírgula.

            - Coluna F (índice 5): Valor — opcional
              - Número decimal (ex: 2500.00). Aceita célula numérica ou texto, com ponto ou vírgula.

            - Coluna G (índice 6): Data de Início — opcional
              - Texto num dos formatos dd/MM/aaaa, dd-MM-aaaa, aaaa-MM-dd ou aaaa/MM/dd (ex: 01/01/2026).
                Uma célula de data do Excel é lida no formato regional do ficheiro e pode não ser reconhecida.

            - Coluna H (índice 7): Data de Fim — opcional
              - Mesmos formatos da Data de Início; não pode ser anterior à Data de Início.

            - Coluna I (índice 8): Situação — opcional
              - Texto livre.

            Cada linha válida origina um registo em RH_T_IMPORTACAO_MOVIMENTO, associado ao funcionário da coluna A.

            A importação é tudo ou nada: se alguma linha tiver erro (UUID inválido ou inexistente, número ou data
            inválidos, Data de Fim anterior à Data de Início), o pedido devolve 400 com a lista de erros por linha
            (ex: "Linha 3: Valor inválido (abc)") e nenhuma linha é gravada. Um ficheiro que não seja Excel, ou sem
            movimentos, também devolve 400.
            """,
        responses = {
            @ApiResponse(
                responseCode = "200",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))
            ),
            @ApiResponse(
                responseCode = "400",
                description = "Ficheiro inválido ou com linhas inválidas; o detalhe traz a lista de erros por linha."
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

  @GetMapping(value = "baixa-medica/{baixaMedicaId}")
    @Operation(
        summary = "Get baixa medica",
        description = "Get baixa medica",
        responses = {
            @ApiResponse(
                responseCode = "200",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = BaixaMedicaDetailDTO.class)
                )
            ),
        }
    )
  public ResponseEntity<BaixaMedicaDetailDTO> getBaixaMedica(
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

    @PostMapping(value = "baixa-medica/{abonoId}")
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
        @PathVariable(value = "abonoId") String abonoId
    ) {
      final var command = new ValidarBaixaMedicaCommand(validarBaixaMedicaRequest, validar, abonoId);

        return commandBus.send(command);
    }
}
