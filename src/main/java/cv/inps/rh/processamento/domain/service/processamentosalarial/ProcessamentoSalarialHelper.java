package cv.inps.rh.processamento.domain.service.processamentosalarial;

import cv.inps.rh.shared.infrastructure.persistence.repository.ProcessamentoSalarialEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProcessamentoSalarialHelper {

  private final ProcessamentoSalarialEntityRepository processamentoSalarialEntityRepository;

  @Transactional
  public void atualizarEstado(Long id, String estado) {
    var p = processamentoSalarialEntityRepository.findById(id).orElseThrow();
    p.setEstado(estado);
  }
}
