package cv.inps.rh.progressaopromocao.domain.service.engine;

import cv.inps.rh.shared.infrastructure.persistence.repository.SimEvolucaoCarreiraEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.VwRhProgressaoInputEntityRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CarreiraEvolucaoService {

  private final ProgressaoService progressaoService;
  private final PromocaoService promocaoService;
  private final VwRhProgressaoInputEntityRepository vwRhProgressaoInputEntityRepository;
  private final SimEvolucaoCarreiraEntityRepository simEvolucaoCarreiraEntityRepository;

  @Transactional
  public void executarSimulacao() {

    simEvolucaoCarreiraEntityRepository.deleteAll();

    var promotionCandidates = vwRhProgressaoInputEntityRepository.findAll();
    for (var candidate : promotionCandidates) {
      promocaoService.simular(candidate);
      progressaoService.simular(candidate);
    }
  }
}
