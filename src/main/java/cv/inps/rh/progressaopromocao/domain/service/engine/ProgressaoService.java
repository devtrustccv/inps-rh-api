package cv.inps.rh.progressaopromocao.domain.service.engine;

import cv.inps.rh.progressaopromocao.domain.service.engine.rule.AvaliacaoService;
import cv.inps.rh.progressaopromocao.domain.service.engine.rule.RegraPrimeiraEntradaEfetivoService;
import cv.inps.rh.progressaopromocao.domain.service.engine.rule.RegraTempoProgressaoService;
import cv.inps.rh.shared.infrastructure.persistence.entity.CarreiraEntity;
import cv.inps.rh.shared.infrastructure.persistence.repository.CarreiraEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class ProgressaoService {

  private final CarreiraEntityRepository carreiraRepository;
  private final AvaliacaoService avaliacaoService;
  private final SimulacaoService simulacaoService;
  private final RegraTempoProgressaoService regraTempoProgressaoService;
  private final RegraPrimeiraEntradaEfetivoService regraPrimeiraEntrada;

  public void simular() {

    var careers = carreiraRepository.findCarreirasAtivas();

    for (var career : careers) {

      // TODO 05/03/2026 17:41 colaborador em licensa sem vencimento por exemplo nao deve progredir
      // TODO 05/03/2026 17:41 	O colabordor não deve uma situação laboral na qual não evolui na carreira no intervalo de data que supostamente deverá evoluir. deve iniciar a partir da situacao laboral

      if (!atingiuTempoMinimo(career))
        continue;

      var media = avaliacaoService.calcularMedia(career.getContrVinculoId().getFunId(), 3);
      if (!media.elegivelProgressao())
        continue;

      simulacaoService.registarSimulacao(career, media, "P"); // Progressão todo: create enum for this
    }
  }

  private boolean atingiuTempoMinimo(CarreiraEntity carreira) {

    if (!regraPrimeiraEntrada.atingiuTempoPrimeiraProgressao(carreira))
      return false;

    int anosMinimos = regraTempoProgressaoService.determinarTempoMinimoProgressao(carreira);

    return carreira.getDataInicio()
        .plusYears(anosMinimos)
        .isBefore(LocalDate.now()); // TODO 05/03/2026 15:10 validate if it is Localdate.now().plusdays(1)
  }
}
