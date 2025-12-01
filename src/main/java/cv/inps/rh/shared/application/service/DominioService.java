package cv.inps.rh.shared.application.service;

import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.infrastructure.persistence.entity.DomainEntity;
import cv.inps.rh.shared.infrastructure.persistence.repository.DomainEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DominioService {

  private final DomainEntityRepository domainEntityRepository;

  public Map<String, String> getTipoMovimentoLaboralDomain() {
    return domainEntityRepository.findByDominioAndEstado("TIPO_MOV_LABORAL", Estado.A)
        .stream()
        .collect(Collectors.toMap(DomainEntity::getValor, DomainEntity::getDescricao));
  }


}
