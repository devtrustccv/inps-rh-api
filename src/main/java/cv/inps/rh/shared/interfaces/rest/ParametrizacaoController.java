package cv.inps.rh.shared.interfaces.rest;

import cv.igrp.framework.core.data.EnumItem;
import cv.igrp.framework.core.utils.object.EnumUtils;
import cv.inps.rh.parametrizacao.application.dto.ParametrizacaoDTO;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.application.service.ParametrizacaoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
  public ResponseEntity<List<ParametrizacaoDTO>> getTiposMovimentoDescontoAtivos() {
    return ResponseEntity.ok(parametrizacaoService.getTiposMovimentos());
  }

  @GetMapping("/tipo-movimento-renumeracao/ativos")
  @Operation(summary = "Lista tipos de movimento de remuneração ativos")
  public ResponseEntity<List<ParametrizacaoDTO>> getTiposMovimentoRemuneracaoAtivos() {
    return ResponseEntity.ok(parametrizacaoService.getTiposMovimentos());
  }

  @GetMapping("/instituicoes/ativos")
  @Operation(summary = "Lista tipos de movimento de remuneração ativos")
  public ResponseEntity<List<ParametrizacaoDTO>> getInstituicoesAtivas() {
    return ResponseEntity.ok(parametrizacaoService.getInstituicoes());
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

}
