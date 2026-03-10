package cv.inps.rh.progressaopromocao.domain.service.engine;

import cv.inps.rh.progressaopromocao.domain.service.engine.model.ProgessionPromotionType;
import cv.inps.rh.shared.infrastructure.persistence.repository.VwRhProgressaoInputEntityRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
@RequiredArgsConstructor
public class ProgressaoService {

  private static final Logger LOGGER = LoggerFactory.getLogger(ProgressaoService.class);

  private final SimulacaoService simulacaoService;
  private final VwRhProgressaoInputEntityRepository vwRhProgressaoInputEntityRepository;

  public void simular() {

    // TODO 05/03/2026 17:41 colaborador em licença sem vencimento não deve progredir
    // TODO 05/03/2026 17:41 O colaborador não deve estar em situação laboral em que não evolui na carreira no período de progressão; deve iniciar a partir da situação laboral atual
    // TODO 10/03/2026 21:55 avaliar se já é possível filtrar na query

    for (var c : vwRhProgressaoInputEntityRepository.findAll()) {

      // Verifica se já existe evolução ou tempo mínimo de progressão
      if (c.getExisteEvolucao() == 0L && c.getAtingiuPrimeiraProgressao() == 0L) {
        LOGGER.debug("Ignorando carreira {} do funcionário {}: sem evolução e sem tempo mínimo de progressão", c.getCarreiraId(), c.getNomeFuncionario());
        continue;
      }

      // Verifica se atingiu tempo mínimo para progressão
      if (c.getAtingiuTempMinProgressao() == 0L) {
        LOGGER.debug("Ignorando carreira {} do funcionário {}: não atingiu tempo mínimo para progressão", c.getCarreiraId(), c.getNomeFuncionario());
        continue;
      }

      // Verifica se a média das avaliações atende ao mínimo para progressão
      var media = c.getMediaAvaliacoes();
      if (media != null && media >= 2.5) {
        LOGGER.debug("Processando carreira {} do funcionário {}: média {} >= 2.5, registrando simulação", c.getCarreiraId(), c.getNomeFuncionario(), media);
        simulacaoService.registarSimulacao(c, media, ProgessionPromotionType.PROGRESSAO);
      } else {
        LOGGER.debug("Ignorando carreira {} do funcionário {}: média {} abaixo do limite", c.getCarreiraId(), c.getNomeFuncionario(), media);
      }
    }
  }
}
