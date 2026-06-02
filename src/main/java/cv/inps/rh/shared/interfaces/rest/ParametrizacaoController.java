package cv.inps.rh.shared.interfaces.rest;

import cv.igrp.framework.core.data.EnumItem;
import cv.igrp.framework.core.utils.object.EnumUtils;
import cv.inps.rh.parametrizacao.application.dto.ParametrizacaoDTO;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.application.service.ParametrizacaoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("ParametrizacaoController2")
@RequestMapping(path = "api/v1/parametrizacao")
@Tag(name = "Parametrizacao", description = "Modulo parametrizacao")
public class ParametrizacaoController {

  private final ParametrizacaoService parametrizacaoService;


  public ParametrizacaoController(ParametrizacaoService parametrizacaoService) {
    this.parametrizacaoService = parametrizacaoService;
  }


  @GetMapping("/tipo-movimento-desconto/ativos")
  @Operation(summary = "Lista tipos de movimento de desconto ativos")
  public ResponseEntity<List<ParametrizacaoDTO>> getTiposMovimentoPagamentosDescontoAtivos() {
    return ResponseEntity.ok(parametrizacaoService.getTiposMovimentosPagamentosDesconto());
  }

  @GetMapping("/tipo-movimento-renumeracao/ativos")
  @Operation(summary = "Lista tipos de movimento de remuneração ativos")
  public ResponseEntity<List<ParametrizacaoDTO>> getTiposMovimentoRemuneracaoAtivos() {
    return ResponseEntity.ok(parametrizacaoService.getTiposMovimentosRenumeracao());
  }

  @GetMapping("/instituicoes/ativos")
  @Operation(summary = "Lista tipos de movimento de remuneração ativos")
  public ResponseEntity<List<ParametrizacaoDTO>> getInstituicoesAtivas() {
    return ResponseEntity.ok(parametrizacaoService.getInstituicoes());
  }

  @GetMapping("/instituicoes/{institId}/centros-custo")
  @Operation(summary = "Obter centro de custo por instituição")
  public ResponseEntity<String> getCentroCustoByInstituicao(
      @PathVariable Long institId) {

    return ResponseEntity.ok(
        parametrizacaoService.getCentroByInstituicao(institId)
    );
  }

  @GetMapping("/geografias")
  @Operation(summary = "Lista geografias por nivelDetalhe e geogrId")
  public ResponseEntity<List<ParametrizacaoDTO>> getGeografias(
      @RequestParam Long nivelDetalhe,
      @RequestParam(required = false) Long geogrId
  ) {
    return ResponseEntity.ok(parametrizacaoService.getGeografias(nivelDetalhe, geogrId));
  }

  @GetMapping("/entidades/ativos")
  @Operation(summary = "Lista entidades ativas")
  public ResponseEntity<List<ParametrizacaoDTO>> getEntidadesAtivas() {
    return ResponseEntity.ok(parametrizacaoService.getEntidades());
  }

  @GetMapping("/bancos/ativos")
  @Operation(summary = "Lista bancos ativos")
  public ResponseEntity<List<ParametrizacaoDTO>> getBancosAtivos() {
    return ResponseEntity.ok(parametrizacaoService.getBancos());
  }

  @GetMapping("/estados")
  public ResponseEntity<List<EnumItem<String>>> getQualificacoes() {

    return ResponseEntity.ok(EnumUtils.mapEnumToItems(Estado.class));
  }

  @GetMapping("/vinculos/tipos-movimento")
  @Operation(
      summary = "Lista tipos de movimento associados a um vínculo",
      description = """
          Retorna os tipos de movimento (TipoMovimento) configurados para um determinado vínculo \
          na tabela RH_T_PARAM_VINCULO_MOV.

          **Regra de filtragem:** forneça **exactamente um** dos parâmetros:
          - `vinculoId` — identificador numérico do vínculo (`RH_T_PARAM_VINCULO.id`)
          - `vinculoUuid` — identificador UUID do vínculo (`RH_T_PARAM_VINCULO.uuid`)

          Enviar ambos ou nenhum resulta em `400 Bad Request`.
          """
  )
  @ApiResponses({
      @ApiResponse(
          responseCode = "200",
          description = "Lista de tipos de movimento encontrada com sucesso",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = ParametrizacaoDTO.class),
              examples = @ExampleObject(
                  name = "Exemplo de resposta",
                  value = """
                      [
                        { "label": "Vencimento Base", "value": 1 },
                        { "label": "Subsídio de Função", "value": 5 }
                      ]
                      """
              )
          )
      ),
      @ApiResponse(
          responseCode = "400",
          description = "Parâmetros inválidos — forneça exactamente um: vinculoId ou vinculoUuid",
          content = @Content(mediaType = "application/json")
      )
  })
  public ResponseEntity<List<ParametrizacaoDTO>> getTiposMovimentoByVinculo(
      @Parameter(
          description = "ID numérico do vínculo (RH_T_PARAM_VINCULO.id). Mutuamente exclusivo com `vinculoUuid`.",
          example = "3"
      )
      @RequestParam(required = false) Long vinculoId,
      @Parameter(
          description = "UUID do vínculo (RH_T_PARAM_VINCULO.uuid). Mutuamente exclusivo com `vinculoId`.",
          example = "550e8400-e29b-41d4-a716-446655440000"
      )
      @RequestParam(required = false) String vinculoUuid
  ) {
    return ResponseEntity.ok(
        parametrizacaoService.getTiposMovimentoByVinculo(vinculoId, vinculoUuid));
  }

}
