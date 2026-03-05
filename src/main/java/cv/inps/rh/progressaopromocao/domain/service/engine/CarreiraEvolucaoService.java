package cv.inps.rh.progressaopromocao.domain.service.engine;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CarreiraEvolucaoService {

  private final ProgressaoService progressaoService;
  private final PromocaoService promocaoService;

  @Transactional
  public void executarSimulacao() {
    progressaoService.simular();
    promocaoService.simular();
  }
}
