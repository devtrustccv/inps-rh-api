package cv.inps.rh.progressaopromocao.domain.service.engine;

import cv.inps.rh.progressaopromocao.domain.service.engine.model.ProgessionPromotionType;
import cv.inps.rh.progressaopromocao.domain.service.engine.rule.AvaliacaoService;
import cv.inps.rh.progressaopromocao.domain.service.engine.rule.DisciplinaService;
import cv.inps.rh.progressaopromocao.domain.service.engine.rule.FaltaService;
import cv.inps.rh.shared.infrastructure.persistence.entity.CarreiraEntity;
import cv.inps.rh.shared.infrastructure.persistence.repository.CarreiraEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.EvolucaoCarreiraEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PromocaoService {

  private final EvolucaoCarreiraEntityRepository evolucaoRepository;
  private final CarreiraEntityRepository carreiraRepository;
  private final AvaliacaoService avaliacaoService;
  private final FaltaService faltaService;
  private final DisciplinaService disciplinaService;
  private final SimulacaoService simulacaoService;

  public void simular(List<CarreiraEntity> careers) {

    for (var career : careers) {

      if (!validaElegibilidadePromocao(career)) continue;

      if (!atingiuTempoPromocao(career)) continue;

      if (!podePromoverNovamente(career)) continue;

      var media = avaliacaoService.calcularMedia(
          career.getContrVinculoId().getFunId(),
          2
      );
      if (!media.elegivelPromocao()) continue;

      if (!faltaService.valida(career)) continue;

      if (!disciplinaService.valida(career)) continue;

      simulacaoService.registarSimulacao(career, media, ProgessionPromotionType.PROMOCAO);
    }
  }

  /**
   * Regras básicas para promoção
   */
  private boolean validaElegibilidadePromocao(CarreiraEntity career) {

    // diretores não promovem
    if (career.getCargoId() != null)
      return false;

    // não pode ter tido promoção anterior
    return !ProgessionPromotionType.PROMOCAO.name().equals(career.getTipoSituacao());
  }

  /**
   * Regra tempo mínimo para promoção
   * - 6 anos desde entrada como efetivo (por agora simplificado)
   */
  private boolean atingiuTempoPromocao(CarreiraEntity career) {

    var dataElegibilidade = career.getDataInicio().plusYears(6);

    return !LocalDate.now().isBefore(dataElegibilidade);
  }

  private boolean podePromoverNovamente(CarreiraEntity career) {

    var ultima =
        evolucaoRepository.findUltimaEvolucao(
            career.getId(),
            PageRequest.of(0, 1)
        );
    if (ultima.isEmpty())
      return true; // nunca evoluiu

    var evolucao = ultima.getFirst();

    // Se não foi promoção, pode promover
    if (!ProgessionPromotionType.PROMOCAO.name().equals(evolucao.getTipo()))
      return true;

    // Se foi promoção, verificar 3 anos
    var dataUltimaPromocao = evolucao.getDataReferente();

    var dataLimite = dataUltimaPromocao.plusYears(3);

    return !LocalDate.now().isBefore(dataLimite);
  }
}
