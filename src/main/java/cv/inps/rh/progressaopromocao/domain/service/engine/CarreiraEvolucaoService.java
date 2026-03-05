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

    // todo: primeiro executar promocao, depois progressao

    // TODO 05/03/2026 17:44 eliminar dados da tabela simulacao evolucao de carreira sempre antes

    progressaoService.simular();
    //promocaoService.simular();
  }
}
