package cv.inps.rh.progressaopromocao.domain.service.engine;

import cv.inps.rh.avaliacao.application.services.AvaliacaoService;
import cv.inps.rh.progressaopromocao.domain.service.engine.model.ProgessionPromotionType;
import cv.inps.rh.progressaopromocao.domain.service.engine.rule.DisciplinaService;
import cv.inps.rh.progressaopromocao.domain.service.engine.rule.FaltaService;
import cv.inps.rh.shared.infrastructure.persistence.entity.CarreiraEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.VwRhProgressaoInputEntity;
import cv.inps.rh.shared.infrastructure.persistence.repository.CarreiraEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.EvolucaoCarreiraEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.VwRhProgressaoInputEntityRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class PromocaoService {

  private static final Logger LOGGER = LoggerFactory.getLogger(PromocaoService.class);

  private static final double MIN_MEDIA_AVALIACOES = 4.5;

  private final EvolucaoCarreiraEntityRepository evolucaoRepository;
  private final CarreiraEntityRepository carreiraRepository;
  private final AvaliacaoService avaliacaoService;
  private final FaltaService faltaService;
  private final DisciplinaService disciplinaService;
  private final SimulacaoService simulacaoService;
  private final VwRhProgressaoInputEntityRepository vwRhProgressaoInputEntityRepository;

  public void simular(VwRhProgressaoInputEntity c) {

    LOGGER.debug("\n---------------------------------------------------------------------------------------------------------------------------");
    LOGGER.debug("Simulando progressao para {}", c);

    if (c.getTipoCarreira().equals("DIRECTOR") || c.getTipoCarreira().equals("DIRECTOR_BASE")) {
      LOGGER.debug("Tipo de carreira não permitido para promoção");
      return;
    }

    // Verifica se já existe evolução ou tempo mínimo de progressão
    if (c.getExisteProgressao() == 0L && c.getAtingiuPrimeiraProgressao() == 0L) {
      LOGGER.debug("Sem progressao");
      return;
    }

    // Verifica se a média das avaliações atende ao mínimo para progressão
    var media = c.getMediaAvaliacoes2Anos();
    if (media >= MIN_MEDIA_AVALIACOES) {
      LOGGER.debug("Media {} >= 3.0, registrando simulacao", media);
      simulacaoService.registarProgressao(c, media);
      return;
    }

/*    if (!atingiuTempoPromocao(career)) continue;

    if (!podePromoverNovamente(career)) continue;

    if (!faltaService.valida(career)) continue;

    if (!disciplinaService.valida(career)) continue;*/

    simulacaoService.registarPromocao(c, media);
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
