package cv.inps.rh.shared.interfaces.rest;

import cv.inps.rh.funcionario.application.dto.ValidacaoDetalheDTO;
import cv.inps.rh.shared.application.service.JaversValidacaoDetalheReadService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

/**
 * Endpoint-piloto do "Detalhe de alterações" servido pelo JaVers, em paralelo ao existente
 * ({@code .../detalhes}, que lê de RH_T_VALIDACAO_DETALHE). Serve para comparar os dois lado a lado
 * durante a validação da abordagem; uma vez confirmado, o endpoint oficial passa a usar esta fonte
 * e este controller é removido.
 *
 * <p>Em controller próprio (não no FuncionarioController gerado) para não ser apagado numa
 * regeneração do iGRP Studio.
 */
@RestController
@RequestMapping(path = "api/v1/funcionarios/validacoes")
@RequiredArgsConstructor
public class JaversDetalhePilotController {

  private final JaversValidacaoDetalheReadService javersValidacaoDetalheReadService;

  @GetMapping("{idValidacao}/detalhes-javers")
  @Operation(
      summary = "[PILOTO] Detalhe de alteracoes via JaVers",
      description = "Mesma grelha 'Detalhe de alteracoes', mas com origem no historico do JaVers em vez de RH_T_VALIDACAO_DETALHE")
  public ResponseEntity<List<ValidacaoDetalheDTO>> getDetalheAlteracoesJavers(
      @PathVariable("idValidacao") String idValidacao) {
    return ResponseEntity.ok(javersValidacaoDetalheReadService.listar(UUID.fromString(idValidacao)));
  }
}
