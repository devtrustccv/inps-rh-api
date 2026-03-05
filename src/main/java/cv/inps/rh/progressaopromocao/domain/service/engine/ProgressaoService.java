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

    var carreiras = carreiraRepository.findCarreirasAtivas();

    for (var carreira : carreiras) {

      if (!validaRegrasBasicas(carreira) || !atingiuTempoMinimo(carreira)) continue;

      var media = avaliacaoService.calcularMedia(carreira.getContrVinculoId().getFunId(), 3);

      if (!media.elegivelProgressao()) continue;

      simulacaoService.registarSimulacao(carreira, media, "P"); // Progressão
    }
  }

  /**
   * Regras básicas:
   * - Tem contrato associado
   * - Tem funcionário associado
   */
  private boolean validaRegrasBasicas(CarreiraEntity carreira) {

    if (carreira.getContrVinculoId() == null) return false;

    if (carreira.getContrVinculoId().getFunId() == null) return false;

    return carreira.getDataInicio() != null;
  }

  /**
   * Regra dos 3 anos para progressão
   */
  private boolean atingiuTempoMinimo(CarreiraEntity carreira) {

    if (!regraPrimeiraEntrada
        .atingiuTempoPrimeiraProgressao(carreira)) {
      return false;
    }

    if (carreira.getDataInicio() == null) return false;

    int anosMinimos =
        regraTempoProgressaoService
            .determinarTempoMinimoProgressao(carreira);

    return carreira.getDataInicio()
        .plusYears(anosMinimos)
        .isBefore(LocalDate.now());
  }
}
