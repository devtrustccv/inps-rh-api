package cv.inps.rh.progressaopromocao.domain.service.engine;

import cv.inps.rh.progressaopromocao.domain.service.engine.model.ProgessionPromotionType;
import cv.inps.rh.progressaopromocao.domain.service.engine.rule.AvaliacaoService;
import cv.inps.rh.progressaopromocao.domain.service.engine.rule.RegraPrimeiraEntradaEfetivoService;
import cv.inps.rh.progressaopromocao.domain.service.engine.rule.RegraTempoProgressaoService;
import cv.inps.rh.shared.infrastructure.persistence.entity.CarreiraEntity;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProgressaoService {

  private static final Logger LOGGER = LoggerFactory.getLogger(ProgressaoService.class);

  private final AvaliacaoService avaliacaoService;
  private final SimulacaoService simulacaoService;
  private final RegraTempoProgressaoService regraTempoProgressaoService;
  private final RegraPrimeiraEntradaEfetivoService regraPrimeiraEntrada;

  public void simular(List<CarreiraEntity> careers) {

    for (var career : careers) {

      // TODO 05/03/2026 17:41 colaborador em licensa sem vencimento por exemplo nao deve progredir
      // TODO 05/03/2026 17:41 O colabordor não deve uma situação laboral na qual não evolui na carreira no intervalo de data que supostamente deverá evoluir. deve iniciar a partir da situacao laboral

      if (!atingiuTempoMinimo(career))
        continue;

      var media = avaliacaoService.calcularMedia(career.getContrVinculoId().getFunId(), 3);
      if (media.elegivelProgressao())
        simulacaoService.registarSimulacao(career, media, ProgessionPromotionType.PROGRESSAO);
    }
  }

  private boolean atingiuTempoMinimo(CarreiraEntity career) {

    var atingiuTempoPrimeiraProgressao = regraPrimeiraEntrada.atingiuTempoPrimeiraProgressao(career);
    LOGGER.debug("ATINGIU TEMPO PRIMEIRA PROGRESSAO: {}", atingiuTempoPrimeiraProgressao);
    if (!atingiuTempoPrimeiraProgressao)
      return false;

    int minimalYears = regraTempoProgressaoService.determinarTempoMinimoProgressao(career);
    LOGGER.debug("TEMPO MINIMO: {}", minimalYears);

    var before = career.getDataInicio()
        .plusYears(minimalYears)
        .isBefore(LocalDate.now().plusDays(1));
    LOGGER.debug("TEMPO MINIMO ATINGIDO: {}, {}, {}", before, career.getDataInicio(), LocalDate.now().plusDays(1));

    return before;
  }
}
