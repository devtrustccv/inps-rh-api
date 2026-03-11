package cv.inps.rh.progressaopromocao.domain.service.engine;

import cv.inps.rh.avaliacao.application.services.AvaliacaoService;
import cv.inps.rh.progressaopromocao.domain.service.engine.model.ProgessionPromotionType;
import cv.inps.rh.progressaopromocao.domain.service.engine.rule.DisciplinaService;
import cv.inps.rh.progressaopromocao.domain.service.engine.rule.FaltaService;
import cv.inps.rh.shared.infrastructure.persistence.entity.CarreiraEntity;
import cv.inps.rh.shared.infrastructure.persistence.repository.CarreiraEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.EvolucaoCarreiraEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class PromocaoService {

  private final EvolucaoCarreiraEntityRepository evolucaoRepository;
  private final CarreiraEntityRepository carreiraRepository;
  private final AvaliacaoService avaliacaoService;
  private final FaltaService faltaService;
  private final DisciplinaService disciplinaService;
  private final SimulacaoService simulacaoService;

  public void simular() {

    // TODO 05/03/2026 18:00 with only carreiras base without cargo
    var carreiras = carreiraRepository.findCarreirasAtivas();

    for (var carreira : carreiras) {

      if (!validaElegibilidadePromocao(carreira)) continue;

      if (!atingiuTempoPromocao(carreira)) continue;

      if (!podePromoverNovamente(carreira)) continue;

      var media = avaliacaoService.calcularMedia(
          carreira.getContrVinculoId().getFunId(),
          2
      );

      if (!media.elegivelPromocao()) continue;

      if (!faltaService.valida(carreira)) continue;

      if (!disciplinaService.valida(carreira)) continue;

      simulacaoService.registarSimulacao(carreira, media, ProgessionPromotionType.PROMOCAO);
    }
  }

  /**
   * Regras básicas para promoção
   */
  private boolean validaElegibilidadePromocao(CarreiraEntity carreira) {

    // deve ter contrato
    if (carreira.getContrVinculoId() == null) return false;

    // deve ter funcionário
    if (carreira.getContrVinculoId().getFunId() == null) return false;

    // diretores não promovem
    if (carreira.getCargoId() != null
        && carreira.getCargoId().getNome() != null
        && carreira.getCargoId().getNome().toLowerCase().contains("diretor")) {
      return false;
    }

    // não pode ter sido promoção anterior
    if ("M".equalsIgnoreCase(carreira.getTipoSituacao())) {
      return false;
    }

    return true;
  }

  /**
   * Regra tempo mínimo para promoção
   * - 6 anos desde entrada como efetivo (por agora simplificado)
   */
  private boolean atingiuTempoPromocao(CarreiraEntity carreira) {

    if (carreira.getDataInicio() == null) return false;

    var dataElegibilidade = carreira.getDataInicio().plusYears(6);

    return !LocalDate.now().isBefore(dataElegibilidade);
  }

  private boolean podePromoverNovamente(CarreiraEntity carreira) {

    var ultima =
        evolucaoRepository.findUltimaEvolucao(
            carreira.getId(),
            PageRequest.of(0, 1)
        );

    if (ultima.isEmpty())
      return true; // nunca evoluiu

    var evolucao = ultima.getFirst();

    // Se não foi promoção, pode promover
    if (!"M".equalsIgnoreCase(evolucao.getTipo())) {
      return true;
    }

    // Se foi promoção, verificar 3 anos
    LocalDate dataUltimaPromocao = evolucao.getDataReferente();

    LocalDate dataLimite =
        dataUltimaPromocao.plusYears(3);

    return !LocalDate.now().isBefore(dataLimite);
  }
}
