package cv.inps.rh.progressaopromocao.domain.service.engine;

import cv.inps.rh.shared.infrastructure.persistence.repository.CarreiraEntityRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CarreiraEvolucaoService {

  private static final Logger LOGGER = LoggerFactory.getLogger(CarreiraEvolucaoService.class);

  private final CarreiraEntityRepository carreiraRepository;
  private final ProgressaoService progressaoService;
  private final PromocaoService promocaoService;

  @Transactional
  public void executarSimulacao() {

    // todo: primeiro executar promocao, depois progressao

    var careers = carreiraRepository.findCarreirasAtivas();
    LOGGER.info("SIMULANDO PROGRESSÃO PARA {} CARREIRAS", careers.size());

    //promocaoService.simular(careers);
    progressaoService.simular(careers);
  }
}
