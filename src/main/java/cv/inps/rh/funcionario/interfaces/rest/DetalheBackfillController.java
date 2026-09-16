package cv.inps.rh.funcionario.interfaces.rest;

import cv.inps.rh.funcionario.application.dto.DetalheBackfillResultadoDTO;
import cv.inps.rh.funcionario.application.service.detalhe.DetalheBackfillService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * <b>Temporário</b> — migra o detalhe de alterações das validações antigas do histórico do JaVers para
 * {@code RH_T_VALIDACAO_DETALHE}. Corre uma vez antes de o JaVers sair, e é removido com ele.
 *
 * <p>{@code dryRun=true} (o valor por omissão) só conta: nada é gravado. Idempotente — só toca em
 * validações ainda sem detalhe.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "api/v1/funcionarios/validacoes/detalhes")
public class DetalheBackfillController {

  private final DetalheBackfillService detalheBackfillService;

  @PostMapping("backfill")
  @Operation(summary = "Backfill do detalhe de alterações a partir do JaVers (temporário)")
  public ResponseEntity<DetalheBackfillResultadoDTO> backfill(
      @RequestParam(value = "dryRun", defaultValue = "true") boolean dryRun) {
    return ResponseEntity.ok(detalheBackfillService.executar(dryRun));
  }
}
